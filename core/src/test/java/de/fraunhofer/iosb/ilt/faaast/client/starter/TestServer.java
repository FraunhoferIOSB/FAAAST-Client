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
package de.fraunhofer.iosb.ilt.faaast.client.starter;

import de.fraunhofer.iosb.ilt.faaast.client.interfaces.AASRepositoryInterface;
import de.fraunhofer.iosb.ilt.faaast.client.interfaces.ConceptDescriptionRepositoryInterface;
import de.fraunhofer.iosb.ilt.faaast.client.interfaces.SubmodelInterface;
import de.fraunhofer.iosb.ilt.faaast.client.interfaces.SubmodelRepositoryInterface;
import de.fraunhofer.iosb.ilt.faaast.service.Service;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.config.ServiceConfig;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.HttpEndpointConfig;
import de.fraunhofer.iosb.ilt.faaast.service.filestorage.memory.FileStorageInMemoryConfig;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.internal.MessageBusInternalConfig;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemoryConfig;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRange;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;


public class TestServer {

    private static final int PORT = 443;
    private URI uri;
    private Service service;

    @Before
    public void setUp() throws Exception {
        System.out.println("Setting up the test server...");

        service = new Service(ServiceConfig.builder()
                .core(CoreConfig.builder()
                        .requestHandlerThreadPoolSize(2)
                        .build())
                .persistence(PersistenceInMemoryConfig.builder().build())
                .endpoint(HttpEndpointConfig.builder().port(PORT).ssl(false).build())
                .messageBus(MessageBusInternalConfig.builder().build())
                .fileStorage(FileStorageInMemoryConfig.builder().build())
                .build());
        service.start();
        uri = new URI("http://localhost:" + PORT + "/api/v3.0");
        System.out.println("Test server started on port " + PORT);
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
    public void testGetList() throws Exception {
        AASRepositoryInterface aasRepository = new AASRepositoryInterface(uri);
        ConceptDescriptionRepositoryInterface conceptRepository = new ConceptDescriptionRepositoryInterface(uri);
        SubmodelRepositoryInterface submodelRepository = new SubmodelRepositoryInterface(uri);

        AssetAdministrationShell newShell = new DefaultAssetAdministrationShell.Builder()
                .id("https://example.com/testShell")
                .idShort("TestShell")
                .build();
        aasRepository.post(newShell);
        ConceptDescription newConcept = new DefaultConceptDescription.Builder()
                .id("https://example.com/testConcept")
                .idShort("TestConcept")
                .build();
        conceptRepository.post(newConcept);
        Submodel newSubmodel = new DefaultSubmodel.Builder()
                .id("https://example.com/testSubmodel")
                .idShort("TestSubmodel")
                .submodelElements(List.of(
                        new DefaultRange.Builder().idShort("Range").min("100").max("500").build(),
                        new DefaultProperty.Builder().idShort("Property").value("Test").build()))
                .build();
        SubmodelInterface newSubmodelInterface = submodelRepository.getSubmodelInterface(newSubmodel.getId());
        submodelRepository.post(newSubmodel);

        assertEquals(aasRepository.getAll().get(0), newShell);
        assertEquals(conceptRepository.getAll().get(0), newConcept);
        assertEquals(submodelRepository.getAll().get(0), newSubmodel);
        assertEquals(newSubmodelInterface.getAllElements(), newSubmodel.getSubmodelElements());
    }

}
