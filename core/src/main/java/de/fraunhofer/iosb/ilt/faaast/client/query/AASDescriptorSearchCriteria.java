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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.serialization.EnumSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;


/**
 * Allows to filter Asset Administration Shell descriptors in an Asset Administration Shell registry based on
 * asset kind and asset type.
 */
public class AASDescriptorSearchCriteria implements SearchCriteria {

    public static final AASDescriptorSearchCriteria DEFAULT = new AASDescriptorSearchCriteria.Builder().build();
    private final AssetKind assetKind;
    private final String assetType;

    private AASDescriptorSearchCriteria(Builder builder) {
        this.assetKind = builder.assetKind;
        this.assetType = builder.assetType;
    }


    /**
     * Getter method for retrieving the asset kind used to filter Asset Administration Shell descriptors.
     *
     * @return The asset kind object
     */
    public AssetKind getAssetKind() {
        return assetKind;
    }


    /**
     * Getter method for retrieving the asset type used to filter Asset Administration Shell descriptors.
     *
     * @return The asset type object
     */
    public String getAssetType() {
        return assetType;
    }


    /**
     * Serializes the asset kind and asset type as filters in a query string for the use in a http request.
     *
     * @return The query string
     */
    @Override
    public String toQueryString() {
        String assetKindString = assetKind == null ? ""
                : "assetKind=" + EnumSerializer.serializeEnumName(assetKind.name());

        String assetTypeString = assetType == null ? ""
                : "assetType=" + EncodingHelper.base64Encode(assetType);

        return Stream.of(assetKindString, assetTypeString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }

    public static class Builder {
        private AssetKind assetKind;
        private String assetType;

        public Builder assetKind(AssetKind assetKind) {
            this.assetKind = assetKind;
            return this;
        }


        public Builder assetType(String assetType) {
            this.assetType = assetType;
            return this;
        }


        public AASDescriptorSearchCriteria build() {
            return new AASDescriptorSearchCriteria(this);
        }
    }
}
