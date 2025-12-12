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

import static org.apache.commons.fileupload.FileUploadBase.CONTENT_DISPOSITION;

import de.fraunhofer.iosb.ilt.faaast.client.http.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.util.HttpConstants;
import de.fraunhofer.iosb.ilt.faaast.service.model.InMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.model.TypedInMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.util.Ensure;
import de.fraunhofer.iosb.ilt.faaast.service.util.LambdaExceptionHelper;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * Utility class for sending HTTP requests using HttpClient.
 * Provides methods to send GET, POST, PUT, PATCH, and DELETE requests,
 * and handles the response as a string.
 * This class manages request building and sending,
 * throwing a ConnectivityException in case of failures during the request.
 */
public final class HttpRequestHelper {

    private static final String BOUNDARY = "----ClientBoundary7MA4YWxkTrZu0gW";
    private static final String FILE_PARAMETER = "file";
    private static final String FILENAME_PARAMETER = "fileName";
    private static final String DEFAULT_FILENAME = "unknown";
    private static final String AUTHORIZATION = "Authorization";

    private HttpRequestHelper() {}


    /**
     * Creates a GET request to the specified URI.
     *
     * @param uri the target URI to send the GET request to
     * @param authHeader Additional authentication header
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createGetRequest(URI uri, String authHeader) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri).GET();
        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Creates a POST request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the POST request to
     * @param authHeader Additional authentication header
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createPostRequest(URI uri, String authHeader, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header(HttpConstants.HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Sends a PUT request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the PUT request to
     * @param authHeader Additional authentication header
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createPutRequest(URI uri, String authHeader, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header(HttpConstants.HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Creates a PUT request for files to the specified URI.
     *
     * @param uri the target URI to send the GET request to
     * @param authHeader Additional authentication header
     * @param file File to send
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createPutFileRequest(URI uri, String authHeader, TypedInMemoryFile file) {
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody(FILENAME_PARAMETER, file.getPath())
                .addBinaryBody(FILE_PARAMETER,
                        file.getContent(),
                        ContentType.create(file.getContentType(), StandardCharsets.UTF_8),
                        file.getPath())
                .setBoundary(BOUNDARY)
                .build();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpConstants.HEADER_CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA.getMimeType() + "; boundary=" + BOUNDARY)
                .PUT(HttpRequest.BodyPublishers.ofInputStream(LambdaExceptionHelper.wrap(httpEntity::getContent)));

        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Creates a PATCH request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the PATCH request to
     * @param authHeader Additional authentication header
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createPatchRequest(URI uri, String authHeader, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .method(HttpMethod.PATCH.name(), HttpRequest.BodyPublishers.ofString(body))
                .header(HttpConstants.HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Creates a DELETE request to the specified URI.
     *
     * @param uri the target URI to send the DELETE request to
     * @param authHeader Additional authentication header
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createDeleteRequest(URI uri, String authHeader) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri).DELETE();

        decorate(requestBuilder, authHeader);

        return requestBuilder.build();
    }


    /**
     * Sends the provided HttpRequest and returns the HttpResponse containing a string body Handles any IOException or
     * InterruptedException by throwing a ConnectivityException.
     *
     * @param httpClient the client to use
     * @param request the HttpRequest to be sent
     * @return the HttpResponse containing the response body as a string
     * @throws ConnectivityException if a connectivity error occurs during the request
     */
    public static HttpResponse<String> send(HttpClient httpClient, HttpRequest request) throws ConnectivityException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            throw new ConnectivityException(e);
        }
    }


    /**
     * Sends the provided HttpRequest and returns the HttpResponse containing a byte array body Handles any IOException or
     * InterruptedException by throwing a
     * ConnectivityException.
     *
     * @param httpClient the client to use
     * @param request the HttpRequest to be sent
     * @return the HttpResponse containing the response body as a string
     * @throws ConnectivityException if a connectivity error occurs during the request
     */
    public static HttpResponse<byte[]> sendFileRequest(HttpClient httpClient, HttpRequest request) throws ConnectivityException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        }
        catch (IOException e) {
            throw new ConnectivityException(e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectivityException("Request interrupted", e);
        }
    }


    /**
     * Parses HTTP response to TypedInMemoryFile.
     *
     * @param httpResponse HTTP response
     * @return deserialized payload
     */
    public static InMemoryFile parseBody(HttpResponse<byte[]> httpResponse) {
        Ensure.requireNonNull(httpResponse, "httpResponse must be non-null");
        String contentDispositionHeader = httpResponse.headers().firstValue(CONTENT_DISPOSITION).orElse(DEFAULT_FILENAME);
        return new InMemoryFile.Builder()
                .content(httpResponse.body())
                .path(extractName(contentDispositionHeader)).build();
    }


    private static void decorate(HttpRequest.Builder requestBuilder, String authenticationHeaderValue) {
        if (authenticationHeaderValue != null) {
            requestBuilder.header(AUTHORIZATION, authenticationHeaderValue);
        }
    }


    private static String extractName(String contentDispositionHeader) {
        ParameterParser parser = new ParameterParser();
        Map<String, String> params = parser.parse(contentDispositionHeader, ';');

        return params.getOrDefault(FILENAME_PARAMETER, DEFAULT_FILENAME);
    }
}
