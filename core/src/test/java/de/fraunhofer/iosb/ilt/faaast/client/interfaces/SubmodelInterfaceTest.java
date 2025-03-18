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
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.IdShortPath;
import de.fraunhofer.iosb.ilt.faaast.service.model.InMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.model.TypedInMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.InvalidRequestException;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.Datatype;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.PropertyValue;
import de.fraunhofer.iosb.ilt.faaast.service.typing.ElementValueTypeInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static org.apache.commons.fileupload.FileUploadBase.CONTENT_DISPOSITION;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_TYPE;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


public class SubmodelInterfaceTest {
    private static SubmodelInterface submodelInterface;
    private static MockWebServer server;
    private static JsonApiSerializer serializer;
    private static Submodel requestSubmodel;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("api/v3.0/submodel").uri();
        submodelInterface = new SubmodelInterface(serviceUri);
        serializer = new JsonApiSerializer();

        instantiateSubmodel();
    }


    @NotNull
    private static List<SubmodelElement> getElements() {
        List<SubmodelElement> requestSubmodelElements = new ArrayList<>();

        SubmodelElement entitySubmodelElement = new DefaultEntity.Builder().idShort("entityId").build();
        requestSubmodelElements.add(entitySubmodelElement);

        SubmodelElement operationSubmodelElement = new DefaultOperation.Builder().idShort("operationId").build();
        requestSubmodelElements.add(operationSubmodelElement);

        return requestSubmodelElements;
    }


    private static void instantiateSubmodel() {
        requestSubmodel = new DefaultSubmodel();
        String requestSubmodelIdShort = "idShort";
        String requestSubmodelId = EncodingHelper.base64UrlEncode(requestSubmodelIdShort);
        requestSubmodel.setId(requestSubmodelId);
        requestSubmodel.setSubmodelElements(getElements());
    }


    @Test
    public void testGetDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setBody(serializedSubmodel));

        Submodel responseSubmodel = submodelInterface.get();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel", request.getPath());
        assertEquals(requestSubmodel, responseSubmodel);
    }


    @Test
    public void testPut() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setResponseCode(204));
        submodelInterface.put(requestSubmodel);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedSubmodel, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel", request.getPath());
    }


    @Test
    public void testPatchDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setResponseCode(204));
        submodelInterface.patch(requestSubmodel);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedSubmodel, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/?level=core", request.getPath());
    }


    @Test
    public void testGetAllElements() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<SubmodelElement> requestSubmodelElementPage = Page.<SubmodelElement> builder()
                .result(new DefaultProperty())
                .metadata(new PagingMetadata.Builder().build())
                .build();
        String serializedSubmodelElements = serializer.write(requestSubmodelElementPage);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElements));
        List<SubmodelElement> responseSubmodelElements = submodelInterface.getAllElements();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(requestSubmodelElementPage.getContent(), responseSubmodelElements);
        assertEquals("/api/v3.0/submodel/submodel-elements", request.getPath());
    }


    @Test
    public void testPostElement() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedSubmodelElement));
        SubmodelElement responseSubmodelElement = submodelInterface.postElement(requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements", request.getPath());
    }


    @Test
    public void testGetElementDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        SubmodelElement responseSubmodelElement = submodelInterface.getElement(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort(), request.getPath());
    }


    @Test
    public void testGetElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Double value = 2.0;
        String serializedPropertyValue = serializer.write(value);
        server.enqueue(new MockResponse().setBody(serializedPropertyValue));
        ElementValueTypeInfo propertyTypeInfo = ElementValueTypeInfo.builder()
                .datatype(Datatype.DOUBLE)
                .type(PropertyValue.class)
                .build();
        IdShortPath idShort = new IdShortPath.Builder()
                .idShort(requestSubmodel.getSubmodelElements().get(0).getIdShort())
                .build();

        PropertyValue responseSubmodelElementValue = submodelInterface.getElementValue(idShort, propertyTypeInfo);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/$value", idShort), request.getPath());
        assertEquals(value, Double.valueOf(responseSubmodelElementValue.getValue().asString()));
    }


    @Test
    public void testPatchElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        DefaultProperty property = new DefaultProperty.Builder().value("2.0").valueType(DataTypeDefXsd.FLOAT).idShort("value").build();
        String serializedPropertyValue = serializer.write(property, OutputModifier.DEFAULT);
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = new IdShortPath.Builder()
                .idShort(requestSubmodel.getSubmodelElements().get(0).getIdShort())
                .build();
        submodelInterface.patchElementValue(idShort, property);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedPropertyValue, request.getBody().readUtf8());
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/$value", idShort), request.getPath());
    }


    @Test
    public void testPostElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        SubmodelElement responseSubmodelElement = submodelInterface.postElement(
                idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort(), request.getPath());
    }


    @Test
    public void testPutElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.putElement(idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedSubmodelElement, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort(), request.getPath());
    }


    @Test
    public void testPatchElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.patchElement(idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedSubmodelElement, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort(), request.getPath());
    }


    @Test
    public void testDeleteElement() throws InterruptedException, ClientException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.deleteElement(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort(), request.getPath());
    }


    @Test
    public void testGetAttachment() throws InterruptedException, ClientException, InvalidRequestException {
        byte[] content = "attachment-content".getBytes();
        InMemoryFile requestAttachment = new InMemoryFile.Builder()
                .content(content)
                .path("attachment.pdf")
                .build();

        Buffer buffer = new Buffer();
        buffer.write(requestAttachment.getContent());
        MockResponse response = new MockResponse()
                .setResponseCode(200)
                .addHeader(CONTENT_TYPE, "application/pdf")
                .addHeader(CONTENT_DISPOSITION, "attachment; fileName=\"attachment.pdf\"")
                .setBody(buffer);

        server.enqueue(response);

        IdShortPath idShort = IdShortPath.parse("idShort");
        InMemoryFile responseAttachment = submodelInterface.getAttachment(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", idShort), request.getPath());
        assertEquals(requestAttachment, responseAttachment);
    }


    @Test
    public void testPutAttachment() throws InterruptedException, ClientException, IOException {
        server.enqueue(new MockResponse().setResponseCode(204));
        IdShortPath idShort = IdShortPath.parse("idShort");

        byte[] expectedThumbnailContent = "attachment-content".getBytes();
        TypedInMemoryFile requestAttachment = new TypedInMemoryFile.Builder()
                .content(expectedThumbnailContent)
                .path("TestFile.png")
                .contentType("image/png")
                .build();

        submodelInterface.putAttachment(idShort, requestAttachment);

        var recordedRequest = server.takeRequest();

        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", idShort), recordedRequest.getPath());

        String contentTypeHeader = recordedRequest.getHeader("Content-Type");
        assertNotNull(contentTypeHeader);
        assertTrue(contentTypeHeader.startsWith("multipart/form-data"));

        Path expectedPayloadPath = Paths.get("src/test/resources/expectedMultiPartPayloadAttachment.txt");
        String expectedPayload = Files.readString(expectedPayloadPath, StandardCharsets.UTF_8);
        String actualRequestBody = recordedRequest.getBody().readUtf8();

        // Remove trailing linebreaks to assure consistency across platforms.
        if (expectedPayload.endsWith("\n")) {
            expectedPayload = expectedPayload.substring(0, expectedPayload.length() - 1);
        }
        if (actualRequestBody.endsWith("\n")) {
            actualRequestBody = actualRequestBody.substring(0, actualRequestBody.length() - 1);
        }
        assertEquals(expectedPayload, actualRequestBody);
    }


    @Test
    public void testDeleteFileByPath() throws InterruptedException, ClientException {
        server.enqueue(new MockResponse().setResponseCode(200));
        IdShortPath idShort = IdShortPath.parse("idShort");

        submodelInterface.deleteAttachment(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", idShort), request.getPath());
    }


    @Test
    public void testInvokeOperationSync() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, DatatypeConfigurationException {
        OperationResult requestOperationResult = new DefaultOperationResult();
        String serializedOperationResult = serializer.write(requestOperationResult);
        server.enqueue(new MockResponse().setBody(serializedOperationResult));

        List<OperationVariable> inputVariables = new ArrayList<>();
        inputVariables.add(new DefaultOperationVariable());

        DatatypeFactory factory = DatatypeFactory.newInstance();

        IdShortPath idShort = new IdShortPath.Builder()
                .idShort(requestSubmodel.getSubmodelElements().get(1).getIdShort())
                .build();
        OperationResult responseOperationResult = submodelInterface.invokeOperationSync(
                idShort, inputVariables, factory.newDuration(2500));

        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestOperationResult, responseOperationResult);
        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/invoke", idShort), request.getPath());
    }
}
