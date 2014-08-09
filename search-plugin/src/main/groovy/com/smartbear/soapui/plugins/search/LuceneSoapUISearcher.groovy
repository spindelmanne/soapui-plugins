package com.smartbear.soapui.plugins.search

import com.eviware.soapui.SoapUIPro
import com.eviware.soapui.config.ModelItemConfig
import com.eviware.soapui.impl.wsdl.AbstractWsdlModelItem
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.model.ModelItem
import com.eviware.soapui.model.project.Project
import com.eviware.soapui.model.testsuite.TestSuite
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilderFactory
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/*
 * Copyright 2004-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
*/
class LuceneSoapUISearcher implements SoapUISearcher {
    private static final String MODEL_ITEM_ID_FIELD = "id"
    private static final String CONTENTS_FIELD = "contents"

    private RAMDirectory index
    private DocumentBuilderFactory documentBuilderFactory
    private StandardAnalyzer analyzer
    private Map<String, ModelItem> modelItems = [:]

    LuceneSoapUISearcher() {
        analyzer = new StandardAnalyzer(Version.LUCENE_40)
        index = new RAMDirectory()
        documentBuilderFactory = DocumentBuilderFactory.newInstance()
    }

    @Override
    void addModelItem(ModelItem modelItem) {
        IndexWriter indexWriter = makeIndexWriter()
        if (modelItems.containsKey(modelItem.id)) {
            doRemoveModelItem(modelItem, indexWriter)
        }
        try {
            doAddModelItem(modelItem, indexWriter)
        } finally {
            indexWriter.close();
        }
    }

    private void doAddModelItem(ModelItem modelItem, IndexWriter indexWriter) {
        Document doc = new Document();
        doc.add(new TextField(MODEL_ITEM_ID_FIELD, modelItem.id, Field.Store.YES));
        def text = extractText(modelItem)
        doc.add(new TextField(CONTENTS_FIELD, text, Field.Store.YES));
        indexWriter.addDocument(doc);
        modelItems[modelItem.id] = modelItem
        modelItem.addPropertyChangeListener(IndexUpdater.instance)
        for (ModelItem child : modelItem.children) {
            doAddModelItem(child, indexWriter)
        }
    }

    @Override
    void removeModelItem(ModelItem modelItem) {
        IndexWriter indexWriter = makeIndexWriter()
        try {
            doRemoveModelItem(modelItem, indexWriter)
        } finally {
            indexWriter.close();
        }
    }

    private void doRemoveModelItem(ModelItem modelItem, IndexWriter indexWriter) {
        indexWriter.deleteDocuments(parseQuery(modelItem.id, MODEL_ITEM_ID_FIELD))
        modelItems.remove(modelItem.id)
        for (ModelItem child : modelItem.children) {
            doRemoveModelItem(child, indexWriter)
        }
    }

    @Override
    SearchResult search(String searchString) {
        if (!searchString || searchString.trim().length() < 3) {
            return new SearchResult(0, [])
        }
        def queryString = sanitizeString(searchString.trim()) + "*"
        Query query = parseQuery(queryString, CONTENTS_FIELD)
        IndexReader reader = IndexReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits(), true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        List<ModelItem> topHits = hits.collect() { modelItems[searcher.doc(it.doc).get(MODEL_ITEM_ID_FIELD)] }
        return new SearchResult(collector.totalHits, topHits)
    }

    private String sanitizeString(String searchString) {
        StringBuilder output = new StringBuilder()
        def specialCharactersString = "\\+-&|!(){}[]^\"~*?:"
        searchString.each { inputChar ->
            if (specialCharactersString.contains(inputChar)) {
                output.append('\\')
            }
            output.append(inputChar)
        }
        return output.toString()
    }

    private IndexWriter makeIndexWriter() { new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_40, analyzer)) }

    private Query parseQuery(String queryString, String field) { new QueryParser(Version.LUCENE_40, field, analyzer).parse(queryString) }

    int maxHits() {
        try {
            Integer.parseInt(SoapUIPro.soapUIProCore.settings.getString(SearchPluginPrefs.NUMBER_OF_HITS_SETTING, "10"));
        } catch (NumberFormatException ignored) {
            10
        }
    }

    private String extractText(ModelItem modelItem) {
        switch (modelItem.getClass()) {
            case Project:
            case WsdlInterface:
            case TestSuite:
                return "$modelItem.name $modelItem.description"
            case AbstractWsdlModelItem:
                ModelItemConfig config = modelItem.config
                // not very elegant, but works ...
                def xmlDocument = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(
                        new StringReader(config.xmlText())))
                return extractNodeText(xmlDocument.firstChild)
            default:
                return modelItem.name
        }
    }

    private String extractNodeText(org.w3c.dom.Node node) {
        switch (node.nodeType) {
            case org.w3c.dom.Node.TEXT_NODE:
            case org.w3c.dom.Node.ATTRIBUTE_NODE:
            case org.w3c.dom.Node.CDATA_SECTION_NODE:
                return node.nodeValue.trim()
            case org.w3c.dom.Node.ELEMENT_NODE:
                return getAttributeValues(node) + ' ' +
                        node.childNodes.collect({ childNode -> extractNodeText(childNode) }).join(' ')
            default:
                return ""

        }
    }

    private static String getAttributeValues(org.w3c.dom.Node node) {
        StringBuilder buf = new StringBuilder();
        def attributes = node.attributes
        for (i in 0..(attributes.length - 1)) {
            if (i > 0) {
                buf.append(' ')
            }
            buf.append(attributes.item(i)?.nodeValue ?: '')
        }
        buf.toString()
    }

}

class IndexUpdater implements PropertyChangeListener {

    static final IndexUpdater instance = new IndexUpdater()

    private IndexUpdater() { }

    @Override
    void propertyChange(PropertyChangeEvent evt) {
        if (evt.source instanceof ModelItem) {
            SearchPlugin.searcherInstance.addModelItem((ModelItem)evt.source)
        }
    }
}
