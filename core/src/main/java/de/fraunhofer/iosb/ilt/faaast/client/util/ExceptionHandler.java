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
package de.fraunhofer.iosb.ilt.faaast.client.util;

import de.fraunhofer.iosb.ilt.faaast.client.exception.BadRequestException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ForbiddenException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.NotFoundException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.UnauthorizedException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.InternalServerErrorException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.UnsupportedStatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.MethodNotAllowedException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConflictException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public final class ExceptionHandler {

    private ExceptionHandler() {}


    public static StatusCodeException handleException(HttpMethod httpMethod, HttpRequest request, HttpResponse<String> response) {
        return switch (httpMethod) {
            case GET, PUT, PATCH, DELETE -> handleCommonException(request, response);
            case POST -> handlePostException(request, response);
        };
    }


    private static StatusCodeException handleCommonException(HttpRequest request, HttpResponse<String> response) {
        return switch (response.statusCode()) {
            case 400 -> new BadRequestException(request, response);
            case 401 -> new UnauthorizedException(request, response);
            case 403 -> new ForbiddenException(request, response);
            case 404 -> new NotFoundException(request, response);
            case 500 -> new InternalServerErrorException(request, response);
            default -> throw new UnsupportedStatusCodeException(request, response);
        };
    }


    private static StatusCodeException handlePostException(HttpRequest request, HttpResponse<String> response) {
        return switch (response.statusCode()) {
            case 400 -> new BadRequestException(request, response);
            case 401 -> new UnauthorizedException(request, response);
            case 403 -> new ForbiddenException(request, response);
            case 404 -> new NotFoundException(request, response);
            case 405 -> new MethodNotAllowedException(request, response);
            case 409 -> new ConflictException(request, response);
            case 500 -> new InternalServerErrorException(request, response);
            default -> throw new UnsupportedStatusCodeException(request, response);
        };
    }
}
