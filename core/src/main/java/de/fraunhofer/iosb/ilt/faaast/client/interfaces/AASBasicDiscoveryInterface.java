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
import de.fraunhofer.iosb.ilt.faaast.client.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.asset.AssetIdentification;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;


/**
 * Interface for discovering asset administration shells using aasIdentifiers.
 * This includes the capabilities and supported features of the server.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASBasicDiscoveryInterface extends BaseInterface {

    private static final String API_PATH = "/lookup";

    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient custom http-client in case the user wants to set specific attributes
     */
    public AASBasicDiscoveryInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public AASBasicDiscoveryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public AASBasicDiscoveryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Discovery Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public AASBasicDiscoveryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Returns a list of Asset Administration Shell IDs linked to specific asset identifiers or the global asset ID.
     *
     * @param assetLinks A list of specific asset identifiers. Search for the global asset ID is supported by setting "name"
     *            to "globalAssetId" (see Constraint AASd-116).
     * @return Page of requested Asset Administration Shell IDs
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
    public Page<String> getByAssetLink(List<AssetIdentification> assetLinks) throws StatusCodeException, ConnectivityException {
        return post(assetLinks, Page.class); // todo: implement this
    }


    /**
     * Returns a list of specific asset identifiers based on an Asset Administration Shell ID to edit discoverable content.
     * The global asset ID is returned as specific asset ID with "name" equal to "globalAssetId" (see Constraint AASd-116).
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)
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
    public List<AssetIdentification> getByAasId(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        return getAll(idPath(aasIdentifier), AssetIdentification.class);
    }


    /**
     * Creates or replaces all asset links associated to the Asset Administration Shell.
     *
     * @param assetLinks A set of specific asset identifiers
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)
     * @return List of asset links
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
    public List<AssetIdentification> createAssetLinks(List<AssetIdentification> assetLinks, String aasIdentifier) throws StatusCodeException, ConnectivityException {
        return post(assetLinks, List.class); // todo: implement this
    }


    /**
     * Deletes specified specific asset identifiers linked to an Asset Administration Shell:
     * discovery via these specific asset IDs shall not be supported any longer
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)
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
    public void deleteAssetLinks(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        delete(idPath(aasIdentifier));
    }

}
