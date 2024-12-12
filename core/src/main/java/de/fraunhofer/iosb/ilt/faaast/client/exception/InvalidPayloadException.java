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

/**
 * This exception is thrown if the server responds with a body that cannot be deserialized.
 */
public class InvalidPayloadException extends RuntimeException {

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public InvalidPayloadException(Throwable cause) {
        super(cause);
    }


    /**
     * Constructs a new exception.
     *
     * @param message the message
     * @param cause the cause of the exception
     */
    public InvalidPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
