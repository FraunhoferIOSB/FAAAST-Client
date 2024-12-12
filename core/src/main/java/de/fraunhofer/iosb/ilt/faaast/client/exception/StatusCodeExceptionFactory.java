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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class StatusCodeExceptionFactory {

    private StatusCodeExceptionFactory() {}


    /**
     * Creates a new exception based on the status code.
     * 
     * @param status the HTTP status
     * @param request the request
     * @param response the response
     * @return an exception corresponding to the status code
     * @throws UnsupportedStatusCodeException if status code is not supported
     */
    public static StatusCodeException create(HttpStatus status, HttpRequest request, HttpResponse<String> response) {
        return switch (status) {
            case BAD_REQUEST -> new BadRequestException(request, response);
            case UNAUTHORIZED -> new UnauthorizedException(request, response);
            case FORBIDDEN -> new ForbiddenException(request, response);
            case NOT_FOUND -> new NotFoundException(request, response);
            case METHOD_NOT_ALLOWED -> new MethodNotAllowedException(request, response);
            case CONFLICT -> new ConflictException(request, response);
            case INTERNAL_SERVER_ERROR -> new InternalServerErrorException(request, response);
            default -> throw new UnsupportedStatusCodeException(request, response);
        };
    }
}
