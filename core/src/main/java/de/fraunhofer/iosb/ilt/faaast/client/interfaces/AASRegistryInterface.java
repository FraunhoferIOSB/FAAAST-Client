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
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.descriptor.AssetAdministrationShellDescriptor;
import de.fraunhofer.iosb.ilt.faaast.service.model.descriptor.impl.DefaultAssetAdministrationShellDescriptor;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;


/**
 * Interface for interacting with an Asset Administration Shell (AAS) Registry via a standardized API.
 * This interface allows to register and unregister descriptors of administration shells.
 * The descriptors contain the information needed to access the AAS interface.
 * This required information includes the endpoint in the dedicated environment.
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASRegistryInterface extends BaseInterface {

    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param serviceUri Uri used to communicate with the FA³ST Service.
     */
    public AASRegistryInterface(URI serviceUri) {
        super(serviceUri, "/shell-descriptors/");
    }


    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param user String to allow for basic authentication.
     * @param password String to allow for basic authentication.
     * @param serviceUri Uri used to communicate with the FA³ST Service.
     */
    public AASRegistryInterface(URI serviceUri, String user, String password) {
        super(serviceUri, "/shell-descriptors/", user, password);
    }


    /**
     * Creates a new Asset Administration Shell Registry Interface.
     *
     * @param httpClient Allows the user to specify a custom httpClient.
     * @param serviceUri Uri used to communicate with the FA³ST Service.
     */
    public AASRegistryInterface(URI serviceUri, HttpClient httpClient) {
        super(serviceUri, "/shell-descriptors/", httpClient);
    }


    /**
     * Returns all Asset Administration Shell Descriptors in a List.
     *
     * @return List containing all Asset Administration Shell Descriptors.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public List<DefaultAssetAdministrationShellDescriptor> getAll() throws StatusCodeException, ConnectivityException {
        return getList(basePath(), DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Returns a page of Asset Administration Shell Descriptors.
     *
     * @param pagingInfo Metadata for controlling the pagination of results.
     * @return A page of Asset Administration Shell Descriptors.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public Page<DefaultAssetAdministrationShellDescriptor> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, AASDescriptorSearchCriteria.DEFAULT);
    }


    /**
     * Returns a Page of Asset Administration Shell Descriptors.
     *
     * @param pagingInfo Metadata for controlling the pagination of results.
     * @param aasDescriptorSearchCriteria Allows to filter Descriptors based on AssetType and AssetKind.
     * @return A page of Asset Administration Shell Descriptors.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public Page<DefaultAssetAdministrationShellDescriptor> get(PagingInfo pagingInfo, AASDescriptorSearchCriteria aasDescriptorSearchCriteria)
            throws StatusCodeException, ConnectivityException {
        return getPage(basePath(), Content.DEFAULT, QueryModifier.DEFAULT, pagingInfo, aasDescriptorSearchCriteria, DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS.
     *
     * @param shellDescriptor Object containing the Asset Administration Shell’s identification and endpoint information.
     * @return Created Asset Administration Shell Descriptor.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public DefaultAssetAdministrationShellDescriptor post(DefaultAssetAdministrationShellDescriptor shellDescriptor) throws StatusCodeException, ConnectivityException {
        return post(idPath(shellDescriptor.getId()), shellDescriptor, DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Returns a specific Asset Administration Shell Descriptor.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id.
     * @return Requested Asset Administration Shell Descriptor.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public DefaultAssetAdministrationShellDescriptor get(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        return get(idPath(aasIdentifier), DefaultAssetAdministrationShellDescriptor.class);
    }


    /**
     * Replaces an existing Asset Administration Shell Descriptor, i.e. replaces registration information.
     *
     * @param shellDescriptor Object containing the Asset Administration Shell’s identification and endpoint information.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public void put(String aasIdentifier, AssetAdministrationShellDescriptor shellDescriptor) throws StatusCodeException, ConnectivityException {
        super.put(idPath(aasIdentifier), shellDescriptor);
    }


    /**
     * Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS.
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id.
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
     * @throws ConnectivityException if the connection to the server cannot be established.
     */
    public void delete(String aasIdentifier) throws StatusCodeException, ConnectivityException {
        super.delete(idPath(aasIdentifier));
    }


    /**
     * Returns the Submodel Registry Interface.
     *
     * @param aasIdentifier The unique id of the Submodel for the reference to be deleted
     */
    public SubmodelRegistryInterface getSubmodelRegistryInterface(String aasIdentifier) {
        return new SubmodelRegistryInterface(URI.create(idPath(aasIdentifier)));
    }
}
