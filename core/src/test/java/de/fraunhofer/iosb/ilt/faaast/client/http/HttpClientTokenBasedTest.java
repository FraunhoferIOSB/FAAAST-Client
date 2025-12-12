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

import de.fraunhofer.iosb.ilt.faaast.client.util.HttpClientHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class HttpClientTokenBasedTest {
    private static MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
    }


    @Test
    public void send_withCorrectAuthHeader_isAddedToRequest() throws InterruptedException {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> "Bearer eyxyZABCDEFGHIJKLMNOPQRSTUVWXYZ";

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);
        HttpRequest request = ordinaryGetRequest();

        RecordedRequest recordedRequest = sendAndRecordRequest(wrapSend(testSubject), request);

        assertRequest(request, recordedRequest, supplier.get());
    }


    @Test
    public void sendAsync_withCorrectAuthHeader_isAddedToRequest() throws InterruptedException {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> "Bearer eyxyZABCDEFGHIJKLMNOPQRSTUVWXYZ";

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);
        HttpRequest request = ordinaryGetRequest();

        RecordedRequest recordedRequest = sendAndRecordRequest(wrapSendAsync(testSubject), request);

        assertRequest(request, recordedRequest, supplier.get());
    }


    @Test
    public void sendAsyncWithPushPromise_withCorrectAuthHeader_isAddedToRequest() throws InterruptedException {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> "Bearer eyxyZABCDEFGHIJKLMNOPQRSTUVWXYZ";

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);
        HttpRequest request = ordinaryGetRequest();

        RecordedRequest recordedRequest = sendAndRecordRequest(wrapSendAsyncWithPushPromise(testSubject), request);

        assertRequest(request, recordedRequest, supplier.get());
    }


    @Test
    public void send_supplyingNull_doesNotFail() throws InterruptedException {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> null;

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);
        HttpRequest request = ordinaryGetRequest();

        RecordedRequest recordedRequest = sendAndRecordRequest(wrapSend(testSubject), request);

        assertRequest(request, recordedRequest, supplier.get());
    }


    @Test
    public void send_noAdditionalHeaders_doesNotFail() throws InterruptedException {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> "test";

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%s/mypath", server.getPort())))
                .GET()
                .timeout(Duration.of(1234, ChronoUnit.SECONDS))
                .build();

        RecordedRequest recordedRequest = sendAndRecordRequest(wrapSend(testSubject), request);

        assertRequest(request, recordedRequest, supplier.get());
    }


    @Test
    public void validate_allSettings_sameAsDecoratedHttpClient() {
        HttpClient httpClient = HttpClientHelper.newDefaultClient();
        Supplier<String> supplier = () -> "Bearer eyxyZABCDEFGHIJKLMNOPQRSTUVWXYZ";

        HttpClient testSubject = new HttpClientTokenBased(httpClient, supplier);

        assertEquals(httpClient.authenticator(), testSubject.authenticator());
        assertEquals(httpClient.connectTimeout(), testSubject.connectTimeout());
        assertEquals(httpClient.cookieHandler(), testSubject.cookieHandler());
        assertEquals(httpClient.executor(), testSubject.executor());
        assertEquals(httpClient.followRedirects(), testSubject.followRedirects());
        assertEquals(httpClient.proxy(), testSubject.proxy());
        assertEquals(httpClient.sslContext(), testSubject.sslContext());
        assertArrayEquals(httpClient.sslParameters().getCipherSuites(), testSubject.sslParameters().getCipherSuites());
        assertArrayEquals(httpClient.sslParameters().getProtocols(), testSubject.sslParameters().getProtocols());
        assertEquals(httpClient.sslParameters().getWantClientAuth(), testSubject.sslParameters().getWantClientAuth());
        assertEquals(httpClient.sslParameters().getNeedClientAuth(), testSubject.sslParameters().getNeedClientAuth());
        assertEquals(httpClient.sslParameters().getEndpointIdentificationAlgorithm(), testSubject.sslParameters().getEndpointIdentificationAlgorithm());
        assertEquals(httpClient.sslParameters().getAlgorithmConstraints(), testSubject.sslParameters().getAlgorithmConstraints());
        assertEquals(httpClient.sslParameters().getSNIMatchers(), testSubject.sslParameters().getSNIMatchers());
        assertEquals(httpClient.sslParameters().getServerNames(), testSubject.sslParameters().getServerNames());
        assertEquals(httpClient.sslParameters().getUseCipherSuitesOrder(), testSubject.sslParameters().getUseCipherSuitesOrder());
        assertEquals(httpClient.sslParameters().getEnableRetransmissions(), testSubject.sslParameters().getEnableRetransmissions());
        assertEquals(httpClient.sslParameters().getMaximumPacketSize(), testSubject.sslParameters().getMaximumPacketSize());
        assertArrayEquals(httpClient.sslParameters().getApplicationProtocols(), testSubject.sslParameters().getApplicationProtocols());
        assertEquals(httpClient.version(), testSubject.version());
    }


    private BiFunction<HttpRequest, HttpResponse.BodyHandler<?>, HttpResponse<?>> wrapSend(HttpClient client) {
        return (request, responseHandler) -> {
            try {
                return client.send(request, responseHandler);
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }


    private BiFunction<HttpRequest, HttpResponse.BodyHandler<?>, HttpResponse<?>> wrapSendAsync(HttpClient client) {
        return (request, responseHandler) -> {
            try {
                return client.sendAsync(request, responseHandler).get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }


    private BiFunction<HttpRequest, HttpResponse.BodyHandler<?>, HttpResponse<?>> wrapSendAsyncWithPushPromise(HttpClient client) {
        return (request, responseHandler) -> {
            try {
                return client.sendAsync(request, responseHandler, null).get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }


    private RecordedRequest sendAndRecordRequest(BiFunction<HttpRequest, HttpResponse.BodyHandler<?>, HttpResponse<?>> handler, HttpRequest request)
            throws InterruptedException {
        server.enqueue(new MockResponse());
        handler.apply(request, HttpResponse.BodyHandlers.discarding());
        return server.takeRequest();
    }


    private void assertRequest(HttpRequest sent, RecordedRequest recorded, String authenticationHeaderValue) {
        assertEquals(authenticationHeaderValue, recorded.getHeader("Authorization"));
        sent.headers().map().forEach((k, vs) -> vs.forEach(v -> assertEquals(v, recorded.getHeader(k))));
        assertEquals(0, recorded.getBodySize());
        assertNotNull(recorded.getRequestUrl());
        assertEquals(sent.uri().getPath(), recorded.getPath());
    }


    private HttpRequest ordinaryGetRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%s/mypath", server.getPort())))
                .GET()
                .headers("Accept", "application/json")
                .timeout(Duration.of(1234, ChronoUnit.SECONDS))
                .build();
    }
}
