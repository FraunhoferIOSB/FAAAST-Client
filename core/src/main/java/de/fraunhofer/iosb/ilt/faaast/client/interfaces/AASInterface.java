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
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpStatus;
import de.fraunhofer.iosb.ilt.faaast.service.model.TypedInMemoryFile;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.*;


/**
 * Interface for accessing the elements of an Asset Administration Shell (AAS) via a standardized API.
 * This interface allows operations such as retrieving, updating, and deleting various aspects of the AAS,
 * including its submodels, asset information, and thumbnails.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class AASInterface extends BaseInterface {

    /**
     * Creates a new Asset Administration Shell Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public AASInterface(URI endpoint) {
        super(endpoint);
    }


    /**
     * Creates a new Asset Administration Shell Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public AASInterface(URI endpoint, String user, String password) {
        super(endpoint, user, password);
    }


    /**
     * Creates a new Asset Administration Shell Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    public AASInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Retrieves the Asset Administration Shell (AAS) from the server.
     *
     * @return The requested Asset Administration Shell object
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
    public AssetAdministrationShell get() throws StatusCodeException, ConnectivityException {
        return get(AssetAdministrationShell.class);
    }


    /**
     * Replaces the current Asset Administration Shell with a new one.
     *
     * @param aas The new Asset Administration Shell object to replace the current one
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
    public void put(AssetAdministrationShell aas) throws StatusCodeException, ConnectivityException {
        super.put(aas);
    }


    /**
     * Retrieves the Asset Administration Shell (AAS) as a reference.
     *
     * @return The requested Asset Administration Shell reference
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
    public Reference getAsReference() throws StatusCodeException, ConnectivityException {
        return get(Content.REFERENCE, Reference.class);
    }


    /**
     * Retrieves the asset information associated with the Asset Administration Shell.
     *
     * @return The requested Asset Information object
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
    public AssetInformation getAssetInformation() throws StatusCodeException, ConnectivityException {
        return get(assetInfoPath(), AssetInformation.class);
    }


    /**
     * Updates the asset information of the Asset Administration Shell.
     *
     * @param assetInfo The new Asset Information object to replace the current one
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
    public void putAssetInformation(AssetInformation assetInfo) throws StatusCodeException, ConnectivityException {
        put(assetInfoPath(), assetInfo);
    }


    /**
     * Retrieves the thumbnail image associated with the Asset Administration Shell.
     *
     * @return The requested thumbnail as a TypedInMemoryFile object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public TypedInMemoryFile getThumbnail() throws StatusCodeException, ConnectivityException {
        return getFile(thumbnailPath());
    }


    /**
     * Replaces the current thumbnail image of the Asset Administration Shell.
     *
     * @param file The new thumbnail file to replace the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void putThumbnail(TypedInMemoryFile file) throws StatusCodeException, ConnectivityException {
        putFile(thumbnailPath(), file);
    }


    /**
     * Deletes the current thumbnail image of the Asset Administration Shell.
     *
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void deleteThumbnail() throws StatusCodeException, ConnectivityException {
        delete(thumbnailPath(), HttpStatus.OK);
    }


    /**
     * Retrieves all references to submodels within the Asset Administration Shell.
     *
     * @return A list of references to all submodels
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
    public List<Reference> getAllSubmodelReferences() throws StatusCodeException, ConnectivityException {
        return getAll(submodelRefPath(), Reference.class);
    }


    /**
     * Retrieves a page of references to submodels.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of references to submodels
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
    public Page<Reference> getSubmodelReference(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(submodelRefPath(), pagingInfo, Reference.class);
    }


    /**
     * Creates a new reference to a submodel within the Asset Administration Shell.
     *
     * @param reference The reference to the submodel to be added
     * @return The created submodel reference
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
    public Reference postSubmodelReference(Reference reference) throws StatusCodeException, ConnectivityException {
        return post(submodelRefPath(), reference, Reference.class);
    }


    /**
     * Deletes a specific submodel reference from the Asset Administration Shell.
     *
     * @param submodelId The unique identifier of the submodel to delete
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
    public void deleteSubmodelReference(String submodelId) throws StatusCodeException, ConnectivityException {
        delete(submodelRefPath() + idPath(submodelId));
    }


    /**
     * Deletes a specific submodel from the Asset Administration Shell.
     *
     * @param submodelId The unique identifier of the submodel to delete
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
    public void deleteSubmodel(String submodelId) throws StatusCodeException, ConnectivityException {
        delete(submodelPath() + idPath(submodelId));
    }


    /**
     * Returns the Submodel Interface for managing the submodel within the AAS.
     * Although submodels can be managed directly through this interface,
     * it is recommended to use the Submodel Repository Interface.
     *
     * @param submodelId The unique identifier of the submodel to retrieve
     * @return The SubmodelInterface object for interacting with the specified submodel
     */
    public SubmodelInterface getSubmodelInterface(String submodelId) {
        return new SubmodelInterface(resolve(idPath(submodelId)));
    }


    private static String assetInfoPath() {
        return "/asset-information";
    }


    private static String submodelRefPath() {
        return "/submodel-refs";
    }


    private static String submodelPath() {
        return "/submodels";
    }


    private static String thumbnailPath() {
        return assetInfoPath() + "/thumbnail";
    }

}
