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

import de.fraunhofer.iosb.ilt.faaast.service.persistence.AssetAdministrationShellSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Allows to filter Asset Administration Shells in an Asset Administration Shell Basic Discovery Interface
 * based on a List of AssetIdentification objects.
 */
public class AASBasicDiscoverySearchCriteria extends AssetAdministrationShellSearchCriteria implements SearchCriteria {

    private boolean fallback = false;
    private List<SpecificAssetId> specificAssetIds;

    public static final AASBasicDiscoverySearchCriteria DEFAULT = new AASBasicDiscoverySearchCriteria();

    @Override
    public String toQueryString() {
        String assetIdsString;
        if (!fallback) {
            assetIdsString = specificAssetIds == null || specificAssetIds.isEmpty() ? ""
                    : "assetIds=" + serializeAssetIdentifications(specificAssetIds);
            return assetIdsString;
        }
        else {
            assetIdsString = specificAssetIds == null || specificAssetIds.isEmpty() ? ""
                    : "assetIds=" + serializeAssetIdentificationsFallback(specificAssetIds);
            return assetIdsString;
        }
    }


    private AASBasicDiscoverySearchCriteria() {}


    protected void setFallback(boolean fallback) {
        this.fallback = fallback;
    }


    protected void setSpecificAssetIds(List<SpecificAssetId> specificAssetIds) {
        this.specificAssetIds = specificAssetIds;
    }


    private String serializeAssetIdentifications(List<SpecificAssetId> assetIds) {
        try {
            return EncodingHelper.base64UrlEncode(new JsonSerializer().write(assetIds));
        }
        catch (SerializationException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private String serializeAssetIdentificationsFallback(List<SpecificAssetId> assetIds) {
        return assetIds.stream()
                .map(this::serializeAssetId)
                .collect(Collectors.joining(","));
    }


    private String serializeAssetId(SpecificAssetId specificAssetId) {
        try {
            return EncodingHelper.base64UrlEncode(new JsonSerializer().write(specificAssetId));
        }
        catch (SerializationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class Builder extends AbstractBuilder<AASBasicDiscoverySearchCriteria, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected AASBasicDiscoverySearchCriteria newBuildingInstance() {
            return new AASBasicDiscoverySearchCriteria();
        }


        public Builder fallback(boolean fallback) {
            getBuildingInstance().setFallback(fallback);
            return this;
        }


        public Builder specificAssetIds(List<SpecificAssetId> specificAssetIds) {
            getBuildingInstance().setSpecificAssetIds(specificAssetIds);
            return this;
        }
    }
}
