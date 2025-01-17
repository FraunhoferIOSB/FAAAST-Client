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

import de.fraunhofer.iosb.ilt.faaast.client.SimpleAASTest;
import de.fraunhofer.iosb.ilt.faaast.client.exception.ClientException;
import de.fraunhofer.iosb.ilt.faaast.service.Service;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionException;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.config.ServiceConfig;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.HttpEndpointConfig;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationException;
import de.fraunhofer.iosb.ilt.faaast.service.exception.EndpointException;
import de.fraunhofer.iosb.ilt.faaast.service.exception.MessageBusException;
import de.fraunhofer.iosb.ilt.faaast.service.filestorage.memory.FileStorageInMemoryConfig;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.internal.MessageBusInternalConfig;
import de.fraunhofer.iosb.ilt.faaast.service.model.IdShortPath;
import de.fraunhofer.iosb.ilt.faaast.service.model.TypedInMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.PersistenceException;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemoryConfig;
import de.fraunhofer.iosb.ilt.faaast.service.util.DeepCopyHelper;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;


public class FileHandlingTest {

    private static AASRepositoryInterface aasRepository;
    private static SubmodelRepositoryInterface submodelRepository;
    private static Service service;
    private static final int PORT = 8080;
    private static Environment environment;

    @Before
    public void setup() throws IOException, MessageBusException, EndpointException, PersistenceException, ConfigurationException, AssetConnectionException, URISyntaxException {
        environment = SimpleAASTest.createEnvironment();
        service = new Service(ServiceConfig.builder()
                .core(CoreConfig.builder()
                        .requestHandlerThreadPoolSize(2)
                        .build())
                .persistence(PersistenceInMemoryConfig.builder().initialModel(DeepCopyHelper.deepCopy(environment)).build())
                .endpoint(HttpEndpointConfig.builder().port(PORT).ssl(false).build())
                .messageBus(MessageBusInternalConfig.builder().build())
                .fileStorage(FileStorageInMemoryConfig.builder().build())
                .build());
        service.start();
        URI uri = new URI("http://localhost:" + PORT + "/api/v3.0");
        aasRepository = new AASRepositoryInterface(uri);
        submodelRepository = new SubmodelRepositoryInterface(uri);
    }


    @After
    public void tearDown() {
        if (service != null) {
            System.out.println("Shutting down the test server...");
            service.stop();
            System.out.println("Test server stopped.");
        }
    }


    @Test
    public void testThumbnail() throws ClientException {
        TypedInMemoryFile expected = new TypedInMemoryFile.Builder().content(new byte[20]).path("TestFile.png").contentType("image/png").build();
        aasRepository.getAASInterface(environment.getAssetAdministrationShells().get(0).getId()).putThumbnail(expected);
        TypedInMemoryFile actual = aasRepository.getAASInterface(environment.getAssetAdministrationShells().get(0).getId()).getThumbnail();
        assertEquals(expected, actual);
    }


    @Test
    public void testAttachment() throws ClientException {
        TypedInMemoryFile expected = new TypedInMemoryFile.Builder().content(new byte[20]).path("OperatingManual.pdf").contentType("application/pdf").build();
        IdShortPath idShortPath = IdShortPath.combine(IdShortPath.parse("OperatingManual"), IdShortPath.parse("DigitalFile_PDF"));
        String submodelId = "http://i40.customer.com/type/1/1/1A7B62B529F19152";
        submodelRepository.getSubmodelInterface(submodelId).putAttachment(idShortPath, expected);
        TypedInMemoryFile actual = submodelRepository.getSubmodelInterface(submodelId).getAttachment(idShortPath);
        assertEquals(expected, actual);
    }
}
