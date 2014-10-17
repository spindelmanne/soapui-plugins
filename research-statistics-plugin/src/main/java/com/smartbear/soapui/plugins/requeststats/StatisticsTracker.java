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
import com.eviware.soapui.impl.rest.RestRequestInterface;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class StatisticsTracker {

    private Map<RestRequestInterface, Long> responseCountsByRequest = new HashMap<>();
    private Map<RestRequestInterface, Long> maxResponseTimesByRequest = new HashMap<>();
    private Map<RestRequestInterface, BigInteger> totalResponseTimesByRequest = new HashMap<>();

    public synchronized void registerRequestStatistics(RestRequestInterface restRequest) {
        long timeTaken = restRequest.getResponse().getTimeTaken();
        BigInteger previousTotalResponseTime = totalResponseTimesByRequest.get(restRequest);
        if (previousTotalResponseTime == null) {
            previousTotalResponseTime = new BigInteger(String.valueOf(timeTaken));
            totalResponseTimesByRequest.put(restRequest, previousTotalResponseTime);
        }
        else {
            BigInteger totalResponseTime = previousTotalResponseTime.add(new BigInteger(String.valueOf(timeTaken)));
            totalResponseTimesByRequest.put(
                    restRequest,
                    totalResponseTime);
        }
        responseCountsByRequest.put(restRequest,
                longValue(responseCountsByRequest.get(restRequest)) + 1);
        long previousMaxResponseTime = longValue(maxResponseTimesByRequest.get(restRequest));
        maxResponseTimesByRequest.put(restRequest, Math.max(timeTaken, previousMaxResponseTime));
    }

    public SubmitStatistics getStatisticsFor(RestRequest restRequest) {
        return new SubmitStatistics(longValue(responseCountsByRequest.get(restRequest)),
                longValue(maxResponseTimesByRequest.get(restRequest)),
                getAverageResponseTime(restRequest));
    }

    private long longValue(Number number) {
        return number == null ? 0 : number.longValue();
    }

    private int getAverageResponseTime(RestRequest restRequest) {
        long requestCount = longValue(
                responseCountsByRequest.get(restRequest));
        if (requestCount == 0) {
            return 0;
        }
        else {
            return (int)Math.round(((double)longValue(totalResponseTimesByRequest.get(restRequest))) / requestCount);
        }
    }
}
