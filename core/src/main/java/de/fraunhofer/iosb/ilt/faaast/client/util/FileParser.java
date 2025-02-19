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

import de.fraunhofer.iosb.ilt.faaast.service.model.TypedInMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.util.Ensure;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.hc.core5.http.ContentType;

import java.net.http.HttpResponse;
import java.util.Map;

import static org.apache.commons.fileupload.FileUploadBase.CONTENT_DISPOSITION;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_TYPE;


/**
 * Utility class for parsing HTTP responses for file contents.
 */
public class FileParser {

    private static final String DEFAULT_FILENAME = "unknown";
    private static final String FILENAME_PARAMETER = "filename";

    /**
     * Parses HTTP response to TypedInMemoryFile.
     *
     * @param httpResponse HTTP response
     * @return deserialized payload
     */
    public static TypedInMemoryFile parseBody(HttpResponse<byte[]> httpResponse) {
        Ensure.requireNonNull(httpResponse, "httpResponse must be non-null");

        String contentTypeHeader = httpResponse.headers().firstValue(CONTENT_TYPE).orElse(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        String contentDispositionHeader = httpResponse.headers().firstValue(CONTENT_DISPOSITION).orElse(DEFAULT_FILENAME);
        return new TypedInMemoryFile.Builder()
                .content(httpResponse.body())
                .contentType(contentTypeHeader)
                .path(extractName(contentDispositionHeader)).build();
    }


    private static String extractName(String contentDispositionHeader) {
        ParameterParser parser = new ParameterParser();
        Map<String, String> params = parser.parse(contentDispositionHeader, ';');

        return params.getOrDefault(FILENAME_PARAMETER, DEFAULT_FILENAME);
    }
}
