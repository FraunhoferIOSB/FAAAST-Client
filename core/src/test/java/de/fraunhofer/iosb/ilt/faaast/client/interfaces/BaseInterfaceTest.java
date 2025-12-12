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
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;


public class BaseInterfaceTest {
    private static MockWebServer server;
    private static SubmodelRepositoryInterface concreteSubclass;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        concreteSubclass = new SubmodelRepositoryInterface.Builder()
                .endpoint(server.url("api/v3.0").uri())
                .build();
    }


    private Page<Submodel> createPage() {
        return Page.<Submodel> builder()
                .result(Collections.singletonList(new DefaultSubmodel()))
                .metadata(new PagingMetadata.Builder().cursor("cursor").build())
                .build();
    }


    private String serializePage(Page<Submodel> page) throws SerializationException, UnsupportedModifierException {
        return new JsonApiSerializer().write(page);
    }


    @Test
    public void testDeserializeStandardPage() throws SerializationException, ClientException, UnsupportedModifierException {
        Page<Submodel> requestPage = createPage();
        server.enqueue(new MockResponse().setBody(serializePage(requestPage)));

        Page<Submodel> responseSubmodelPage = concreteSubclass.get(new PagingInfo.Builder().limit(5).build());
        assertEquals(requestPage.getMetadata(), responseSubmodelPage.getMetadata());
    }


    @Test
    public void testDeserializeCustomPage() throws JSONException, SerializationException, ClientException, UnsupportedModifierException {
        Page<Submodel> requestPage = createPage();
        JSONObject customPage = new JSONObject(serializePage(requestPage));
        customPage.getJSONObject("paging_metadata").put("cursor2", "cursor");
        server.enqueue(new MockResponse().setBody(customPage.toString()));

        Page<Submodel> responseSubmodelPage = concreteSubclass.get(new PagingInfo.Builder().limit(5).build());
        assertEquals(requestPage.getMetadata(), responseSubmodelPage.getMetadata());
    }
}
