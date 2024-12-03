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
package de.fraunhofer.iosb.ilt.faaast.client.interfaces;

import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.InvalidPayloadException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.query.SearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.ExceptionHandler;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpClientUtility;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.util.UriBuilder;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.DeserializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiDeserializer;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.typing.TypeInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Abstract base class providing core functionality for sending HTTP requests and handling API responses.
 * Supports GET, POST, PUT, PATCH and DELETE operations, deserialization of responses, and throws exceptions based on
 * status codes.
 * Subclasses extend these methods to interact with specific APIs.
 */
public abstract class BaseInterface {
    protected String basePath;
    protected URI serviceUri;
    protected UriBuilder uriBuilder;
    private final HttpClientUtility httpClientUtility;
    private final HttpClient httpClient;

    protected BaseInterface(URI serviceUri, String basePath) {
        this.basePath = serviceUri + basePath;
        this.serviceUri = serviceUri;
        this.uriBuilder = new UriBuilder(serviceUri);
        this.httpClient = HttpClient.newHttpClient();
        this.httpClientUtility = new HttpClientUtility(httpClient);
    }


    protected BaseInterface(URI serviceUri, String basePath, String username, String password) {
        this.basePath = serviceUri + basePath;
        this.serviceUri = serviceUri;
        this.uriBuilder = new UriBuilder(serviceUri);
        this.httpClient = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                }).build();
        this.httpClientUtility = new HttpClientUtility(httpClient);
    }


    protected BaseInterface(URI serviceUri, String basePath, HttpClient httpClient) {
        this.basePath = serviceUri + basePath;
        this.serviceUri = serviceUri;
        this.uriBuilder = new UriBuilder(serviceUri);
        this.httpClient = httpClient;
        this.httpClientUtility = new HttpClientUtility(httpClient);
    }


    protected <T> T get(String path, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, Content.DEFAULT, responseType);
    }


    protected <T> T get(String path, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, QueryModifier.DEFAULT, content, responseType);
    }


    protected <T> T get(String path, QueryModifier queryModifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, queryModifier, Content.DEFAULT, responseType);
    }


    protected <T> T get(String path, QueryModifier modifier, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createGetRequest(uriBuilder.getUri(path, content, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        try {
            if (response.statusCode() == 200) {
                return new JsonApiDeserializer().read(response.body(), responseType);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.GET, request, response);
            }
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected <T extends ElementValue> T getValue(String path, QueryModifier modifier, TypeInfo<?> typeInfo) throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createGetRequest(uriBuilder.getUri(path, Content.VALUE, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        try {
            if (response.statusCode() == 200) {
                return new JsonApiDeserializer().readValue(response.body(), typeInfo);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.GET, request, response);
            }
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected <T> List<T> getList(String path, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getList(path, QueryModifier.DEFAULT, responseType);
    }


    protected <T> List<T> getList(String path, QueryModifier modifier, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getList(path, SearchCriteria.DEFAULT, Content.DEFAULT, modifier, responseType);
    }


    protected <T> List<T> getList(String path, SearchCriteria searchCriteria, Content content, QueryModifier modifier, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createGetRequest(uriBuilder.getUri(
                path, content, modifier, new PagingInfo.Builder().build(), searchCriteria));
        HttpResponse<String> response = httpClientUtility.send(request);

        uriBuilder.getUri(path, content, modifier, new PagingInfo.Builder().build(), searchCriteria);
        try {
            if (response.statusCode() == 200) {
                return new JsonApiDeserializer().readList(response.body(), responseType);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.GET, request, response);
            }
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected <T> Page<T> getPage(String path, PagingInfo pagingInfo, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getPage(path, QueryModifier.DEFAULT, pagingInfo, responseType);
    }


    protected <T> Page<T> getPage(String path, QueryModifier modifier, PagingInfo pagingInfo, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getPage(path, Content.DEFAULT, modifier, pagingInfo, responseType);
    }


    protected <T> Page<T> getPage(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getPage(path, content, modifier, pagingInfo, SearchCriteria.DEFAULT, responseType);
    }


    protected <T> Page<T> getPage(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, SearchCriteria searchCriteria, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createGetRequest(uriBuilder.getUri(
                path, content, modifier, pagingInfo, searchCriteria));
        HttpResponse<String> response = httpClientUtility.send(request);

        try {
            if (response.statusCode() == 200) {
                return deserializePage(response.body(), responseType);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.GET, request, response);
            }
        }
        catch (DeserializationException | JSONException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected <T extends ElementValue> T postValue(String path, Object entity, TypeInfo<?> typeInfo, QueryModifier modifier)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createPostRequest(uriBuilder.getUri(
                path, Content.VALUE, QueryModifier.DEFAULT),
                serialize(entity, Content.VALUE, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        try {
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return new JsonApiDeserializer().readValue(response.body(), typeInfo);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.POST, request, response);
            }
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected <T> T post(String path, Object entity, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(path, entity, QueryModifier.DEFAULT, Content.DEFAULT, responseType);
    }


    protected <T> T post(String path, Object entity, QueryModifier modifier, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createPostRequest(uriBuilder.getUri(path, content, QueryModifier.DEFAULT),
                serialize(entity, content, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        try {
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return new JsonApiDeserializer().read(response.body(), responseType);
            }
            else {
                throw ExceptionHandler.handleException(HttpMethod.POST, request, response);
            }
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    protected void put(String path, Object entity) throws ConnectivityException, StatusCodeException {
        put(path, entity, QueryModifier.DEFAULT);
    }


    protected void put(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        put(path, entity, Content.DEFAULT, modifier);
    }


    protected void put(String path, Object entity, Content content, QueryModifier modifier)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createPutRequest(
                uriBuilder.getUri(path, content, modifier),
                serialize(entity, content, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw ExceptionHandler.handleException(HttpMethod.PUT, request, response);
        }
    }


    protected void patch(String path, Object entity) throws ConnectivityException, StatusCodeException {
        patch(path, entity, QueryModifier.DEFAULT);
    }


    protected void patch(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(path, entity, Content.DEFAULT, modifier);
    }


    protected void patch(String path, Object entity, Content content, QueryModifier modifier)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createPatchRequest(
                uriBuilder.getUri(path, content, modifier),
                serialize(entity, content, modifier));
        HttpResponse<String> response = httpClientUtility.send(request);

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw ExceptionHandler.handleException(HttpMethod.PATCH, request, response);
        }
    }


    protected void patchValue(String path, Object entity, QueryModifier modifier)
            throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createPatchRequest(
                uriBuilder.getUri(path, Content.VALUE, modifier),
                serializeEntity(entity));
        HttpResponse<String> response = httpClientUtility.send(request);

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw ExceptionHandler.handleException(HttpMethod.PATCH, request, response);
        }
    }


    protected void delete(String path) throws ConnectivityException, StatusCodeException {

        HttpRequest request = httpClientUtility.createDeleteRequest(uriBuilder.getUri(path));
        HttpResponse<String> response = httpClientUtility.send(request);

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw ExceptionHandler.handleException(HttpMethod.DELETE, request, response);
        }
    }


    protected String basePath() {
        return basePath;
    }


    protected String idPath(String id) {
        return basePath() + EncodingHelper.base64UrlEncode(id) + "/";
    }


    protected HttpClient httpClient() {
        return this.httpClient;
    }


    private static String serialize(Object entity, Content content, QueryModifier queryModifier) {
        try {
            OutputModifier outputModifier = new OutputModifier.Builder()
                    .level(queryModifier.getLevel())
                    .extend(queryModifier.getExtent())
                    .content(content).build();
            return new JsonApiSerializer().write(entity, outputModifier);
        }
        catch (SerializationException | UnsupportedModifierException e) {
            throw new InvalidPayloadException("Serialization Failed", e);
        }
    }


    private static String serializeEntity(Object entity) {
        try {
            return new JsonApiSerializer().write(entity, OutputModifier.DEFAULT);
        }
        catch (SerializationException | UnsupportedModifierException e) {
            throw new InvalidPayloadException("Serialization Failed", e);
        }
    }


    private static <T> Page<T> deserializePage(String responseBody, Class<T> responseType) throws DeserializationException, JSONException {
        JSONArray result = new JSONObject(responseBody).getJSONArray("result");
        JSONObject metadata = new JSONObject(responseBody).getJSONObject("paging_metadata");
        return new Page.Builder<T>()
                .result(new JsonApiDeserializer().readList(result.toString(), responseType))
                .metadata(new JsonApiDeserializer().read(metadata.toString(), PagingMetadata.class))
                .build();
    }
}
