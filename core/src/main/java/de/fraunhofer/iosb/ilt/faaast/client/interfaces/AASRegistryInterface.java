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
import de.fraunhofer.iosb.ilt.faaast.client.query.AASDescriptorSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;


/**
 * Interface for interacting with an Asset Administration Shell (AAS) Registry via a standardized API.
 * This interface allows to register and unregister descriptors of administration shells.
 * The descriptors contain the information needed to access the AAS interface.
 * This required information includes the endpoint in the dedicated environment.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASRegistryInterface extends BaseInterface {

    private static final String API_PATH = "/shell-descriptors";

    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param httpClient Allows the user to specify a custom httpClient
     */
    public AASRegistryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, API_PATH), httpClient);
    }


    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     */
    public AASRegistryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public AASRegistryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Asset Administration Shell Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public AASRegistryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Returns all Asset Administration Shell Descriptors in a List.
     *
     * @return List containing all Asset Administration Shell Descriptors
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
    public List<DefaultAssetAdministrationShellDescriptor> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Returns a page of Asset Administration Shell Descriptors.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Asset Administration Shell Descriptors
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
    public Page<DefaultAssetAdministrationShellDescriptor> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, AASDescriptorSearchCriteria.DEFAULT);
    }


    /**
     * Returns a Page of Asset Administration Shell Descriptors.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param aasDescriptorSearchCriteria Allows to filter Descriptors based on AssetType and AssetKind
     * @return A page of Asset Administration Shell Descriptors
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
    public Page<DefaultAssetAdministrationShellDescriptor> get(PagingInfo pagingInfo, AASDescriptorSearchCriteria aasDescriptorSearchCriteria)
            throws StatusCodeException, ConnectivityException {
        return getPage(Content.DEFAULT, QueryModifier.DEFAULT, pagingInfo, aasDescriptorSearchCriteria, DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS.
     *
     * @param shellDescriptor Object containing the Asset Administration Shell’s identification and endpoint information
     * @return Created Asset Administration Shell Descriptor
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
    public DefaultAssetAdministrationShellDescriptor post(DefaultAssetAdministrationShellDescriptor shellDescriptor) throws StatusCodeException, ConnectivityException {
        return post(shellDescriptor, DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Returns a specific Asset Administration Shell Descriptor.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id
     * @return Requested Asset Administration Shell Descriptor
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
    public DefaultAssetAdministrationShellDescriptor get(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        return get(idPath(aasIdentifier), DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Replaces an existing Asset Administration Shell Descriptor, i.e. replaces registration information.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id
     * @param shellDescriptor Object containing the Asset Administration Shell’s identification and endpoint information
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
    public void put(String aasIdentifier, AssetAdministrationShellDescriptor shellDescriptor) throws StatusCodeException, ConnectivityException {
        super.put(idPath(aasIdentifier), shellDescriptor);
    }


    /**
     * Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id
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
     * Returns the Submodel Registry Interface.
     *
     * @param aasIdentifier The unique id of the Submodel for the reference to be deleted
     * @return the {@link SubmodelRegistryInterface}
     */
    public SubmodelRegistryInterface getSubmodelRegistryInterface(String aasIdentifier) {
        return new SubmodelRegistryInterface(resolve(idPath(aasIdentifier)));
    }
}
