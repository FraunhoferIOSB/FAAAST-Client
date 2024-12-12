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
import de.fraunhofer.iosb.ilt.faaast.client.query.AASSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.ApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fraunhofer.iosb.ilt.faaast.service.model.asset.AssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.GlobalAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.SpecificAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class AASRepositoryInterfaceTest {

    private static AASRepositoryInterface aasRepositoryInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;
    private static List<AssetAdministrationShell> requestAssetAdministrationShellList;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.requireClientAuth();
        server.start();
        URI serviceUri = server.url("/example/api/v3.0").uri();
        aasRepositoryInterface = new AASRepositoryInterface(serviceUri);
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
        String requestGlobalAssetId = EncodingHelper.base64UrlEncode(globalAssetId);
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

        Page<AssetAdministrationShell> responseAssetAdministrationShellPage = aasRepositoryInterface.get(
                new PagingInfo.Builder()
                        .limit(1)
                        .build());
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shells/?limit=1", request.getPath());
        assertEquals("1", responseAssetAdministrationShellPage.getMetadata().getCursor());
    }


    @Test
    public void testGetAllAssetAdministrationShellsWithPagingWithSearchCriteria()
            throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, JSONException {
        Page<AssetAdministrationShell> aasPage = Page.<AssetAdministrationShell> builder().result(requestAssetAdministrationShellList)
                .metadata(new PagingMetadata.Builder().build()).build();
        String serializedAasPage = serializer.write(aasPage);
        server.enqueue(new MockResponse().setBody(serializedAasPage));

        List<AssetIdentification> assetIdentificationList = createAssetIdentificationList();
        List<String> serializedAssetIdentificationList = serializeAssetIdentificationList(assetIdentificationList);

        Page<AssetAdministrationShell> responseAssetAdministrationShellPage = aasRepositoryInterface.get(
                new PagingInfo.Builder()
                        .cursor("1")
                        .limit(1)
                        .build(),
                new AASSearchCriteria.Builder()
                        .assetIds(assetIdentificationList)
                        .idShort("idShort")
                        .build());

        RecordedRequest request = server.takeRequest();
        List<String> assetIdentifications = extractAssetIdsfromUrl(request);

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        JSONAssert.assertEquals(serializedAssetIdentificationList.get(0), assetIdentifications.get(0), JSONCompareMode.NON_EXTENSIBLE);
        JSONAssert.assertEquals(serializedAssetIdentificationList.get(1), assetIdentifications.get(1), JSONCompareMode.NON_EXTENSIBLE);
        assertNull(responseAssetAdministrationShellPage.getMetadata().getCursor());
    }


    private List<String> serializeAssetIdentificationList(List<AssetIdentification> assetIdentificationList) throws SerializationException, UnsupportedModifierException {
        String serializedGlobalAssetId = serializer.write(new DefaultSpecificAssetId.Builder().value(assetIdentificationList.get(0).getValue()).name("globalAssetId").build());
        String serializedSpecificAssetId = serializer.write(
                new DefaultSpecificAssetId.Builder()
                        .value(assetIdentificationList.get(1).getValue())
                        .name(((SpecificAssetIdentification) assetIdentificationList.get(1)).getKey())
                        .build());
        List<String> serializedAssetIdentificationList = new ArrayList<>();
        serializedAssetIdentificationList.add(serializedGlobalAssetId);
        serializedAssetIdentificationList.add(serializedSpecificAssetId);
        return serializedAssetIdentificationList;
    }


    private List<AssetIdentification> createAssetIdentificationList() {
        List<AssetIdentification> assetIdentificationList = new ArrayList<>();
        GlobalAssetIdentification globalAssetId = new GlobalAssetIdentification.Builder().value("assetLink1").build();
        assetIdentificationList.add(globalAssetId);
        SpecificAssetIdentification specificAssetId = new SpecificAssetIdentification.Builder().key("specificAssetId").value("assetLink2").build();
        assetIdentificationList.add(specificAssetId);
        return assetIdentificationList;
    }


    private List<String> extractAssetIdsfromUrl(RecordedRequest request) {
        Pattern pattern = Pattern.compile("assetIds=([^&]*)");
        String requestString = request.getPath();
        assert requestString != null;
        Matcher matcher = pattern.matcher(requestString);
        List<String> assetIdentifications = new ArrayList<>();

        if (matcher.find()) {
            String[] assetIds = matcher.group(1).split(",");
            assetIdentifications.add(EncodingHelper.base64Decode(assetIds[0]));
            assetIdentifications.add(EncodingHelper.base64Decode(assetIds[1]));
        }
        else {
            assetIdentifications.add("empty");
        }

        return assetIdentifications;
    }


    @Test
    public void postAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAssetAdministrationShell = requestAssetAdministrationShellList.get(0);
        String serializedAas = serializer.write(requestAssetAdministrationShell);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedAas));

        AssetAdministrationShell responseAssetAdministrationShell = aasRepositoryInterface.post(
                requestAssetAdministrationShell);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestAssetAdministrationShell, responseAssetAdministrationShell);
        assertEquals("/example/api/v3.0/shells", request.getPath());
    }


    @Test
    public void testGetAllAssetAdministrationShellsAsReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<Reference> requestAasReferenceList = new ArrayList<>();
        requestAasReferenceList.add(new DefaultReference());

        String serializedAasReferenceList = serializer.write(requestAasReferenceList);
        server.enqueue(new MockResponse().setBody(serializedAasReferenceList));

        List<Reference> responseAasReferenceList = aasRepositoryInterface.getAllAsReference();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shells/$reference", request.getPath());
        assertEquals(responseAasReferenceList, requestAasReferenceList);
    }


    @Test
    public void delete() throws InterruptedException, ClientException {
        AssetAdministrationShell requestAssetAdministrationShell = requestAssetAdministrationShellList.get(0);
        String requestAasIdentifier = requestAssetAdministrationShell.getId();
        server.enqueue(new MockResponse().setResponseCode(204));

        aasRepositoryInterface.delete(requestAasIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shells/" + EncodingHelper.base64UrlEncode(requestAasIdentifier), request.getPath());
    }


    @Test
    public void testGetAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = requestAssetAdministrationShellList.get(0);

        String serializedAas = serializer.write(requestAas);
        server.enqueue(new MockResponse().setBody(serializedAas));

        AssetAdministrationShell responseAas = aasRepositoryInterface.getAASInterface(requestAas.getId()).get();

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/example/api/v3.0/shells/" + EncodingHelper.base64UrlEncode(requestAas.getId()), request.getPath());
        assertEquals(requestAas, responseAas);
    }
}
