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
import de.fraunhofer.iosb.ilt.faaast.service.model.descriptor.SubmodelDescriptor;
import de.fraunhofer.iosb.ilt.faaast.service.model.descriptor.impl.DefaultSubmodelDescriptor;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SubmodelRegistryInterfaceTest {

    private SubmodelRegistryInterface submodelRegistryInterface;
    private ApiSerializer serializer;
    private MockWebServer server;
    private static List<DefaultSubmodelDescriptor> requestSubmodelDescriptors;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("api/v3.0").uri();
        submodelRegistryInterface = new SubmodelRegistryInterface(serviceUri);
        serializer = new JsonApiSerializer();
        instantiateSubmodelDescriptors();
    }


    private void instantiateSubmodelDescriptors() {
        requestSubmodelDescriptors = new ArrayList<>();
        requestSubmodelDescriptors.add(createSubmodelDescriptor("defaultId1"));
        requestSubmodelDescriptors.add(createSubmodelDescriptor("defaultId2"));
    }


    @NotNull
    private DefaultSubmodelDescriptor createSubmodelDescriptor(String submodelDescriptorIdShort) {
        DefaultSubmodelDescriptor submodelDescriptor = new DefaultSubmodelDescriptor();
        submodelDescriptor.setId(submodelDescriptorIdShort);
        return submodelDescriptor;
    }


    @Test
    public void testGetAll() throws ClientException, SerializationException, InterruptedException {
        String serializedSubmodelDescriptors = serializer.write(requestSubmodelDescriptors);
        server.enqueue(new MockResponse().setBody(serializedSubmodelDescriptors));

        List<DefaultSubmodelDescriptor> responseSubmodelDescriptors = submodelRegistryInterface.getAll();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel-descriptors/", request.getPath());
        assertEquals(requestSubmodelDescriptors, responseSubmodelDescriptors);
    }


    @Test
    public void testPost() throws SerializationException, ClientException, InterruptedException {
        DefaultSubmodelDescriptor requestSubmodelDescriptor = requestSubmodelDescriptors.get(0);
        String requestSubmodelIdentifier = requestSubmodelDescriptor.getId();
        String serializedSubmodelDescriptors = serializer.write(requestSubmodelDescriptor);
        server.enqueue(new MockResponse().setBody(serializedSubmodelDescriptors));

        DefaultSubmodelDescriptor responseSubmodelDescriptor = submodelRegistryInterface.post(requestSubmodelDescriptor);

        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodelDescriptor, responseSubmodelDescriptor);
        assertEquals("/api/v3.0/submodel-descriptors/", request.getPath());
    }


    @Test
    public void testGetById() throws SerializationException, ClientException, InterruptedException {
        DefaultSubmodelDescriptor requestSubmodelDescriptor = requestSubmodelDescriptors.get(0);
        String requestSubmodelIdentifier = requestSubmodelDescriptor.getId();
        String serializedSubmodelDescriptor = serializer.write(requestSubmodelDescriptor);
        server.enqueue(new MockResponse().setBody(serializedSubmodelDescriptor));

        DefaultSubmodelDescriptor responseSubmodelDescriptor = submodelRegistryInterface.get(requestSubmodelIdentifier);

        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel-descriptors/" +
                Base64.getUrlEncoder().encodeToString(requestSubmodelIdentifier.getBytes()) + "/", request.getPath());
        assertEquals(requestSubmodelDescriptor, responseSubmodelDescriptor);
    }


    @Test
    public void testPutById() throws ClientException, SerializationException, InterruptedException {
        DefaultSubmodelDescriptor requestSubmodelDescriptor = requestSubmodelDescriptors.get(0);
        String requestSubmodelIdentifier = requestSubmodelDescriptor.getId();
        String serializedSubmodelDescriptors = serializer.write(requestSubmodelDescriptor);
        server.enqueue(new MockResponse().setBody(serializedSubmodelDescriptors));

        submodelRegistryInterface.put(requestSubmodelIdentifier, requestSubmodelDescriptor);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedSubmodelDescriptors, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel-descriptors/" +
                Base64.getUrlEncoder().encodeToString(requestSubmodelIdentifier.getBytes()) + "/", request.getPath());
    }


    @Test
    public void testDeleteById() throws SerializationException, ClientException, InterruptedException {
        SubmodelDescriptor requestSubmodelDescriptor = requestSubmodelDescriptors.get(0);
        String requestSubmodelIdentifier = requestSubmodelDescriptor.getId();
        String serializedSubmodelDescriptors = serializer.write(requestSubmodelDescriptor);
        server.enqueue(new MockResponse().setBody(serializedSubmodelDescriptors));

        submodelRegistryInterface.delete(requestSubmodelIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBody().size());
        assertEquals("/api/v3.0/submodel-descriptors/" +
                Base64.getUrlEncoder().encodeToString(requestSubmodelIdentifier.getBytes()) + "/", request.getPath());
    }
}