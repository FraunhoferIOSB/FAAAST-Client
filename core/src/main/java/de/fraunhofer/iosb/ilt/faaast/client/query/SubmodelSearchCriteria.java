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

import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Allows to filter Submodels in a Submodel repository based on
 * semanticId and idShort.
 */
public class SubmodelSearchCriteria extends de.fraunhofer.iosb.ilt.faaast.service.persistence.SubmodelSearchCriteria implements SearchCriteria {
    public static SubmodelSearchCriteria DEFAULT = new SubmodelSearchCriteria();

    /**
     * Serializes the semanticId and idShort as filters in a query string for the use in a http request.
     * 
     * @return The query string.
     */
    @Override
    public String toQueryString() {
        String semanticIdString = getSemanticId() == null ? ""
                : "semanticId=" + getSemanticId().getKeys().stream().map(Object::toString).collect(Collectors.joining(","));
        String idShortString = getIdShort() == null ? ""
                : "idShort=" + getIdShort();

        return Stream.of(semanticIdString, idShortString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }
}
