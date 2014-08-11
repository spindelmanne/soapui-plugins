package com.smartbear.soapui.plugins.search

import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.model.ModelItem

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
*/ class MockSearcher implements SoapUISearcher {

    @Override
    void addModelItem(ModelItem modelItem) {

    }

    @Override
    void removeModelItem(ModelItem modelItem) {

    }

    @Override
    SearchResult search(String s) {
        return new SearchResult(50, (1..20).collect { new WsdlProject(name: "Project $it") })
    }

    @Override
    int maxHits() { 20 }

    public static void main(String[] args) {
        new SearchDialog(new MockSearcher()).display()
    }

}
