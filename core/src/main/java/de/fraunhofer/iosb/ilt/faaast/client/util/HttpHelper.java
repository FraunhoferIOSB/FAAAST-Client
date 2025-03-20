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

import de.fraunhofer.iosb.ilt.faaast.client.http.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Utility class for sending HTTP requests using HttpClient.
 * Provides methods to send GET, POST, PUT, PATCH, and DELETE requests,
 * and handles the response as a string.
 * This class wraps HttpClient and manages request building and sending,
 * throwing a ConnectivityException in case of failures during the request.
 */
public final class HttpHelper {

    private HttpHelper() {}


    /**
     * Creates a new default HTTP client.
     *
     * @return the new HTTP client
     */
    public static HttpClient newDefaultClient() {
        return HttpClient.newHttpClient();
    }


    /**
     * Creates a new HTTP client with basic username/password authentication.
     *
     * @param username the username
     * @param password the password
     * @return the new HTTP client
     */
    public static HttpClient newUsernamePasswordClient(String username, String password) {
        return HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                }).build();
    }


    /**
     * Creates a new HTTP client that trusts all certificates (including self-signed ones).
     *
     * @return the new HTTP client
     */
    public static HttpClient newTrustAllCertificatesClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}


                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}


                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new java.security.SecureRandom());
            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("failed to create HTTP client that trusts all certificates", e);
        }
    }


    /**
     * Creates a GET request to the specified URI.
     *
     * @param uri the target URI to send the GET request to
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).GET().build();
    }


    /**
     * Creates a POST request to the specified URI with the provided request body.
     *
     * @param uri the target URI to send the POST request to
     * @param body the request body as a string
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createPostRequest(URI uri, String body) {
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
    public static HttpRequest createPutRequest(URI uri, String body) {
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
    public static HttpRequest createPatchRequest(URI uri, String body) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .method(HttpMethod.PATCH.name(), HttpRequest.BodyPublishers.ofString(body))
                .build();
    }


    /**
     * Creates a DELETE request to the specified URI.
     *
     * @param uri the target URI to send the DELETE request to
     * @return the HttpResponse containing the response body as a string
     */
    public static HttpRequest createDeleteRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).DELETE().build();
    }


    /**
     * Sends the provided HttpRequest and returns the HttpResponse.
     * Handles any IOException or InterruptedException by throwing
     * a ConnectivityException.
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
}
