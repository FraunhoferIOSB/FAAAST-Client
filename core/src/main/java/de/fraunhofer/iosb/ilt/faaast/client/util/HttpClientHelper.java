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

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Utility class for creating a customized java.net.HttpClient.
 */
public final class HttpClientHelper {

    private HttpClientHelper() {}


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
     * Creates a new HTTP client with basic username/password authentication.
     *
     * @param httpClientBuilder Builder to add basic auth to
     * @param username the username
     * @param password the password
     */
    public static void addBasicAuthentication(HttpClient.Builder httpClientBuilder, String username, String password) {
        httpClientBuilder
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
    }


    /**
     * Creates a new HTTP client that trusts all certificates (including self-signed ones).
     *
     * @return the new HTTP client
     */
    public static HttpClient newTrustAllCertificatesClient() {
        return HttpClient.newBuilder()
                .sslContext(trustAllSslContext())
                .build();
    }


    /**
     * Adds "trust-all-certificates" to a HttpClient builder.
     *
     * @param httpClientBuilder Builder to allow all certificates
     */
    public static void makeTrustAllCertificates(HttpClient.Builder httpClientBuilder) {
        httpClientBuilder.sslContext(trustAllSslContext());
    }


    private static SSLContext trustAllSslContext() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // intentionally empty
                        }


                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // intentionally empty
                        }


                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new java.security.SecureRandom());
        }
        catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException("failed to create HTTP client that trusts all certificates", e);
        }

        return sslContext;
    }
}
