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

import java.net.http.HttpResponse;


/**
 * The request contained valid data and was understood by the server, but the server is refusing action.
 * This may be due to the user not having the necessary permissions for a resource or needing an account,
 * or attempting a prohibited action (e.g. creating a duplicate record where only one is allowed).
 * The request should not be repeated.
 */
public class ForbiddenException extends StatusCodeException {

    /**
     * Constructs a new exception.
     *
     * @param response the response representing the exception
     */
    public ForbiddenException(HttpResponse<String> response) {
        super(response);
    }
}
