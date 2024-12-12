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
package de.fraunhofer.iosb.ilt.faaast.client.exception;

import de.fraunhofer.iosb.ilt.faaast.client.interfaces.SubmodelRepositoryInterface;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertThrows;


public class StatusCodeExceptionTest {
    private static MockWebServer server;
    private static SubmodelRepositoryInterface concreteSubclass;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        concreteSubclass = new SubmodelRepositoryInterface(server.url("api/v3.0").uri());
    }


    @Test
    public void testBadRequest() {
        server.enqueue(new MockResponse().setResponseCode(400));
        assertThrows(BadRequestException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(400));
        assertThrows(BadRequestException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testUnauthorized() {
        server.enqueue(new MockResponse().setResponseCode(401));
        assertThrows(UnauthorizedException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(401));
        assertThrows(UnauthorizedException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testForbidden() {
        server.enqueue(new MockResponse().setResponseCode(403));
        assertThrows(ForbiddenException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(403));
        assertThrows(ForbiddenException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404));
        assertThrows(NotFoundException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(404));
        assertThrows(NotFoundException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testMethodNotAllowed() {
        server.enqueue(new MockResponse().setResponseCode(405));
        assertThrows(MethodNotAllowedException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(405));
        assertThrows(MethodNotAllowedException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testConflict() {
        server.enqueue(new MockResponse().setResponseCode(409));
        assertThrows(ConflictException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(409));
        assertThrows(ConflictException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testInternalServerError() {
        server.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(InternalServerErrorException.class, () -> concreteSubclass.getAll());

        server.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(InternalServerErrorException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }
}
