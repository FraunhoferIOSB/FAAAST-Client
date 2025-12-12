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

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import static java.net.http.HttpRequest.BodyPublishers.noBody;


/**
 * HttpClient decorator adding to each request an authorization header given by a supplier.
 */
public class HttpClientTokenBased extends HttpClient {
    private static final String AUTHORIZATION = "Authorization";

    private final HttpClient impl;
    private final Supplier<String> authSupplier;

    public HttpClientTokenBased(HttpClient httpClient, Supplier<String> authSupplier) {
        this.authSupplier = authSupplier;
        this.impl = httpClient;
    }


    @Override
    public Optional<CookieHandler> cookieHandler() {
        return impl.cookieHandler();
    }


    @Override
    public Optional<Duration> connectTimeout() {
        return impl.connectTimeout();
    }


    @Override
    public Redirect followRedirects() {
        return impl.followRedirects();
    }


    @Override
    public Optional<ProxySelector> proxy() {
        return impl.proxy();
    }


    @Override
    public SSLContext sslContext() {
        return impl.sslContext();
    }


    @Override
    public SSLParameters sslParameters() {
        return impl.sslParameters();
    }


    @Override
    public Optional<Authenticator> authenticator() {
        return impl.authenticator();
    }


    @Override
    public HttpClient.Version version() {
        return impl.version();
    }


    @Override
    public Optional<Executor> executor() {
        return impl.executor();
    }


    @Override
    public <T> HttpResponse<T> send(HttpRequest req, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        return impl.send(decorate(req), responseBodyHandler);
    }


    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest req, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return impl.sendAsync(decorate(req), responseBodyHandler);
    }


    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest req,
                                                            HttpResponse.BodyHandler<T> responseBodyHandler,
                                                            HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return impl.sendAsync(decorate(req), responseBodyHandler, pushPromiseHandler);
    }


    private HttpRequest decorate(HttpRequest request) {
        HttpRequest.Builder decoratedRequestBuilder = HttpRequest.newBuilder()
                .uri(request.uri())
                .method(request.method(), request.bodyPublisher().orElse(noBody()))
                .expectContinue(request.expectContinue());

        Optional.ofNullable(authSupplier.get())
                .ifPresent(auth -> decoratedRequestBuilder.header(AUTHORIZATION, auth));

        // Add existing headers
        request.headers().map()
                .forEach((key, values) -> values.forEach(value -> decoratedRequestBuilder.header(key, value)));

        // Optional settings
        request.version().ifPresent(decoratedRequestBuilder::version);
        request.timeout().ifPresent(decoratedRequestBuilder::timeout);
        return decoratedRequestBuilder.build();
    }
}
