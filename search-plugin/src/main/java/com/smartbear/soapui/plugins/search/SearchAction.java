package com.smartbear.soapui.plugins.search;

import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;

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
@ActionConfiguration(actionGroup = "WorkspaceImplActions", description = "Search projects ...", keyStroke = "menu F")
public class SearchAction extends AbstractSoapUIAction<WorkspaceImpl>{

    public SearchAction() {
        super("Search Projects", "Free text search in all projects");
    }

    @Override
    public void perform(WorkspaceImpl t, Object o) {
        new SearchDialog(SearchPlugin.searcherInstance).display();
    }
}
