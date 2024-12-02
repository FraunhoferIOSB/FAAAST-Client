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
import de.fraunhofer.iosb.ilt.faaast.service.util.ReferenceHelper;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Allows to filter Concept Descriptions in a Concept Description Repository based on
 * idShort, isCaseOf and dataSpecification.
 */
public class ConceptDescriptionSearchCriteria extends de.fraunhofer.iosb.ilt.faaast.service.persistence.ConceptDescriptionSearchCriteria implements SearchCriteria {
    public static ConceptDescriptionSearchCriteria DEFAULT = new ConceptDescriptionSearchCriteria();

    /**
     * Serializes isCaseOf, idShort and dataSpecification as filters in a query string for the use in a http request.
     * 
     * @return The query string.
     */
    @Override
    public String toQueryString() {
        String isCaseOfString = getIsCaseOf() == null ? ""
                : "isCaseOf=" + EncodingHelper.base64Encode(ReferenceHelper.toString(getIsCaseOf()));
        String idShortString = getIdShort() == null ? "" : "idShort=" + getIdShort();
        String dataSpecificationRefString = getDataSpecification() == null ? ""
                : "dataSpecificationRef=" + EncodingHelper.base64Encode(ReferenceHelper.toString(getDataSpecification()));

        return Stream.of(isCaseOfString, idShortString, dataSpecificationRefString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }
}
