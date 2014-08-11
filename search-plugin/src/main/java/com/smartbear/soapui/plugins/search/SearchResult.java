package com.smartbear.soapui.plugins.search;

import com.eviware.soapui.model.ModelItem;

import java.util.List;

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
class SearchResult {

    private final int numberOfHits;
    private final List<ModelItem> topHits;

    SearchResult(int numberOfHits, List<ModelItem> topHits) {
        this.numberOfHits = numberOfHits;
        this.topHits = topHits;
    }

    int getNumberOfHits() {
        return numberOfHits;
    }

    List<ModelItem> getTopHits() {
        return topHits;
    }
}
