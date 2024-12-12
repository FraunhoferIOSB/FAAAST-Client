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
 * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been
 * provided.
 * The response must include a WWW-Authenticate header field containing a challenge applicable to the requested
 * resource.
 */
public class UnauthorizedException extends StatusCodeException {

    /**
     * Constructs a new exception.
     *
     * @param request the request causing the exception
     * @param response the response representing the exception
     */
    public UnauthorizedException(HttpRequest request, HttpResponse<String> response) {
        super(request, response);
    }
}
