package com.smartbear.soapui.plugins.search;

import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.support.WorkspaceListenerAdapter;
import com.eviware.soapui.plugins.ListenerConfiguration;

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
@ListenerConfiguration
public class ProjectManager extends WorkspaceListenerAdapter {

    private SoapUISearcher searcher = SearchPlugin.searcherInstance;

    @Override
    public void projectOpened(Project project) {
        searcher.addModelItem(project);
    }

    @Override
    public void projectAdded(Project project) {
        searcher.addModelItem(project);
    }

    @Override
    public void projectClosed(Project project) {
        searcher.removeModelItem(project);
    }

    @Override
    public void projectRemoved(Project project) {
        searcher.removeModelItem(project);
    }
}
