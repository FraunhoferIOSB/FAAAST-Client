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
import java.net.URI;
import java.util.List;

import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AASInterfaceTest {
    private AASInterface aasInterface;
    private ApiSerializer serializer;
    private MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("api/v3.0/aas").uri();

        serializer = new JsonApiSerializer();

        aasInterface = new AASInterface(serviceUri);
    }


    @Test
    public void testGetAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell();

        String serializedAas = serializer.write(requestAas);
        server.enqueue(new MockResponse().setBody(serializedAas));

        AssetAdministrationShell responseAas = aasInterface.get();

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas", request.getPath());
        assertEquals(requestAas, responseAas);
    }


    @Test
    public void testPutAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell();

        String serializedAas = serializer.write(requestAas);
        server.enqueue(new MockResponse().setResponseCode(204));
        aasInterface.put(requestAas);

        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedAas, request.getBody().readUtf8());
        assertEquals("/api/v3.0/aas", request.getPath());
    }


    @Test
    public void testGetAssetAdministrationShellAsReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Reference requestAasReference = new DefaultReference.Builder()
                .type(ReferenceTypes.MODEL_REFERENCE)
                .keys(new DefaultKey())
                .build();

        String serializedAasReference = serializer.write(requestAasReference);
        server.enqueue(new MockResponse().setBody(serializedAasReference));

        Reference responseAasReference = aasInterface.getAsReference();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/$reference", request.getPath());
        assertEquals(requestAasReference, responseAasReference);
    }


    @Test
    public void testGetAssetInformation() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetInformation requestAssetInformation = new DefaultAssetInformation();
        server.enqueue(new MockResponse().setBody(serializer.write(requestAssetInformation)));

        AssetInformation responseAssetInformation = aasInterface.getAssetInformation();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/asset-information", request.getPath());
        assertEquals(requestAssetInformation, responseAssetInformation);
    }


    @Test
    public void testPutAssetInformation() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetInformation requestAssetInformation = new DefaultAssetInformation();
        String serializedAssetInfo = serializer.write(requestAssetInformation);
        server.enqueue(new MockResponse().setResponseCode(204));

        aasInterface.putAssetInformation(requestAssetInformation);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedAssetInfo, request.getBody().readUtf8());
        assertEquals("/api/v3.0/aas/asset-information", request.getPath());
    }


    @Test
    public void testGetThumbnail() throws InterruptedException, SerializationException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell();
        Resource requestThumbnail = new DefaultResource();
        AssetInformation assetInformation = new DefaultAssetInformation();
        assetInformation.setDefaultThumbnail(requestThumbnail);
        requestAas.setAssetInformation(assetInformation);

        server.enqueue(new MockResponse().setBody(serializer.write(requestThumbnail)));

        Resource responseThumbnail = aasInterface.getThumbnail();

        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/asset-information/thumbnail", request.getPath());
        assertEquals(requestThumbnail, responseThumbnail);
    }


    @Test
    public void testPutThumbnail() throws InterruptedException, SerializationException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell.Builder().build();
        Resource requestThumbnail = new DefaultResource();
        AssetInformation assetInformation = new DefaultAssetInformation();
        assetInformation.setDefaultThumbnail(requestThumbnail);
        requestAas.setAssetInformation(assetInformation);

        server.enqueue(new MockResponse().setResponseCode(204));

        aasInterface.putThumbnail(requestThumbnail);

        RecordedRequest request = server.takeRequest();

        String serializedThumbnail = serializer.write(requestThumbnail);

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedThumbnail, request.getBody().readUtf8());
        assertEquals("/api/v3.0/aas/asset-information/thumbnail", request.getPath());
    }


    @Test
    public void testDeleteThumbnail() throws InterruptedException, ClientException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell();
        Resource requestThumbnail = new DefaultResource();
        AssetInformation assetInformation = new DefaultAssetInformation();
        assetInformation.setDefaultThumbnail(requestThumbnail);
        requestAas.setAssetInformation(assetInformation);

        server.enqueue(new MockResponse().setResponseCode(200));

        aasInterface.deleteThumbnail();

        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/asset-information/thumbnail", request.getPath());
    }


    @Test
    public void testGetAllSubmodelReferences() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Reference> requestSubmodelReferencePage = Page.<Reference> builder()
                .result(new DefaultReference())
                .metadata(new PagingMetadata.Builder().build())
                .build();
        server.enqueue(new MockResponse().setBody(serializer.write(requestSubmodelReferencePage)));

        List<Reference> responseList = aasInterface.getAllSubmodelReferences();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/submodel-refs", request.getPath());

        assertEquals(requestSubmodelReferencePage.getContent(), responseList);
    }


    @Test
    public void testPostSubmodelReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Reference requestSubmodelReference = new DefaultReference();
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializer.write(requestSubmodelReference)));

        Reference responseSubmodelReference = aasInterface.postSubmodelReference(requestSubmodelReference);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/api/v3.0/aas/submodel-refs", request.getPath());
        assertEquals(requestSubmodelReference, responseSubmodelReference);
    }


    @Test
    public void testDeleteSubmodelReference() throws InterruptedException, ClientException {
        server.enqueue(new MockResponse().setResponseCode(204));
        String requestSubmodelId = "submodelId";

        aasInterface.deleteSubmodelReference(requestSubmodelId);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/submodel-refs/" + EncodingHelper.base64Encode(requestSubmodelId), request.getPath());
    }


    @Test
    public void testDeleteSubmodel() throws InterruptedException, ClientException {
        server.enqueue(new MockResponse().setResponseCode(204));
        String requestSubmodelId = "submodelId";

        aasInterface.deleteSubmodel(requestSubmodelId);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/aas/submodels/" + EncodingHelper.base64Encode(requestSubmodelId), request.getPath());
    }
}
