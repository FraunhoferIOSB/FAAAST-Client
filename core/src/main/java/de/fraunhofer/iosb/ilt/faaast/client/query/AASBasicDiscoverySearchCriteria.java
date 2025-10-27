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

import de.fraunhofer.iosb.ilt.faaast.service.model.asset.AssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.GlobalAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.SpecificAssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.AssetAdministrationShellSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;
import de.fraunhofer.iosb.ilt.faaast.service.util.FaaastConstants;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;

import java.util.ArrayList;
import java.util.List;


/**
 * Allows to filter Asset Administration Shells in an Asset Administration Shell Basic Discovery Interface
 * based on a List of AssetIdentification objects.
 */
public class AASBasicDiscoverySearchCriteria extends AssetAdministrationShellSearchCriteria implements SearchCriteria {

    public static final AASBasicDiscoverySearchCriteria DEFAULT = new AASBasicDiscoverySearchCriteria();

    @Override
    public String toQueryString() {
        String assetIdsString;
        assetIdsString = getAssetIds() == null || getAssetIds().isEmpty() ? ""
                : "assetIds=" + serializeAssetIdentifications(getAssetIds());
        return assetIdsString;
    }


    private String serializeAssetIdentifications(List<AssetIdentification> assetIds) {
        List<SpecificAssetId> aas4jAssetIds = new ArrayList<>();
        for(AssetIdentification assetId : assetIds) {
            if (assetId instanceof SpecificAssetIdentification specificAssetIdentification) {
                aas4jAssetIds.add(
                        new DefaultSpecificAssetId.Builder()
                                .value(assetId.getValue())
                                .name(specificAssetIdentification.getKey())
                                .build());
            } else if (assetId instanceof GlobalAssetIdentification) {
                aas4jAssetIds.add(
                        new DefaultSpecificAssetId.Builder()
                                .value(assetId.getValue())
                                .name(FaaastConstants.KEY_GLOBAL_ASSET_ID)
                                .build());
            }
        }
        try {
            return EncodingHelper.base64Encode(new JsonSerializer().write(aas4jAssetIds));
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
    }
}
