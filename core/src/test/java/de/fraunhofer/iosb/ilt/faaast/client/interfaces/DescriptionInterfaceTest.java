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

import de.fraunhofer.iosb.ilt.faaast.client.exception.ClientException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.ApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiSerializer;
import java.io.IOException;
import java.util.List;

import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DescriptionInterfaceTest {

    private static DescriptionInterface descriptionInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        descriptionInterface = new DescriptionInterface(
                server.url("api/v3.0").uri());
        serializer = new JsonApiSerializer();
    }


    @Test
    public void testGet() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<String> requestDescriptionPage = Page.<String> builder()
                .result("Description1")
                .metadata(new PagingMetadata.Builder().build())
                .build();

        String serializedDescription = serializer.write(requestDescriptionPage);
        server.enqueue(new MockResponse().setBody(serializedDescription));

        List<String> responseDescription = descriptionInterface.get();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/description", request.getPath());
        assertEquals(requestDescriptionPage.getContent(), responseDescription);
    }
}
