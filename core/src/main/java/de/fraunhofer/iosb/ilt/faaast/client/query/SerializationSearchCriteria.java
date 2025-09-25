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
package de.fraunhofer.iosb.ilt.faaast.client.query;

import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Allows to create the query to construct the serialization of specific AAS and Submodels on the server.
 */
public class SerializationSearchCriteria implements SearchCriteria {

    public List<String> aasIds;
    public List<String> submodelIds;

    public SerializationSearchCriteria(List<String> aasIds, List<String> submodelIds) {
        this.aasIds = aasIds;
        this.submodelIds = submodelIds;
    }


    @Override
    public String toQueryString() {
        String aasIdsString;
        aasIdsString = aasIds == null || aasIds.isEmpty() ? ""
                : "aasIds=" + aasIds.stream()
                        .map(this::serializeIds)
                        .collect(Collectors.joining(","));

        String submodelIdsString;
        submodelIdsString = submodelIds == null || submodelIds.isEmpty() ? ""
                : "submodelIds=" + submodelIds.stream()
                        .map(this::serializeIds)
                        .collect(Collectors.joining(","));

        return Stream.of(aasIdsString, submodelIdsString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));

    }


    private String serializeIds(String id) {
        return EncodingHelper.base64Encode(id);
    }
}
