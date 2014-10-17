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

import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;

@ActionConfiguration(actionGroup = "RestRequestActions")
public class ShowStatisticsAction extends AbstractSoapUIAction<RestRequest> {

    private StatisticsTracker tracker = RequestStatisticsPlugin.getTracker();


    public ShowStatisticsAction() {
        super("Show Request Statistics", "Displays simple statistics for this REST request");
    }

    @Override
    public void perform(RestRequest restRequest, Object o) {
        SubmitStatistics statistics = tracker.getStatisticsFor(restRequest);
        String message = String.format("Request submitted : %d\n" +
                "Maximum response time: %d\n" +
                "Average response time: %.0f",
                statistics.getNumberOfRequests(), statistics.getMaxResponseTime(), statistics.getAverageResponseTime());
        UISupport.showInfoMessage(message);
    }
}
