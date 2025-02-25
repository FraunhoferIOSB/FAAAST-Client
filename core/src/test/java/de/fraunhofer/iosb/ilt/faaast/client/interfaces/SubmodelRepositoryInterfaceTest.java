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
import de.fraunhofer.iosb.ilt.faaast.client.query.SubmodelSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.IdShortPath;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.Datatype;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.PropertyValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.primitive.StringValue;
import de.fraunhofer.iosb.ilt.faaast.service.typing.ElementValueTypeInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SubmodelRepositoryInterfaceTest {
    private static SubmodelRepositoryInterface submodelRepositoryInterface;
    private static MockWebServer server;
    private static JsonApiSerializer serializer;
    private static List<Submodel> requestSubmodelList;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("api/v3.0").uri();
        submodelRepositoryInterface = new SubmodelRepositoryInterface(serviceUri);

        serializer = new JsonApiSerializer();

        requestSubmodelList = new ArrayList<>();
        requestSubmodelList.add(createSubmodel("idShort1", "key1"));
        requestSubmodelList.add(createSubmodel("idShort2", "key2"));
    }


    @After
    public void teardown() throws IOException {
        server.shutdown();
    }


    private static List<SubmodelElement> createSubmodelElements() {
        List<SubmodelElement> requestSubmodelElements = new ArrayList<>();

        SubmodelElement entitySubmodelElement = new DefaultEntity.Builder().idShort("entityId").build();
        requestSubmodelElements.add(entitySubmodelElement);

        SubmodelElement operationSubmodelElement = new DefaultOperation.Builder().idShort("operationId").build();
        requestSubmodelElements.add(operationSubmodelElement);

        return requestSubmodelElements;
    }


    private static Submodel createSubmodel(String idShort, String key) {
        Submodel submodel = new DefaultSubmodel();
        submodel.setIdShort(idShort);
        submodel.setId(idShort);

        Reference semanticIdReference = new DefaultReference.Builder().build();
        List<Key> keyList = new ArrayList<>();
        Key semanticIdKey = new DefaultKey.Builder().build();
        semanticIdKey.setValue(key);
        keyList.add(semanticIdKey);
        semanticIdReference.setKeys(keyList);
        submodel.setSemanticId(semanticIdReference);

        submodel.setSubmodelElements(createSubmodelElements());

        return submodel;
    }


    @Test
    public void testGetAll() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Submodel> requestSubmodelPage = Page.<Submodel> builder()
                .result(new DefaultSubmodel())
                .metadata(new PagingMetadata.Builder().build())
                .build();
        String serializedSubmodelList = serializer.write(requestSubmodelPage);
        server.enqueue(new MockResponse().setBody(serializedSubmodelList));

        List<Submodel> responseSubmodelList = submodelRepositoryInterface.getAll();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodels", request.getPath());
        assertEquals(requestSubmodelPage.getContent(), responseSubmodelList);
    }


    @Test
    public void testGetAllReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Reference> requestSubmodelReferencePage = Page.<Reference> builder()
                .result(new DefaultReference())
                .metadata(new PagingMetadata.Builder().build())
                .build();

        String serializedSubmodelReferencesList = serializer.write(requestSubmodelReferencePage);
        server.enqueue(new MockResponse().setBody(serializedSubmodelReferencesList));

        List<Reference> responseSubmodelReferences = submodelRepositoryInterface.getAllReferences(SubmodelSearchCriteria.DEFAULT);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodels/$reference?level=core", request.getPath());
        assertEquals(requestSubmodelReferencePage.getContent(), responseSubmodelReferences);
    }


    @Test
    public void testPost() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel requestSubmodel = requestSubmodelList.get(0);
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedSubmodel));

        Submodel responseSubmodel = submodelRepositoryInterface.post(requestSubmodel);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodel, responseSubmodel);
        assertEquals("/api/v3.0/submodels", request.getPath());
    }


    @Test
    public void testDelete() throws InterruptedException, ClientException {
        Submodel requestSubmodel = requestSubmodelList.get(0);
        String requestSubmodelIdentifier = requestSubmodel.getId();
        server.enqueue(new MockResponse().setResponseCode(204));

        submodelRepositoryInterface.delete(requestSubmodelIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodels/" + EncodingHelper.base64UrlEncode(requestSubmodelIdentifier), request.getPath());
    }


    @Test
    public void testGetSubmodel() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel requestSubmodel = requestSubmodelList.get(0);

        String serializedAas = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setBody(serializedAas));

        Submodel responseSubmodel = submodelRepositoryInterface.getSubmodelInterface(requestSubmodel.getId()).get();

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodels/" + EncodingHelper.base64UrlEncode(requestSubmodel.getId()), request.getPath());
        assertEquals(requestSubmodel, responseSubmodel);
    }


    @Test
    public void testGetSubmodelElementValueRequest() throws ClientException, SerializationException, UnsupportedModifierException {
        PropertyValue requestValue = new PropertyValue.Builder().value(new StringValue("de")).build();
        String serializedValue = serializer.write(requestValue);
        server.enqueue(new MockResponse().setBody(serializedValue));

        PropertyValue result = submodelRepositoryInterface.getSubmodelInterface("submodelId").getElementValue(
                new IdShortPath.Builder().idShort("idShort").build(),
                new ElementValueTypeInfo.Builder()
                        .datatype(Datatype.STRING)
                        .type(PropertyValue.class)
                        .build());

        assertEquals("de", result.getValue().asString());
    }


    @Test
    public void testGetSubmodelElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        String requestSubmodelId = requestSubmodelList.get(0).getId();
        Double value = 2.0;
        String serializedPropertyValue = serializer.write(value);
        server.enqueue(new MockResponse().setBody(serializedPropertyValue));
        ElementValueTypeInfo propertyTypeInfo = ElementValueTypeInfo.builder().datatype(Datatype.DOUBLE).type(PropertyValue.class).build();
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelList.get(0).getId()).build();

        PropertyValue responseSubmodelElementValue = submodelRepositoryInterface.getSubmodelInterface(requestSubmodelId).getElementValue(idShort, propertyTypeInfo);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());

        assertEquals(
                String.format("/api/v3.0/submodels/%s/submodel-elements/%s/$value",
                        EncodingHelper.base64UrlEncode(requestSubmodelId),
                        idShort),
                request.getPath());
        assertEquals(value, Double.valueOf(responseSubmodelElementValue.getValue().asString()));
    }


    @Test
    public void testPatchSubmodelElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        DefaultProperty property = new DefaultProperty.Builder().value("2.0").valueType(DataTypeDefXsd.FLOAT).idShort("value").build();
        String serializedPropertyValue = serializer.write(property, OutputModifier.DEFAULT);
        String requestSubmodelId = requestSubmodelList.get(0).getId();
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelList.get(0).getId()).build();

        submodelRepositoryInterface.getSubmodelInterface(requestSubmodelId).patchElementValue(idShort, property);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedPropertyValue, request.getBody().readUtf8());
        assertEquals(
                String.format("/api/v3.0/submodels/%s/submodel-elements/%s/$value",
                        EncodingHelper.base64UrlEncode(requestSubmodelId),
                        idShort),
                request.getPath());
    }
}
