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
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.operation.ExecutionState;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.operation.OperationResult;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.Datatype;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.PropertyValue;
import de.fraunhofer.iosb.ilt.faaast.service.typing.ElementValueTypeInfo;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static org.junit.Assert.assertEquals;


public class SubmodelInterfaceTest {
    private static SubmodelInterface submodelInterface;
    private static MockWebServer server;
    private static JsonApiSerializer serializer;
    private static Submodel requestSubmodel;
    private static File requestFile;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("api/v3.0/submodel/").uri();
        submodelInterface = new SubmodelInterface(serviceUri);
        serializer = new JsonApiSerializer();

        instantiateSubmodel();

        requestFile = new DefaultFile();
        requestFile.setIdShort("idShort");

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
        String requestSubmodelId = Base64.getUrlEncoder().encodeToString(requestSubmodelIdShort.getBytes());
        requestSubmodel.setId(requestSubmodelId);
        requestSubmodel.setSubmodelElements(getElements());
    }


    @NotNull
    private static OperationResult getOperationResult() {
        OperationResult requestOperationResult = new OperationResult();

        OperationVariable requestOutputArgument = new DefaultOperationVariable();
        List<OperationVariable> requestOutputArgumentList = new ArrayList<>();
        requestOutputArgumentList.add(requestOutputArgument);
        requestOperationResult.setOutputArguments(requestOutputArgumentList);

        OperationVariable requestInoutputArgument = new DefaultOperationVariable();
        List<OperationVariable> requestInoutputArgumentList = new ArrayList<>();
        requestOutputArgumentList.add(requestInoutputArgument);
        requestOperationResult.setInoutputArguments(requestInoutputArgumentList);

        requestOperationResult.setExecutionState(ExecutionState.INITIATED);

        return requestOperationResult;
    }


    @NotNull
    private static OperationHandle getOperationHandle() {
        OperationHandle requestOperationHandle = new DefaultOperationHandle();
        requestOperationHandle.setHandleId("handleId");
        return requestOperationHandle;
    }


    @Test
    public void testGetDefault() throws ClientException, InterruptedException, SerializationException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setBody(serializedSubmodel));

        Submodel responseSubmodel = submodelInterface.get();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel/", request.getPath());
        assertEquals(requestSubmodel, responseSubmodel);
    }


    @Test
    public void testPut() throws ClientException, InterruptedException, SerializationException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setBody(serializedSubmodel));
        submodelInterface.put(requestSubmodel);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedSubmodel, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/", request.getPath());
    }


    @Test
    public void testPatchDefault() throws ClientException, InterruptedException, SerializationException {
        String serializedSubmodel = serializer.write(requestSubmodel);
        server.enqueue(new MockResponse().setBody(serializedSubmodel));
        submodelInterface.patch(requestSubmodel);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedSubmodel, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/?level=core", request.getPath());
    }


    @Test
    public void testGetAllElements() throws SerializationException, InterruptedException, ClientException {
        List<SubmodelElement> requestSubmodelElements = requestSubmodel.getSubmodelElements();
        String serializedSubmodelElements = serializer.write(requestSubmodelElements);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElements));
        List<SubmodelElement> responseSubmodelElements = submodelInterface.getAllElements();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(requestSubmodelElements, responseSubmodelElements);
        assertEquals("/api/v3.0/submodel/submodel-elements/", request.getPath());
    }


    @Test
    public void testPostElement() throws ClientException, InterruptedException, SerializationException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        SubmodelElement responseSubmodelElement = submodelInterface.postElement(requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements/", request.getPath());
    }


    @Test
    public void testGetElementDefault() throws SerializationException, InterruptedException, ClientException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        SubmodelElement responseSubmodelElement = submodelInterface.getElement(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort() + "/", request.getPath());
    }


    @Test
    public void testGetElementValue() throws SerializationException, InterruptedException, ClientException {
        Double value = 2.0;
        String serializedPropertyValue = serializer.write(value);
        server.enqueue(new MockResponse().setBody(serializedPropertyValue));
        ElementValueTypeInfo propertyTypeInfo = ElementValueTypeInfo.builder().datatype(Datatype.DOUBLE).type(PropertyValue.class).build();
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodel.getSubmodelElements().get(0).getIdShort()).build();

        PropertyValue responseSubmodelElementValue = submodelInterface.getElementValue(idShort, propertyTypeInfo);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/api/v3.0/submodel/submodel-elements/" +
                idShort.toString() + "/$value", request.getPath());
        assertEquals(value, Double.valueOf(responseSubmodelElementValue.getValue().asString()));
    }


    @Test
    public void testPatchElementValue() throws SerializationException, InterruptedException, ClientException {
        DefaultProperty property = new DefaultProperty.Builder().value("2.0").valueType(DataTypeDefXsd.FLOAT).idShort("value").build();
        String serializedPropertyValue = serializer.write(property, OutputModifier.DEFAULT);
        server.enqueue(new MockResponse().setBody(serializedPropertyValue));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodel.getSubmodelElements().get(0).getIdShort()).build();
        submodelInterface.patchElementValue(idShort, property);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedPropertyValue, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" +
                idShort.toString() + "/$value", request.getPath());
    }


    @Test
    public void testPostElementByPath() throws ClientException, InterruptedException, SerializationException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        SubmodelElement responseSubmodelElement = submodelInterface.postElement(
                idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestSubmodelElement, responseSubmodelElement);
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort() + "/", request.getPath());
    }


    @Test
    public void testPutElementByPath() throws ClientException, InterruptedException, SerializationException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.putElement(idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedSubmodelElement, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort() + "/", request.getPath());
    }


    @Test
    public void testPatchElementByPath() throws ClientException, InterruptedException, SerializationException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.patchElement(idShort, requestSubmodelElement);
        RecordedRequest request = server.takeRequest();

        assertEquals("PATCH", request.getMethod());
        assertEquals(serializedSubmodelElement, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort() + "/", request.getPath());
    }


    @Test
    public void testDeleteElement() throws ClientException, InterruptedException, SerializationException {
        SubmodelElement requestSubmodelElement = requestSubmodel.getSubmodelElements().get(0);
        String serializedSubmodelElement = serializer.write(requestSubmodelElement);
        server.enqueue(new MockResponse().setBody(serializedSubmodelElement));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestSubmodelElement.getIdShort()).build();
        submodelInterface.deleteElement(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestSubmodelElement.getIdShort() + "/", request.getPath());
    }


    @Test
    public void testGetFileByPath() throws ClientException, InterruptedException, SerializationException {
        String serializedFile = serializer.write(requestFile);
        server.enqueue(new MockResponse().setBody(serializedFile));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestFile.getIdShort()).build();
        File responseFile = submodelInterface.getAttachment(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(requestFile, responseFile);
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestFile.getIdShort() + "/attachment/", request.getPath());
    }


    @Test
    public void testPutFileByPath() throws ClientException, InterruptedException, SerializationException {
        String serializedFile = serializer.write(requestFile);
        server.enqueue(new MockResponse().setBody(serializedFile));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestFile.getIdShort()).build();
        submodelInterface.putAttachment(idShort, requestFile);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedFile, request.getBody().readUtf8());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestFile.getIdShort() + "/attachment/", request.getPath());
    }


    @Test
    public void testDeleteFileByPath() throws ClientException, InterruptedException, SerializationException {
        String serializedFile = serializer.write(requestFile);
        server.enqueue(new MockResponse().setBody(serializedFile));
        IdShortPath idShort = new IdShortPath.Builder().idShort(
                requestFile.getIdShort()).build();
        submodelInterface.deleteAttachment(idShort);
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/submodel/submodel-elements/" + requestFile.getIdShort() + "/attachment/", request.getPath());
    }


    @Test
    public void testInvokeOperationSync() throws ClientException, InterruptedException, SerializationException, DatatypeConfigurationException {
        OperationResult requestOperationResult = new OperationResult();
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
        assertEquals("/api/v3.0/submodel/submodel-elements/" + idShort + "/invoke/", request.getPath());
    }
}
