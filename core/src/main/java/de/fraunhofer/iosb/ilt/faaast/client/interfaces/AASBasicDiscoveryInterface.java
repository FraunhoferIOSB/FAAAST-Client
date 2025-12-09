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

import de.fraunhofer.iosb.ilt.faaast.client.exception.ConnectivityException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.InvalidPayloadException;
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpStatus;
import de.fraunhofer.iosb.ilt.faaast.client.query.AASBasicDiscoverySearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.client.util.QueryHelper;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.DeserializationException;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonApiDeserializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.AssetIdentification;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.SpecificAssetIdentification;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.json.JSONException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * Interface for discovering asset administration shells using aasIdentifiers.
 * This includes the capabilities and supported features of the server.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASBasicDiscoveryInterface extends BaseInterface {

    private static final String LOOKUP_PATH = "/lookup/shells";

    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient custom http-client in case the user wants to set specific attributes
     */
    public AASBasicDiscoveryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, LOOKUP_PATH), httpClient);
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public AASBasicDiscoveryInterface(URI endpoint) {
        super(resolve(endpoint, LOOKUP_PATH));
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public AASBasicDiscoveryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, LOOKUP_PATH), user, password);
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public AASBasicDiscoveryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, LOOKUP_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param authenticationHeaderProvider Supplier of authentication header value ('Authorization:
     *            {authenticationHeaderProvider.get()}')
     */
    public AASBasicDiscoveryInterface(URI endpoint, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, LOOKUP_PATH), authenticationHeaderProvider);
    }


    /**
     * Returns a list of Asset Administration Shell IDs linked to specific asset identifiers or the global asset ID.
     *
     * @param assetLinks A list of specific asset identifiers. Search for the global asset ID is supported by setting "name"
     *            to "globalAssetId" (see Constraint AASd-116).
     * @param pagingInfo Metadata that describes how many elements at which starting point should be retrieved.
     * @return Page of requested Asset Administration Shell IDs
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<String> lookupByAssetLink(List<SpecificAssetId> assetLinks, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        List<AssetIdentification> assetIdentificationList = new ArrayList<>();
        assetLinks.forEach(assetLink -> {
            assetIdentificationList.add(new SpecificAssetIdentification.Builder().value(assetLink.getValue()).key(assetLink.getName()).build());
        });

        AASBasicDiscoverySearchCriteria assetIds = new AASBasicDiscoverySearchCriteria.Builder().assetIds(assetIdentificationList).build();
        HttpRequest request = HttpHelper.createGetRequest(
                resolve(QueryHelper.apply(
                        null, Content.DEFAULT, QueryModifier.DEFAULT, pagingInfo, assetIds)),
                authenticationHeaderProvider.get());
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializePage(response.body(), String.class);
        }
        catch (DeserializationException | JSONException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Returns a list of specific asset identifiers based on an Asset Administration Shell ID to edit discoverable content.
     * The global asset ID is returned as specific asset ID with "name" equal to "globalAssetId" (see Constraint AASd-116).
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id
     * @return Requested specific Asset identifiers (including the global asset ID represented by a specific asset ID)
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<SpecificAssetId> lookupByAasId(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        return getAllList(idPath(aasIdentifier), SpecificAssetId.class);
    }


    /**
     * Creates or replaces all asset links associated to the Asset Administration Shell.
     *
     * @param assetLinks A set of specific asset identifiers
     * @param aasIdentifier The Asset Administration Shell’s unique id
     * @return List of asset links
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>409: ConflictException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<SpecificAssetId> createAssetLinks(List<SpecificAssetId> assetLinks, String aasIdentifier) throws StatusCodeException, ConnectivityException {
        HttpRequest request = HttpHelper.createPostRequest(
                resolve(QueryHelper.apply(idPath(aasIdentifier), Content.DEFAULT, QueryModifier.DEFAULT)),
                authenticationHeaderProvider.get(),
                serializeEntity(assetLinks));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.POST, response, HttpStatus.OK);
        try {
            return new JsonApiDeserializer().readList(response.body(), SpecificAssetId.class);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Deletes all specific asset identifiers linked to a specified Asset Administration Shell:
     * discovery via these specific asset IDs shall not be supported any longer.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void deleteAssetLinks(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        delete(idPath(aasIdentifier));
    }

}
