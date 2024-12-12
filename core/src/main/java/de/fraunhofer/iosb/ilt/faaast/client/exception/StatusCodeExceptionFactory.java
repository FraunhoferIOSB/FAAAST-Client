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

import de.fraunhofer.iosb.ilt.faaast.client.http.HttpStatus;
import java.net.http.HttpResponse;
import java.util.Objects;


public class StatusCodeExceptionFactory {

    private StatusCodeExceptionFactory() {}


    /**
     * Creates a new exception based on the status code.
     * 
     * @param status the HTTP status
     * @param response the response
     * @return an exception corresponding to the status code
     * @throws UnsupportedStatusCodeException if status code is not supported
     */
    public static StatusCodeException create(HttpResponse<String> response) {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("response must be non-null");
        }
        try {
            return switch (HttpStatus.from(response.statusCode())) {
                case BAD_REQUEST -> new BadRequestException(response);
                case UNAUTHORIZED -> new UnauthorizedException(response);
                case FORBIDDEN -> new ForbiddenException(response);
                case NOT_FOUND -> new NotFoundException(response);
                case METHOD_NOT_ALLOWED -> new MethodNotAllowedException(response);
                case CONFLICT -> new ConflictException(response);
                case INTERNAL_SERVER_ERROR -> new InternalServerErrorException(response);
                default -> throw new UnsupportedStatusCodeException(response);
            };
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedStatusCodeException(response);
        }
    }
}
