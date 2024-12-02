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

import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Utility class for sending HTTP requests using HttpClient.
 * Provides methods to send GET, POST, PUT, PATCH, and DELETE requests,
 * and handles the response as a string.
 * This class wraps HttpClient and manages request building and sending,
 * throwing a ConnectivityException in case of failures during the request.
 */
public final class HttpClientUtility {

    private final HttpClient httpClient;

    /**
     * Constructs a new HttpClientUtility with the given HttpClient.
     *
     * @param httpClient the HttpClient instance to be used for sending requests
     */
    public HttpClientUtility(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    /**
     * Creates a GET request to the specified URI.
     *
     * @param uri the target URI to send the GET request to
     * @return the HttpResponse containing the response body as a string
     */
    public HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).GET().build();
    }


    /**
     * Creates a POST request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the POST request to
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public HttpRequest createPostRequest(URI uri, String body) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }


    /**
     * Sends a PUT request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the PUT request to
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public HttpRequest createPutRequest(URI uri, String body) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }


    /**
     * Creates a PATCH request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the PATCH request to
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public HttpRequest createPatchRequest(URI uri, String body) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
    }


    /**
     * Creates a DELETE request to the specified URI.
     *
     * @param uri the target URI to send the DELETE request to
     * @return the HttpResponse containing the response body as a string
     */
    public HttpRequest createDeleteRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).DELETE().build();
    }


    /**
     * Sends the provided HttpRequest and returns the HttpResponse.
     * Handles any IOException or InterruptedException by throwing
     * a ConnectivityException.
     *
     * @param request the HttpRequest to be sent
     * @return the HttpResponse containing the response body as a string
     * @throws ConnectivityException if a connectivity error occurs during the request
     */
    public HttpResponse<String> send(HttpRequest request) throws ConnectivityException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            throw new ConnectivityException(e);
        }
    }
}
