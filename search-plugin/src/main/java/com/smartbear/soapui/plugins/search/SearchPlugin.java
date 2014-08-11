package com.smartbear.soapui.plugins.search;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

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
@PluginConfiguration(groupId = "com.smartbear.soapui.plugins", name = "Search Plugin", version = "0.1")
public class SearchPlugin extends PluginAdapter {


    public static final SoapUISearcher searcherInstance = new LuceneSoapUISearcher();

    @Override
    public void initialize() {
        SoapUI.log.debug("Initializing Search plugin");
        new Thread(new Runnable() {

            @Override
            public void run() {
                Workspace workspace = null;
                while (workspace == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {

                    }
                    workspace = SoapUI.getWorkspace();
                }
                for (Project project : workspace.getProjectList()) {
                    if (project.isOpen()) {
                        SoapUI.log.debug("Adding project " + project.getName() + " to search index");
                        searcherInstance.addModelItem(project);
                    }
                }

            }
        }).start();
    }
}
