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

import de.fraunhofer.iosb.ilt.faaast.client.exception.BadRequestException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConflictException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ForbiddenException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.InternalServerErrorException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.InvalidPayloadException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.MethodNotAllowedException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.NotFoundException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.UnauthorizedException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.UnsupportedStatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.query.SearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpFactory;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpStatus;
import de.fraunhofer.iosb.ilt.faaast.client.util.QueryHelper;
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
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private static final String URI_PATH_SEPERATOR = "/";
    private static final List<HttpStatus> SUPPORTED_DEFAULT_HTTP_STATUS = List.of(
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.INTERNAL_SERVER_ERROR);

    protected final HttpClient httpClient;
    protected final URI endpoint;
    protected final boolean trustSelfSign;

    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    protected BaseInterface(URI endpoint) {
        this(endpoint, HttpClient.newHttpClient());
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    protected BaseInterface(URI endpoint, String user, String password) {
        this(endpoint, HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                }).build());
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    protected BaseInterface(URI endpoint, HttpClient httpClient) {
        this.endpoint = sanitizeEndpoint(endpoint);
        this.httpClient = httpClient;
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustSelfSign Allows user to specify if self signed certs are accepted
     */
    protected BaseInterface(URI endpoint, boolean trustSelfSign) throws NoSuchAlgorithmException, KeyManagementException {
        if(trustSelfSign) {
            this(endpoint, HttpFactory.createHttpClient();
        }
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get((String) null, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(String path, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(null, QueryModifier.DEFAULT, content, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(String path, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, QueryModifier.DEFAULT, content, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param modifier the query modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(QueryModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(null, modifier, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the query modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(String path, QueryModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(path, modifier, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param modifier the query modifier
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(QueryModifier modifier, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return get(null, modifier, content, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the query modifier
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(String path, QueryModifier modifier, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, content, modifier)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        return parseBody(response, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType} using valueOnly serialization.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the query modifier
     * @param typeInfo the type information about the AAS element to be returned
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T extends ElementValue> T getValue(String path, QueryModifier modifier, TypeInfo<?> typeInfo) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, Content.VALUE, modifier)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return new JsonApiDeserializer().readValue(response.body(), typeInfo);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getAll(null, QueryModifier.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(String path, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getAll(path, QueryModifier.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the query modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(String path, QueryModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getAll(path, SearchCriteria.DEFAULT, Content.DEFAULT, modifier, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param searchCriteria the search criteria
     * @param content the content modifier
     * @param modifier the query modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(SearchCriteria searchCriteria, Content content, QueryModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getAll(null, searchCriteria, content, modifier, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param searchCriteria the search criteria
     * @param content the content modifier
     * @param modifier the query modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(String path, SearchCriteria searchCriteria, Content content, QueryModifier modifier, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, content, modifier, PagingInfo.ALL, searchCriteria)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializePage(response.body(), responseType).getContent();
        }
        catch (DeserializationException | JSONException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param pagingInfo the paging information
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(PagingInfo pagingInfo, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getPage((String) null, pagingInfo, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param pagingInfo the paging information
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(String path, PagingInfo pagingInfo, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getPage(path, QueryModifier.DEFAULT, pagingInfo, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the query modifier
     * @param pagingInfo the paging information
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(String path, QueryModifier modifier, PagingInfo pagingInfo, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return getPage(path, Content.DEFAULT, modifier, pagingInfo, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param content the content modifier
     * @param modifier the query modifier
     * @param pagingInfo the paging information
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getPage(path, content, modifier, pagingInfo, SearchCriteria.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param content the content modifier
     * @param modifier the query modifier
     * @param pagingInfo the paging information
     * @param searchCriteria the search criteria
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(Content content, QueryModifier modifier, PagingInfo pagingInfo, SearchCriteria searchCriteria, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        return getPage(null, content, modifier, pagingInfo, searchCriteria, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param content the content modifier
     * @param modifier the query modifier
     * @param pagingInfo the paging information
     * @param searchCriteria the search criteria
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, SearchCriteria searchCriteria, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, content, modifier, pagingInfo, searchCriteria)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializePage(response.body(), responseType);
        }
        catch (DeserializationException | JSONException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param entity the payload to send in the POST body
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(Object entity, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(null, entity, QueryModifier.DEFAULT, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(path, entity, QueryModifier.DEFAULT, Content.DEFAULT, responseType);
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param expectedStatusCode the expected HTTP status code
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, HttpStatus expectedStatusCode, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(path, entity, QueryModifier.DEFAULT, Content.DEFAULT, expectedStatusCode, responseType);
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param modifier the query modifier
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, QueryModifier modifier, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(path, entity, modifier, content, HttpStatus.CREATED, responseType);
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param modifier the query modifier
     * @param content the content modifier
     * @param expectedStatusCode the expected HTTP status code
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, QueryModifier modifier, Content content, HttpStatus expectedStatusCode, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPostRequest(
                resolve(QueryHelper.apply(path, content, QueryModifier.DEFAULT)),
                serialize(entity, content, modifier));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.POST, response, expectedStatusCode);
        return parseBody(response, responseType);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void put(Object entity) throws ConnectivityException, StatusCodeException {
        put(null, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void put(String path, Object entity) throws ConnectivityException, StatusCodeException {
        put(path, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void put(Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        put(null, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void put(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        put(path, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void put(String path, Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPutRequest(
                resolve(QueryHelper.apply(path, content, modifier)),
                serialize(entity, content, modifier));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PUT, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patch(String path, Object entity) throws ConnectivityException, StatusCodeException {
        patch(path, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patch(Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(null, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patch(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(path, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patch(Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(null, entity, content, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patch(String path, Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPatchRequest(
                resolve(QueryHelper.apply(path, content, modifier)),
                serialize(entity, content, modifier));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PATCH, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP PATCH with valueOnly serialization.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void patchValue(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPatchRequest(
                resolve(QueryHelper.apply(path, Content.VALUE, modifier)),
                serializeEntity(entity));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PATCH, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP DELETE.
     *
     * @param path the URL path relative to the current endpoint
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void delete(String path) throws ConnectivityException, StatusCodeException {
        delete(path, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP DELETE.
     *
     * @param path the URL path relative to the current endpoint
     * @param expectedStatus the expected HTTP status code
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid statsu code
     */
    protected void delete(String path, HttpStatus expectedStatus) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createDeleteRequest(resolve(path));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.DELETE, response, expectedStatus);
    }


    /**
     * Creates a URL path for an id in the form of "/{base64URL-encoded id}".
     *
     * @param id the id
     * @return the URL path with the encoded id
     */
    protected String idPath(String id) {
        return "/" + EncodingHelper.base64UrlEncode(id);
    }


    /**
     * Resolves a path to the current {@code endpoint}.
     *
     * @param path the path to resolve
     * @return the resolved path relative to the current {@code endpoint}
     */
    protected URI resolve(String path) {
        return resolve(endpoint, path);
    }


    /**
     * Resolves a path to a given {@code baseUri}.
     *
     * @param baseUri the URI to resolve the path to
     * @param path the path to resolve
     * @return the resolved path relative to the current {@code baseUri}
     */
    protected static URI resolve(URI baseUri, String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            return baseUri;
        }
        String actualPath = path;
        if (actualPath.startsWith(URI_PATH_SEPERATOR)) {
            actualPath = "." + actualPath;
        }
        else if (!actualPath.startsWith("./")) {
            actualPath = "./" + actualPath;
        }
        if (actualPath.endsWith(URI_PATH_SEPERATOR)) {
            actualPath = actualPath.substring(0, actualPath.length() - 1);
        }
        try {
            return new URI(baseUri + URI_PATH_SEPERATOR).resolve(actualPath);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    String.format(
                            "error resolving path (endpoint: %s, path: %s)",
                            baseUri,
                            actualPath),
                    e);
        }
    }


    private static URI sanitizeEndpoint(URI endpoint) {
        URI result = endpoint;
        if (endpoint.getPath().endsWith(URI_PATH_SEPERATOR)) {
            try {
                result = new URI(endpoint.toString().substring(0, endpoint.toString().length() - 1));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(String.format("error sanitizing endpoint URI (endpoint: %s", endpoint), e);
            }
        }
        return result;
    }


    /**
     * Parses body of HTTP response.
     *
     * @param <T> result type
     * @param response the response
     * @param responseType the type of the payload to parse
     * @return parsed body of response
     */
    protected static <T> T parseBody(HttpResponse<String> response, Class<T> responseType) {
        try {
            return new JsonApiDeserializer().read(response.body(), responseType);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
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


    /**
     * Checks if a given response matches the expected HTTP status code.
     *
     * @param method the HTTP method
     * @param response the response to check
     * @param expected the expected HTTP status code
     * @throws StatusCodeException if the HTTP status code of the response is invlid/not supported
     */
    protected static void validateStatusCode(HttpMethod method, HttpResponse<String> response, HttpStatus expected) throws StatusCodeException {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("response must be non-null");
        }
        if (Objects.equals(expected.getCode(), response.statusCode())) {
            return;
        }
        List<HttpStatus> supported = new ArrayList<>(SUPPORTED_DEFAULT_HTTP_STATUS);
        if (Objects.equals(method, HttpMethod.POST)) {
            supported.add(HttpStatus.METHOD_NOT_ALLOWED);
            supported.add(HttpStatus.CONFLICT);
        }

        try {
            HttpStatus status = HttpStatus.from(response.statusCode());
            if (!supported.contains(status)) {
                throw new UnsupportedStatusCodeException(response);
            }
            throw switch (status) {
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
