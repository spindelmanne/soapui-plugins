package com.smartbear.soapui.plugins.search

import com.eviware.soapui.model.ModelItem
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.support.swing.ModelItemListCellRenderer
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent

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
class SearchDialog {

    private SoapUISearcher searcher
    private SwingBuilder swingBuilder = new SwingBuilder()
    private JList<ModelItem> resultList
    private JLabel summaryLabel
    private JDialog searchDialog

    private def keepDialogInFront = {
        while (true) {
            if (searchDialog.visible && !searchDialog.hasFocus()) {
                swingBuilder.doLater { searchDialog.toFront()}
            }
            sleep(50)
        }
    }

    SearchDialog(SoapUISearcher searcher) {
        this.searcher = searcher
    }

    void display() {
        if (!searchDialog) {
            createDialog()
        }
        searchDialog.setVisible(true)
    }

    private void createDialog() {
        searchDialog = swingBuilder.dialog(bounds: [400, 400, 350, 400]) {
            borderLayout()
            panel(constraints: BorderLayout.NORTH) {
                boxLayout(axis: BoxLayout.Y_AXIS)
                label text: 'Type search string, then hit Enter:'
                textField columns: 30, actionPerformed: this.&processSearch
            }
            scrollPane(constraints: BorderLayout.CENTER) {
                resultList = list(mouseClicked: this.&processItemClick, cellRenderer: new ModelItemListCellRenderer())
            }
            summaryLabel = label(constraints: BorderLayout.SOUTH)
        }
        new Thread(keepDialogInFront).start()
    }

    private void processItemClick(MouseEvent event) {
        if (event.clickCount == 2) {
            int clickedIndex = resultList.locationToIndex(event.point)
            ModelItem clickedOption = resultList.model[clickedIndex]
            if (clickedOption) {
                UISupport.selectAndShow(clickedOption)
            }
        }
    }

    private void processSearch(ActionEvent event) {
        def searchText = ((JTextField) event.source).text
        def searchResult = searcher.search(searchText)
        def model = new DefaultListModel()
        searchResult.topHits.each {
            model.addElement(it)
        }
        resultList.model = model
        def hitCount = searchResult.numberOfHits
        def maxHits = searcher.maxHits()
        summaryLabel.text = hitCount <= maxHits ? "$hitCount items found" : "Showing $maxHits of $hitCount items"
    }

}



