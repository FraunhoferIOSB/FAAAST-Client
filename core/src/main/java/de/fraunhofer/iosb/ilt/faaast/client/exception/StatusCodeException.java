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
import java.net.http.HttpResponse;


/**
 * Parent exception for different status codes.
 */
public abstract class StatusCodeException extends ClientException {

    private final URI uri;
    private final int statusCode;
    private final String body;

    /**
     * Constructs a new exception.
     *
     * @param response the response representing the exception
     */
    protected StatusCodeException(HttpResponse<?> response) {
        this(response.uri(), response.statusCode(), (response.body() instanceof String) ? (String) response.body() : null);
    }


    /**
     * Constructs a new exception.
     *
     * @param uri the uri called
     * @param statusCode the status code received
     * @param body the body of the response
     */
    protected StatusCodeException(URI uri, int statusCode, String body) {
        super(String.format("Received HTTP status code %d (uri: %s uri, response body: %s)", statusCode, uri, body != null ? body : "not available"));
        this.uri = uri;
        this.statusCode = statusCode;
        this.body = body;
    }


    /**
     * The URI that generated the failure response.
     *
     * @return the URI that generated the failure response
     */
    public URI getUri() {
        return uri;
    }


    /**
     * The status code returned by the server.
     *
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }


    /**
     * The body of the response.
     *
     * @return the body of the response
     */
    public String getBody() {
        return body;
    }
}
