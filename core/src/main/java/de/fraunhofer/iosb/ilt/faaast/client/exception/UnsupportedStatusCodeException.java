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

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * This exception is thrown if the server responds with an error code that is not handled by the client.
 */
public class UnsupportedStatusCodeException extends RuntimeException {

    /**
     * Constructs a new exception.
     *
     * @param request the request causing the exception
     * @param response the response representing the exception
     */
    public UnsupportedStatusCodeException(HttpRequest request, HttpResponse<String> response) {
        super("httpMethod='" + request.method() + "',\n" +
                "requestUri='" + request.uri() + "',\n" +
                "ResponseUri='" + response.uri() + "',\n" +
                "statusCode='" + response.statusCode() + "',\n" +
                "requestBody=\n" + request.bodyPublisher().toString() + "',\n" +
                "responseBody=\n" + response.body());
    }
}
