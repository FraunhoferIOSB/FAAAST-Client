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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ConceptDescriptionRepositoryInterfaceTest {

    private static ConceptDescriptionRepositoryInterface conceptDescriptionRepositoryInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        conceptDescriptionRepositoryInterface = new ConceptDescriptionRepositoryInterface(
                server.url("api/v3.0").uri());
        serializer = new JsonApiSerializer();
    }


    @Test
    public void testGetAll() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<ConceptDescription> requestConceptDescriptions = new ArrayList<>();
        requestConceptDescriptions.add(new DefaultConceptDescription());
        String serializedConceptDescription = serializer.write(requestConceptDescriptions);
        server.enqueue(new MockResponse().setBody(serializedConceptDescription));

        List<ConceptDescription> responseServiceDescription = conceptDescriptionRepositoryInterface.getAll();
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/concept-descriptions", request.getPath());
        assertEquals(requestConceptDescriptions, responseServiceDescription);
    }


    @Test
    public void testPost() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ConceptDescription requestConceptDescription = new DefaultConceptDescription();
        String cdIdentifier = EncodingHelper.base64UrlEncode("cdIdentifier");
        requestConceptDescription.setId(cdIdentifier);

        String serializedConceptDescription = serializer.write(requestConceptDescription);
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(serializedConceptDescription));

        ConceptDescription returnConceptDescription = conceptDescriptionRepositoryInterface.post(requestConceptDescription);
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals(requestConceptDescription, returnConceptDescription);
        assertEquals("/api/v3.0/concept-descriptions", request.getPath());
    }


    @Test
    public void testGetById() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ConceptDescription requestConceptDescription = new DefaultConceptDescription();
        String cdIdentifier = "cdIdentifier";
        requestConceptDescription.setId(cdIdentifier);

        String serializedConceptDescription = serializer.write(requestConceptDescription);
        server.enqueue(new MockResponse().setBody(serializedConceptDescription));

        ConceptDescription responseServiceDescription = conceptDescriptionRepositoryInterface.get(requestConceptDescription.getId());
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/concept-descriptions/" + EncodingHelper.base64UrlEncode(cdIdentifier), request.getPath());
        assertEquals(requestConceptDescription, responseServiceDescription);
    }


    @Test
    public void testPut() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ConceptDescription requestConceptDescription = new DefaultConceptDescription();
        String cdIdentifier = "cdIdentifier";
        requestConceptDescription.setId(cdIdentifier);

        String serializedConceptDescription = serializer.write(requestConceptDescription);
        server.enqueue(new MockResponse().setResponseCode(204));

        conceptDescriptionRepositoryInterface.put(requestConceptDescription, cdIdentifier);
        RecordedRequest request = server.takeRequest();

        assertEquals("PUT", request.getMethod());
        assertEquals(serializedConceptDescription, request.getBody().readUtf8());
        assertEquals("/api/v3.0/concept-descriptions/" + EncodingHelper.base64UrlEncode(cdIdentifier), request.getPath());
    }


    @Test
    public void testDelete() throws InterruptedException, ClientException {
        ConceptDescription requestConceptDescription = new DefaultConceptDescription();
        String cdIdentifier = "cdIdentifier";
        requestConceptDescription.setId(cdIdentifier);

        server.enqueue(new MockResponse().setResponseCode(204));

        conceptDescriptionRepositoryInterface.delete(requestConceptDescription.getId());
        RecordedRequest request = server.takeRequest();

        assertEquals("DELETE", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/concept-descriptions/" + EncodingHelper.base64UrlEncode(cdIdentifier), request.getPath());
    }
}
