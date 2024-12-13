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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Helper class for serializing content modifiers and query parameters to URL.
 */
public class QueryHelper {
    private QueryHelper() {

    }


    /**
     * Creates a uri using the query various query modifiers.
     *
     * @param path the base path
     * @param content the content modifier specifies the server responds: normal, metadata, path, reference or value only
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return the uri to use in an http request
     */
    public static String apply(String path, Content content, QueryModifier modifier) {
        return apply(path, content, modifier, PagingInfo.ALL, SearchCriteria.DEFAULT);
    }


    /**
     * Creates a uri using various query modifiers, paging and search criteria.
     *
     * @param path The base path
     * @param content The content modifier specifies the server responds: normal, metadata, path, reference or value only
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param searchCriteria Search criteria to filter identifiables based on specific criteria
     * @return the uri to use in an http request
     */
    public static String apply(String path, Content content, QueryModifier modifier, PagingInfo pagingInfo, SearchCriteria searchCriteria) {
        String result = Objects.nonNull(path) ? path : "";
        result += serializeContentModifier(content) + serializeParameters(modifier, pagingInfo, searchCriteria);
        return result;
    }


    private static String serializeContentModifier(Content contentModifier) {
        if (contentModifier.equals(Content.DEFAULT)) {
            return "";
        }
        return String.format("/$%s", contentModifier.name().toLowerCase());
    }


    private static String serializeParameters(QueryModifier queryModifier, PagingInfo pagingInfo, SearchCriteria searchCriteria) {
        String levelString = queryModifier.getLevel() == Level.DEFAULT
                ? ""
                : String.format("level=%s", queryModifier.getLevel().name().toLowerCase());
        String extentString = queryModifier.getExtent() == Extent.DEFAULT
                ? ""
                : String.format("extent=%s", queryModifier.getExtent().name().toLowerCase());
        String limitString = pagingInfo.getLimit() == PagingInfo.DEFAULT_LIMIT
                ? ""
                : String.format("limit=%d", pagingInfo.getLimit());
        String cursorString = pagingInfo.getCursor() == null
                ? ""
                : String.format("cursor=%s", EncodingHelper.base64UrlEncode(pagingInfo.getCursor()));
        String serializedParameters = Stream.of(levelString, extentString, limitString, cursorString, searchCriteria.toQueryString())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
        return serializedParameters.isEmpty() ? "" : "?" + serializedParameters;
    }
}
