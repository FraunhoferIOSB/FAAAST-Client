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

import de.fraunhofer.iosb.ilt.faaast.client.exception.InvalidPayloadException;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.AssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.GlobalAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.SpecificAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.AssetAdministrationShellSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;


/**
 * Allows to filter Asset Administration Shells in an Asset Administration Shell repository based on
 * idShort and a List of AssetIdentification objects.
 */
public class AASSearchCriteria extends AssetAdministrationShellSearchCriteria implements SearchCriteria {
    public static final AASSearchCriteria DEFAULT = new AASSearchCriteria();

    public static class Builder extends AssetAdministrationShellSearchCriteria.AbstractBuilder<AASSearchCriteria, Builder> {
        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected AASSearchCriteria newBuildingInstance() {
            return new AASSearchCriteria();
        }
    }

    /**
     * Serializes the asset kind and asset type as filters in a query string for the use in a http request.
     * 
     * @return The query string.
     */
    @Override
    public String toQueryString() {
        String assetIdsString;
        assetIdsString = getAssetIds() == null || getAssetIds().isEmpty() ? ""
                : "assetIds=" + getAssetIds().stream()
                        .map(this::serializeAssetIdentification)
                        .collect(Collectors.joining(","));

        String idShortString = getIdShort() == null ? "" : "idShort=" + getIdShort();

        return Stream.of(assetIdsString, idShortString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));

    }


    private String serializeAssetIdentification(AssetIdentification assetId) {
        try {
            if (assetId instanceof SpecificAssetIdentification specificAssetIdentification) {
                return EncodingHelper.base64Encode(new JsonSerializer().write(new DefaultSpecificAssetId.Builder()
                        .value(assetId.getValue())
                        .name(specificAssetIdentification.getKey())
                        .build()));
            }
            else if (assetId instanceof GlobalAssetIdentification) {
                return EncodingHelper.base64Encode(new JsonSerializer().write(
                        new DefaultSpecificAssetId.Builder()
                                .value(assetId.getValue())
                                .name("globalAssetId")
                                .build()));
            }
        }
        catch (SerializationException e) {
            throw new InvalidPayloadException(e);
        }
        return "";
    }
}
