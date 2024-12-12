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
 * A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.
 */
public class InternalServerErrorException extends StatusCodeException {

    /**
     * Constructs a new exception.
     *
     * @param response the response representing the exception
     */
    public InternalServerErrorException(HttpResponse<String> response) {
        super(response);
    }
}
