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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AASRepositoryInterfaceTest {

    private static AASRepositoryInterface AASRepositoryInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;
    private static List<AssetAdministrationShell> requestAssetAdministrationShellList;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.requireClientAuth();
        server.start();
        URI serviceUri = server.url("/example.com/api/v3.0").uri();
        AASRepositoryInterface = new AASRepositoryInterface(serviceUri);
        serializer = new JsonApiSerializer();

        requestAssetAdministrationShellList = new ArrayList<>();
        requestAssetAdministrationShellList.add(getRequestAssetAdministrationShell("idShort1", "globalAssetId1"));
        requestAssetAdministrationShellList.add(getRequestAssetAdministrationShell("idShort2", "globalAssetId2"));

    }


    @NotNull
    private static AssetAdministrationShell getRequestAssetAdministrationShell(String idShort, String globalAssetId) {
        AssetAdministrationShell requestAssetAdministrationShell = new DefaultAssetAdministrationShell.Builder().build();
        requestAssetAdministrationShell.setIdShort(idShort);
        requestAssetAdministrationShell.setId(idShort);
        String requestGlobalAssetId = Base64.getUrlEncoder().encodeToString(globalAssetId.getBytes()) + "/";
        AssetInformation requestAssetInformation = new DefaultAssetInformation.Builder()
                .globalAssetId(requestGlobalAssetId).build();
        requestAssetAdministrationShell.setAssetInformation(requestAssetInformation);
        return requestAssetAdministrationShell;
    }


    @Test
    public void testGetAASPage() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<AssetAdministrationShell> aasPage = Page.<AssetAdministrationShell> builder()
                .result(requestAssetAdministrationShellList.get(0))
                .metadata(new PagingMetadata.Builder().cursor("1").build())
                .build();

        String serializedAasPage = serializer.write(aasPage);
        server.enqueue(new MockResponse().setBody(serializedAasPage));

        Page<AssetAdministrationShell> responseAssetAdministrationShellPage = AASRepositoryInterface.get(
                new PagingInfo.Builder()
                        .limit(1)
                        .build());
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example.com/api/v3.0/shells/?limit=1", request.getPath());
        assertEquals("1", responseAssetAdministrationShellPage.getMetadata().getCursor());
    }


    @Test
    public void postAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAssetAdministrationShell = requestAssetAdministrationShellList.get(0);
        String serializedAas = serializer.write(requestAssetAdministrationShell);
        server.enqueue(new MockResponse().setBody(serializedAas));

        AssetAdministrationShell responseAssetAdministrationShell = AASRepositoryInterface.post(
                requestAssetAdministrationShell);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestAssetAdministrationShell, responseAssetAdministrationShell);
        assertEquals("/example.com/api/v3.0/shells/", request.getPath());
    }


    @Test
    public void testGetAllAssetAdministrationShellsAsReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<Reference> requestAasReferenceList = new ArrayList<>();
        requestAasReferenceList.add(new DefaultReference());

        String serializedAasReferenceList = serializer.write(requestAasReferenceList);
        server.enqueue(new MockResponse().setBody(serializedAasReferenceList));

        List<Reference> responseAasReferenceList = AASRepositoryInterface.getAllAsReference();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example.com/api/v3.0/shells/$reference", request.getPath());
        assertEquals(responseAasReferenceList, requestAasReferenceList);
    }


    @Test
    public void delete() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAssetAdministrationShell = requestAssetAdministrationShellList.get(0);
        String serializedAas = serializer.write(requestAssetAdministrationShell);
        String requestAasIdentifier = requestAssetAdministrationShell.getId();
        server.enqueue(new MockResponse().setBody(serializedAas));

        AASRepositoryInterface.delete(requestAasIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example.com/api/v3.0/shells/" +
                Base64.getUrlEncoder().encodeToString(requestAasIdentifier.getBytes()) + "/", request.getPath());
    }


    @Test
    public void testGetAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = requestAssetAdministrationShellList.get(0);

        String serializedAas = serializer.write(requestAas);
        server.enqueue(new MockResponse().setBody(serializedAas));

        AssetAdministrationShell responseAas = AASRepositoryInterface.getAASInterface(requestAas.getId()).get();

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example.com/api/v3.0/shells/" + Base64.getUrlEncoder().encodeToString(requestAas.getId().getBytes()) + "/", request.getPath());
        assertEquals(requestAas, responseAas);
    }
}
