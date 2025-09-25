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
import de.fraunhofer.iosb.ilt.faaast.service.model.InMemoryFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SerializationInterfaceTest {

    private static SerializationInterface serializationInterface;
    private static ApiSerializer serializer;
    private static MockWebServer server;
    private static List<String> aasIds;
    private static List<String> submodelIds;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        serializationInterface = new SerializationInterface(
                server.url("api/v3.0").uri());
        serializer = new JsonApiSerializer();
        aasIds = new ArrayList<>();
        submodelIds = new ArrayList<>();
    }


    @Test
    public void testGetAASX() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        InMemoryFile expected = new InMemoryFile();
        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        InMemoryFile actual = serializationInterface.getAASXPackage(aasIds, submodelIds);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/serialization", request.getPath());
        assertEquals(expected, actual);
    }


    @Test
    public void testGetEnvironment() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Environment expected = new DefaultEnvironment();

        server.enqueue(new MockResponse().setBody(serializer.write(expected)));

        Environment actual = serializationInterface.getEnvironment(aasIds, submodelIds);
        RecordedRequest request = server.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals(0, request.getBodySize());
        assertEquals("/api/v3.0/serialization", request.getPath());
        assertEquals(expected, actual);
    }
}
