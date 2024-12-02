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

import de.fraunhofer.iosb.ilt.faaast.client.query.SearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Extent;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Level;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Utility class for building the URIs used in HTTP requests.
 * Provides methods build uris from various combinations of input parameters.
 */
public final class UriBuilder {
    URI serviceUri;

    /**
     * Constructs a new UriBuilder with the given uri.
     *
     * @param uri the base uri of the AAS Server.
     */
    public UriBuilder(URI uri) {
        this.serviceUri = uri;
    }


    /**
     * Creates an uri using the base path of the request.
     * 
     * @param path the base path.
     * @return the uri to use in an http request.
     */
    public URI getUri(String path) {
        return serviceUri.resolve(path);
    }


    /**
     * Creates a uri using the query various query modifiers.
     * 
     * @param path the base path.
     * @param content the content modifier specifies the server responds: normal, metadata, path, reference or value only.
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel.
     * @return the uri to use in an http request.
     */
    public URI getUri(String path, Content content, QueryModifier modifier) {
        String sb = path + serializeContentModifier(content) +
                serializeParameters(modifier);
        return serviceUri.resolve(sb);
    }


    /**
     * Creates a uri using various query modifiers, paging and search criteria.
     * 
     * @param path The base path.
     * @param content The content modifier specifies the server responds: normal, metadata, path, reference or value only.
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel.
     * @param pagingInfo Metadata for controlling the pagination of results.
     * @param searchCriteria Search criteria to filter identifiables based on specific criteria.
     * @return the uri to use in an http request.
     */
    public URI getUri(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, SearchCriteria searchCriteria) {
        String sb = path + serializeContentModifier(content) +
                serializeParameters(modifier, pagingInfo, searchCriteria);
        return serviceUri.resolve(sb);
    }


    private static String serializeContentModifier(Content contentModifier) {
        if (contentModifier.equals(Content.DEFAULT)) {
            return "";
        }
        return String.format("$%s", contentModifier.name().toLowerCase());
    }


    private static String serializeParameters(QueryModifier queryModifier) {
        return serializeParameters(queryModifier, PagingInfo.ALL, SearchCriteria.DEFAULT);
    }


    private static String serializeParameters(QueryModifier queryModifier, PagingInfo pagingInfo, SearchCriteria searchCriteria) {
        String levelString = queryModifier.getLevel() == Level.DEFAULT ? ""
                : "level=" + queryModifier.getLevel().name().toLowerCase();
        String extentString = queryModifier.getExtent() == Extent.DEFAULT ? ""
                : "extent=" + queryModifier.getExtent().name().toLowerCase();
        String limitString = pagingInfo.getLimit() == PagingInfo.DEFAULT_LIMIT ? ""
                : String.format("%s=%d", "limit", pagingInfo.getLimit());
        String cursorString = pagingInfo.getCursor() == null ? ""
                : "cursor=" + EncodingHelper.base64UrlEncode(pagingInfo.getCursor());
        String searchCriteriaString = searchCriteria == SearchCriteria.DEFAULT ? ""
                : searchCriteria.toQueryString();

        String serializedParameters = Stream.of(levelString, extentString, limitString, cursorString, searchCriteriaString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));

        return serializedParameters.isEmpty() ? "" : "?" + serializedParameters;
    }
}
