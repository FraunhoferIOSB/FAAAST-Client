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
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingMetadata;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class AASBasicDiscoveryInterfaceIntegrationTest {

    private static AASBasicDiscoveryInterface discoveryInterface;

    @Before
    public void setup() throws IOException, URISyntaxException {
        discoveryInterface = new AASBasicDiscoveryInterface(new URI("http://localhost:80/api/v3.0"));
    }


    @Test
    public void testLookupByAssetLink() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<String> aasIdList = new ArrayList<>();
        aasIdList.add("aasId1");

        Page<String> expected = Page.<String> builder()
                .result(aasIdList)
                .metadata(new PagingMetadata.Builder().cursor(null).build())
                .build();

        List<SpecificAssetId> assetIdentificationList = new ArrayList<>();
        assetIdentificationList.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("https://example.com/ids/P/5645_2901_9088_1684").build());
        Page<String> actual = discoveryInterface.lookupByAssetLink(assetIdentificationList, PagingInfo.ALL);

        assertEquals(expected, actual);
    }


    @Test
    public void testLookupByAssetLinks() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<String> aasIdList = new ArrayList<>();
        aasIdList.add("aasId1");
        aasIdList.add("aasId2");

        Page<String> expected = Page.<String> builder()
                .result(aasIdList)
                .metadata(new PagingMetadata.Builder().cursor(null).build())
                .build();

        List<SpecificAssetId> assetIdentificationList = new ArrayList<>();
        assetIdentificationList.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("https://example.com/ids/P/5645_2901_9088_1684").build());
        assetIdentificationList.add(new DefaultSpecificAssetId.Builder().name("globalAssetId").value("https://example.com/ids/P/5645_2901_9088_1684123").build());
        Page<String> actual = discoveryInterface.lookupByAssetLink(assetIdentificationList, PagingInfo.ALL);

        assertEquals(expected, actual);
    }

}
