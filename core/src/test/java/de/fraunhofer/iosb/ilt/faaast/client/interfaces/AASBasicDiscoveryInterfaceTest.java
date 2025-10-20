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
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class AASBasicDiscoveryInterfaceTest {

    private static AASBasicDiscoveryInterface discoveryInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        discoveryInterface = new AASBasicDiscoveryInterface(
                server.url("api/v3.0").uri());
        serializer = new JsonApiSerializer();
    }


    @Test
    public void testLookupByAssetLink() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<String> aasIdList = new ArrayList<>();
        aasIdList.add("aasI1");
        aasIdList.add("aasI2");

        Page<String> expected = Page.<String> builder()
                .result(aasIdList)
                .metadata(new PagingMetadata.Builder().cursor("1").build())
                .build();

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        List<SpecificAssetId> assetIdentificationList = new ArrayList<>();
        assetIdentificationList.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId1").build());
        assetIdentificationList.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId2").build());
        Page<String> actual = discoveryInterface.lookupByAssetLink(assetIdentificationList, PagingInfo.ALL);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(
                "/api/v3.0/lookup/shells/?assetIds=ew0KICAibmFtZSIgOiAiZ2xvYmFsQXNzZXRJZCIsDQogICJ2YWx1ZSIgOiAiZ2xvYmFsQXNzZXRJZDEiDQp9,ew0KICAibmFtZSIgOiAiZ2xvYmFsQXNzZXRJZCIsDQogICJ2YWx1ZSIgOiAiZ2xvYmFsQXNzZXRJZDIiDQp9",
                request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testLookupByAASId() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<SpecificAssetId> expected = new ArrayList<>();
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId1").build());
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId2").build());

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        List<SpecificAssetId> actual = discoveryInterface.lookupByAasId("aasId1"); // todo: find reason for deserialization error
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testCreateAssetLinks() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<SpecificAssetId> expected = new ArrayList<>();
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId1").build());
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId2").build());

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        List<SpecificAssetId> actual = discoveryInterface.createAssetLinks(expected, "aasId1"); // todo: find reason for deserialization error
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testDeleteAssetLinks() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        server.enqueue(new MockResponse().setResponseCode(204));
        discoveryInterface.deleteAssetLinks("aasId1");
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
    }
}
