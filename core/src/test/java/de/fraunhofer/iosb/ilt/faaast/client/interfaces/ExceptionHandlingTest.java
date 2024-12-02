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

import de.fraunhofer.iosb.ilt.faaast.client.exception.*;
import java.io.IOException;
import java.net.URI;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;


public class ExceptionHandlingTest {

    private SubmodelRepositoryInterface submodelRepositoryInterface;
    private MockWebServer server;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        URI serviceUri = server.url("/api/v3.0").uri();
        submodelRepositoryInterface = new SubmodelRepositoryInterface(serviceUri);
    }


    @Test
    public void testGetListBadRequestException() {
        server.enqueue(new MockResponse().setResponseCode(400));

        assertThrows(BadRequestException.class, () -> {
            submodelRepositoryInterface.getAll();
        });
    }


    @Test
    public void testGetNotFoundException() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> {
            submodelRepositoryInterface.getSubmodelInterface("wrongId").get();
        });
    }


    @Test
    public void testPostConflictException() {
        server.enqueue(new MockResponse().setResponseCode(409));

        assertThrows(ConflictException.class, () -> {
            submodelRepositoryInterface.post(new DefaultSubmodel.Builder().id("id").build());
        });
        // Nullpointer Exception if id is not set
    }


    @Test
    public void testPutForbiddenException() {
        server.enqueue(new MockResponse().setResponseCode(403));

        assertThrows(ForbiddenException.class, () -> {
            submodelRepositoryInterface.put("path", new DefaultSubmodel());
        });
    }


    @Test
    public void testDeleteInternalServerErrorException() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(InternalServerErrorException.class, () -> {
            submodelRepositoryInterface.delete("path");
        });
    }
}
