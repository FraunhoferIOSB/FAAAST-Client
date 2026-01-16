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
package de.fraunhofer.iosb.ilt.faaast.client.http;

import java.util.stream.Stream;


/**
 * Enum describing the relevant HTTP status codes.
 */
public enum HttpStatus {
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    SEE_OTHER(303),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    REQUEST_TIMEOUT(408),
    CONFLICT(409),
    UNSUPPORTED_MEDIA_TYPE(415),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int code;

    private HttpStatus(int code) {
        this.code = code;
    }


    /**
     * Gets the HTTP status code.
     *
     * @return the HTTP status code
     */
    public int getCode() {
        return code;
    }


    /**
     * Checks if this status represents success, i.e., is of form 2xx.
     *
     * @return true if status represents success, otherwise false
     */
    public boolean isSuccess() {
        return 200 <= code && code <= 299;
    }


    /**
     * Checks if this status represents a redirection, i.e., is of form 3xx.
     *
     * @return true if status represents a redirection, otherwise false
     */
    public boolean isRedirection() {
        return 300 <= code && code <= 399;
    }


    /**
     * Checks if this status represents a client error, i.e., is of form 4xx.
     *
     * @return true if status represents a client error, otherwise false
     */
    public boolean isClientError() {
        return 400 <= code && code <= 499;
    }


    /**
     * Checks if this status represents a server error, i.e., is of form 5xx.
     *
     * @return true if status represents a server error, otherwise false
     */
    public boolean isServerError() {
        return 500 <= code && code <= 599;
    }


    /**
     * Checks if this status represents a retryable error.
     *
     * @return true if status is retryable, otherwise false
     */
    public boolean isRetryable() {
        if (isServerError()) {
            return true;
        }

        return this == REQUEST_TIMEOUT;
    }


    /**
     * Creates a {@link HttpStatus} based on the underlying HTTP status code.
     *
     * @param code the HTTP status code
     * @return the corresponding HTTP status
     * @throws IllegalArgumentException if the status code is unsupported
     */
    public static HttpStatus from(int code) {
        return Stream.of(HttpStatus.values())
                .filter(x -> x.code == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unknown HTTP status (code: %s)",
                        code)));
    }
}
