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
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AASRegistryInterfaceTest {
    private static AASRegistryInterface assetAdministrationShellRegistryInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;
    private static List<DefaultAssetAdministrationShellDescriptor> requestShellDescriptors;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("/example/api/v3.0").uri();
        assetAdministrationShellRegistryInterface = new AASRegistryInterface(serviceUri);

        serializer = new JsonApiSerializer();

        requestShellDescriptors = new ArrayList<>();
        requestShellDescriptors.add(getRequestShellDescriptor(AssetKind.INSTANCE));
        requestShellDescriptors.add(getRequestShellDescriptor(AssetKind.TYPE));
    }


    @NotNull
    static DefaultAssetAdministrationShellDescriptor getRequestShellDescriptor(AssetKind assetKind) {
        DefaultAssetAdministrationShellDescriptor requestShellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        requestShellDescriptor.setAssetKind(assetKind);
        requestShellDescriptor.setAssetType("defaultType");
        return requestShellDescriptor;
    }


    @Test
    public void testGetAllAssetAdministrationShellDescriptors() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        String serializedShellDescriptors = serializer.write(requestShellDescriptors);
        server.enqueue(new MockResponse().setBody(serializedShellDescriptors));

        List<DefaultAssetAdministrationShellDescriptor> responseShellDescriptors = assetAdministrationShellRegistryInterface.getAll();

        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shell-descriptors", request.getPath());
        assertEquals(requestShellDescriptors, responseShellDescriptors);
    }


    @Test
    public void testPost() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        DefaultAssetAdministrationShellDescriptor requestShellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        String requestAasIdentifier = "DefaultId";
        requestShellDescriptor.setId(requestAasIdentifier);

        String serializedShellDescriptors = serializer.write(requestShellDescriptor);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedShellDescriptors));

        AssetAdministrationShellDescriptor returnShellDescriptor = assetAdministrationShellRegistryInterface.post(requestShellDescriptor);

        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestShellDescriptor, returnShellDescriptor);
        assertEquals("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(requestAasIdentifier), request.getPath());
    }


    @Test
    public void testGet() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        AssetAdministrationShellDescriptor requestShellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        String requestAasIdentifier = "DefaultId";
        requestShellDescriptor.setId(requestAasIdentifier);

        String serializedShellDescriptors = serializer.write(requestShellDescriptor);
        server.enqueue(new MockResponse().setBody(serializedShellDescriptors));

        DefaultAssetAdministrationShellDescriptor responseShellDescriptor = assetAdministrationShellRegistryInterface.get(requestAasIdentifier);

        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(requestAasIdentifier), request.getPath());
        assertEquals(requestShellDescriptor, responseShellDescriptor);
    }


    @Test
    public void testPut() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        DefaultAssetAdministrationShellDescriptor requestShellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        String requestAasId = "DefaultId";
        requestShellDescriptor.setId(requestAasId);

        String serializedShellDescriptors = serializer.write(requestShellDescriptor);
        server.enqueue(new MockResponse().setResponseCode(204));

        assetAdministrationShellRegistryInterface.put(requestAasId, requestShellDescriptor);

        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedShellDescriptors, request.getBody().readUtf8());
        assertEquals("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(requestAasId), request.getPath());
    }


    @Test
    public void testDelete() throws ClientException, InterruptedException {
        AssetAdministrationShellDescriptor requestShellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        String requestAasIdentifier = "DefaultId";
        requestShellDescriptor.setId(requestAasIdentifier);

        server.enqueue(new MockResponse().setResponseCode(204));

        assetAdministrationShellRegistryInterface.delete(requestAasIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBody().size());
        assertEquals("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(requestAasIdentifier), request.getPath());
    }
}
