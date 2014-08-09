package com.smartbear.soapui.plugins.search

import com.eviware.soapui.impl.rest.RestResource
import com.eviware.soapui.model.ModelItem
import com.eviware.soapui.utils.ModelItemFactory
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.not
import static com.eviware.soapui.utils.CommonMatchers.anEmptyCollection
import static org.junit.matchers.JUnitMatchers.hasItem

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
*/ class LuceneSoapUISearcherTest {

    private LuceneSoapUISearcher searcher = new LuceneSoapUISearcher()
    private RestResource restResource

    @Before
    public void setUp() throws Exception {
        restResource = ModelItemFactory.makeRestResource()
        restResource.setPropertyValue("custom", "prio")
        searcher.addModelItem(restResource)
    }

    @Test
    public void findsAddedModelItem() throws Exception {
        def result = searcher.search('pri')
        assertThat result.numberOfHits, is(1)
        List<ModelItem> hits = result.topHits
        assertThat hits, is(not(anEmptyCollection()))
        assertThat hits[0].id, is(restResource.id)
    }

    @Test
    public void replacesModelItemWhenAddingAgain() throws Exception {
        searcher.addModelItem(restResource)
        def result = searcher.search('pri')
        assertThat result.numberOfHits, is(1)
    }

    @Test
    public void addsChildrenOfModelItem() throws Exception {
        def restMethod = restResource.addNewMethod("Galax")
        searcher.addModelItem(restResource)
        assertThat searcher.search('Galax').topHits, hasItem(restMethod)
    }

    @Test
    public void canRemoveModelItem() throws Exception {
        searcher.removeModelItem(restResource)
        assertThat searcher.search('pri').numberOfHits, is(0)
    }

    @Test
    public void removesChildrenOfModelItem() throws Exception {
        def restMethod = restResource.addNewMethod("Galax")
        searcher.addModelItem(restResource)
        searcher.removeModelItem(restResource)
        assertThat searcher.search('Galax').topHits, not(hasItem(restMethod))
    }

    @Test
    public void disregardsOneOrTwoCharacterSearches() throws Exception {
        assertThat searcher.search('p').numberOfHits, is(0)
        assertThat searcher.search('pr').numberOfHits, is(0)
    }

    @Test
    public void handlesSearchesWithLuceneTokens() throws Exception {
        def stringWithLuceneTokens = "+-&&||!(){}[]^\"~*?:\\"
        // just verify that this doesn't crash
        searcher.search(stringWithLuceneTokens)
    }
}
