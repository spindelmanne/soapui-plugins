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
package com.smartbear.soapui.plugins.requeststats;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.submit.RequestTransportRegistry;
import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "com.smartbear.soapui.plugins", name = "Request Statistics Plugin", version = "0.1")
public class RequestStatisticsPlugin extends PluginAdapter {

    private static final StatisticsTracker tracker = new StatisticsTracker();

    public static StatisticsTracker getTracker() {
        return tracker;
    }

    @Override
    public void initialize() {
        try {
            RequestTransportRegistry.getTransport("http").addRequestFilter(new RequestStatisticsListener(tracker));
        } catch (RequestTransportRegistry.MissingTransportException e) {
            SoapUI.logError(e, "Can't find HTTP Transport!");
        }
    }
}
