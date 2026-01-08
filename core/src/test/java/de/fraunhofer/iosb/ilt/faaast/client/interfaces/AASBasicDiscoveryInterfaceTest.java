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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ClientException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;
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
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


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
    public void testLookupByAssetLink() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, JsonProcessingException {
        List<String> aasIdList = new ArrayList<>();
        aasIdList.add("aasI1");
        aasIdList.add("aasI2");

        Page<String> expected = Page.<String>builder()
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

        String path = request.getPath();
        assert path != null;
        String base64Part = path.substring(path.indexOf('=') + 1);
        String decodedJson = new String(Base64.getDecoder().decode(base64Part));
        String expectedJson = """
                [
                  { "name": "globalAssetId", "value": "globalAssetId1" },
                  { "name": "globalAssetId", "value": "globalAssetId2" }
                ]
                """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedNode = mapper.readTree(expectedJson);
        JsonNode actualNode = mapper.readTree(decodedJson);

        assertEquals(expectedNode, actualNode);
        assertEquals(expected, actual);
    }


    @Test
    public void testLookupByAssetLinkFallback() throws SerializationException, UnsupportedModifierException, StatusCodeException, ConnectivityException, InterruptedException {
        server.enqueue(new MockResponse().setBody(""));

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

        assertEquals(expected, actual);

        RecordedRequest firstRequest = server.takeRequest();
        String firstPath = firstRequest.getPath();
        assert firstPath != null;

        RecordedRequest fallbackRequest = server.takeRequest();
        String fallbackPath = fallbackRequest.getPath();
        assert fallbackPath != null;

        assertEquals("GET", firstRequest.getMethod());
        assertEquals("GET", fallbackRequest.getMethod());

        assertNotEquals(firstPath, fallbackPath);
    }


    @Test
    public void testLookupByAASId() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<SpecificAssetId> expected = new ArrayList<>();
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId1").build());
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId2").build());

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        List<SpecificAssetId> actual = discoveryInterface.lookupByAasId("aasId1");
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testCreateAssetLinks() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<SpecificAssetId> expected = new ArrayList<>();
        expected.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("globalAssetId1").build());

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        List<SpecificAssetId> actual = discoveryInterface.createAssetLinks(expected, "aasId1");
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testDeleteAssetLinks() throws InterruptedException, ClientException {
        server.enqueue(new MockResponse().setResponseCode(204));
        discoveryInterface.deleteAssetLinks("aasId1");
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/lookup/shells/YWFzSWQx", request.getPath());
    }
}
