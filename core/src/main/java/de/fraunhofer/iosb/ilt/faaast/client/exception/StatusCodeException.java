/*
 * Copyright (c) 2024 Fraunhofer IOSB, eine rechtlich nicht selbstaendige
 * Einrichtung der Fraunhofer-Gesellschaft zur Foerderung der angewandten
 * Forschung e.V.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.iosb.ilt.faaast.client.exception;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Parent exception for different status codes.
 */
public abstract class StatusCodeException extends ClientException {

    private final URI serviceUri;
    private final int statusCode;

    /**
     * Constructor.
     *
     * @param request The http Request
     * @param response The http Response
     */
    protected StatusCodeException(HttpRequest request, HttpResponse<String> response) {
        super("httpMethod='" + request.method() + "',\n" +
                "requestUri='" + request.uri() + "',\n" +
                "ResponseUri='" + response.uri() + "',\n" +
                "statusCode='" + response.statusCode() + "',\n" +
                "requestBody=\n" + request.bodyPublisher().toString() + "',\n" +
                "responseBody=\n" + response.body());
        this.serviceUri = response.uri();
        this.statusCode = response.statusCode();
    }


    /**
     * The URI that generated the failure response.
     *
     * @return the URI that generated the failure response
     */
    public URI getServiceUri() {
        return serviceUri;
    }


    /**
     * The status code returned by the server.
     *
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }
}
