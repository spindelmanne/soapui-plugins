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
import com.eviware.soapui.impl.rest.RestRequestInterface;
import com.eviware.soapui.impl.wsdl.submit.filters.AbstractRequestFilter;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.plugins.ListenerConfiguration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

@ListenerConfiguration
public class RequestStatisticsListener extends AbstractRequestFilter {

    private StatisticsTracker statisticsTracker;
    private Set<RestRequestInterface> trackedRequests = new HashSet<>();

    public RequestStatisticsListener(StatisticsTracker statisticsTracker) {
        this.statisticsTracker = statisticsTracker;
    }

    @Override
    public void filterRestRequest(SubmitContext context, final RestRequestInterface request) {
        if (!trackedRequests.contains(request)) {
            request.addPropertyChangeListener(RestRequestInterface.RESPONSE_PROPERTY, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    SoapUI.log.debug("Received response for " + request);
                    Response response = (Response) evt.getNewValue();
                    if (response != null) {
                        statisticsTracker.registerRequestStatistics(request);
                    }
                }
            });
            trackedRequests.add(request);
        }
    }
}
