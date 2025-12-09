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
import de.fraunhofer.iosb.ilt.faaast.client.query.AASSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import java.util.function.Supplier;


/**
 * Interface for managing Asset Administration Shells. It further provides access to the data of these elements through
 * the AAS Interface. A repository can host multiple entities.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASRepositoryInterface extends BaseInterface {

    private static final String API_PATH = "/shells";

    private AASRepositoryInterface(URI endpoint, HttpClient httpClient, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, API_PATH), httpClient, authenticationHeaderProvider);
    }


    /**
     * Creates a new Asset Administration Shell Repository Interface using a custom HTTP client.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    public AASRepositoryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, API_PATH), httpClient);
    }


    /**
     * Creates a new Asset Administration Shell Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public AASRepositoryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Asset Administration Shell Repository Interface with basic authentication.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param user String for basic authentication
     * @param password String for basic authentication
     */
    public AASRepositoryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Asset Administration Shell Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public AASRepositoryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Creates a new Asset Administration Shell Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param authenticationHeaderProvider Supplier of authentication header value ('Authorization:
     *            {authenticationHeaderProvider.get()}')
     */
    public AASRepositoryInterface(URI endpoint, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, API_PATH), authenticationHeaderProvider);
    }


    /**
     * Retrieves all Asset Administration Shells.
     *
     * @return A list of all Asset Administration Shells
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
    public List<AssetAdministrationShell> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(AASSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves all Asset Administration Shells based on specific search criteria.
     *
     * @param aasSearchCriteria Search criteria to filter Asset Administration Shells based on AssetType and AssetKind
     * @return A list of Asset Administration Shells that match the search criteria
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
    public List<AssetAdministrationShell> getAll(AASSearchCriteria aasSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(null, aasSearchCriteria, Content.DEFAULT, QueryModifier.DEFAULT, AssetAdministrationShell.class);
    }


    /**
     * Retrieves a page of Asset Administration Shells.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Asset Administration Shells
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
    public Page<AssetAdministrationShell> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, AASSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves a page of Asset Administration Shells based on specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param aasSearchCriteria Search criteria to filter Asset Administration Shells based on AssetType and AssetKind
     * @return A page of Asset Administration Shells
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
    public Page<AssetAdministrationShell> get(PagingInfo pagingInfo, AASSearchCriteria aasSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getPage(Content.DEFAULT, QueryModifier.DEFAULT, pagingInfo, aasSearchCriteria, AssetAdministrationShell.class);
    }


    /**
     * Creates a new Asset Administration Shell.
     * The unique identifier of the Asset Administration Shell must be provided in the payload.
     *
     * @param aas Asset Administration Shell object to be created
     * @return The created Asset Administration Shell
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>409: ConflictException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public AssetAdministrationShell post(AssetAdministrationShell aas) throws StatusCodeException, ConnectivityException {
        return post(aas, AssetAdministrationShell.class);
    }


    /**
     * Retrieves references to all Asset Administration Shells.
     *
     * @return A list of references to all Asset Administration Shells
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
    public List<Reference> getAllAsReference() throws StatusCodeException, ConnectivityException {
        return getAllAsReference(AASSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves references to all Asset Administration Shells based on specific search criteria.
     *
     * @param aasSearchCriteria Search criteria to filter Asset Administration Shell references based on AssetType and
     *            AssetKind
     * @return A list of references to Asset Administration Shells
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
    public List<Reference> getAllAsReference(AASSearchCriteria aasSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(null, aasSearchCriteria, Content.REFERENCE, QueryModifier.DEFAULT, Reference.class);
    }


    /**
     * Retrieves a page of references to Asset Administration Shells.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of references to Asset Administration Shells
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
    public Page<Reference> getReference(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getReference(pagingInfo, AASSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves a page of references to Asset Administration Shells based on specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param aasSearchCriteria Search criteria to filter Asset Administration Shell references based on AssetType and
     *            AssetKind
     * @return A page of references to Asset Administration Shells
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
    public Page<Reference> getReference(PagingInfo pagingInfo, AASSearchCriteria aasSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getPage(null, Content.REFERENCE, QueryModifier.DEFAULT, pagingInfo, aasSearchCriteria, Reference.class);
    }


    /**
     * Deletes an Asset Administration Shell.
     *
     * @param aasIdentifier The unique identifier of the Asset Administration Shell to be deleted
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
    @Override
    public void delete(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        super.delete(idPath(aasIdentifier));
    }


    /**
     * Returns an AAS Interface for accessing the data of AAS elements.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id
     * @return Requested Asset Administration Shell Interface
     */
    public AASInterface getAASInterface(String aasIdentifier) {
        return new AASInterface(resolve(idPath(aasIdentifier)), httpClient);
    }

    public static class Builder extends BaseBuilder<AASRepositoryInterface, Builder> {

        private Builder() {}


        @Override
        public Builder newInstance() {
            return new Builder();
        }


        @Override
        public Builder getSelf() {
            return this;
        }


        @Override
        public Builder endpoint(URI endpoint) {
            this.endpoint = resolve(endpoint, API_PATH);
            return getSelf();
        }


        @Override
        public AASRepositoryInterface buildConcrete() {
            return new AASRepositoryInterface(endpoint, httpClient, authenticationHeaderProvider);
        }
    }
}
