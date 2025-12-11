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
import de.fraunhofer.iosb.ilt.faaast.client.query.SubmodelSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;


/**
 * Interface for managing Submodels. It further provides access to the data of these elements through
 * the Submodel Interface. A repository can host multiple entities.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class SubmodelRepositoryInterface extends BaseInterface {

    private static final String API_PATH = "/submodels";

    private SubmodelRepositoryInterface(URI endpoint, HttpClient httpClient, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, API_PATH), httpClient, authenticationHeaderProvider);
    }


    /**
     * Creates a new Submodel Repository API.
     *
     * @param endpoint uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    public SubmodelRepositoryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, API_PATH), httpClient);
    }


    /**
     * Creates a new Submodel Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public SubmodelRepositoryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Submodel Repository Interface with basic authentication.
     *
     * @param endpoint uri used to communicate with the FA³ST service
     * @param user String for basic authentication
     * @param password String for basic authentication
     */
    public SubmodelRepositoryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Submodel Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public SubmodelRepositoryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Creates a new Submodel Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param authenticationHeaderProvider Supplier of authentication header value ('Authorization:
     *            {authenticationHeaderProvider.get()}')
     */
    public SubmodelRepositoryInterface(URI endpoint, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, API_PATH), authenticationHeaderProvider);
    }


    /**
     * Retrieves all Submodels.
     *
     * @return A List of all Submodels
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
    public List<Submodel> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(QueryModifier.DEFAULT, SubmodelSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves all Submodels according to output modifier.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return List of all Submodels
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
    public List<Submodel> getAll(QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return getAll(modifier, SubmodelSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves all Submodels that match specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return List of all submodels matching the search criteria
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
    public List<Submodel> getAll(SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(QueryModifier.DEFAULT, submodelSearchCriteria);
    }


    /**
     * Retrieves all Submodels that match specific search criteria according to query modifiers.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return List of Submodels
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
    public List<Submodel> getAll(QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(submodelSearchCriteria, Content.DEFAULT, modifier, Submodel.class);
    }


    /**
     * Retrieves a page of Submodels.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of submodels
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
    public Page<Submodel> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, QueryModifier.DEFAULT, SubmodelSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves a page of Submodels that match specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of Submodels
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
    public Page<Submodel> get(PagingInfo pagingInfo, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, QueryModifier.DEFAULT, submodelSearchCriteria);
    }


    /**
     * Retrieves a page of Submodels according to query modifier.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return A page of Submodels
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
    public Page<Submodel> get(PagingInfo pagingInfo, QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, modifier, SubmodelSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves a page of Submodels matching specific search criteria according to query modifier.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of Submodels
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
    public Page<Submodel> get(PagingInfo pagingInfo, QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getPage(Content.DEFAULT, modifier, pagingInfo, submodelSearchCriteria, Submodel.class);
    }


    /**
     * Retrieves all Submodel metadata matching specific search criteria.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A List containing all submodels serialised as metadata
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
    public List<Submodel> getAllMetadata(QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(submodelSearchCriteria, Content.METADATA, modifier, Submodel.class);
    }


    /**
     * Retrieves a page of Submodel metadata matching specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of Submodel metadata
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
    public Page<Submodel> getMetadata(PagingInfo pagingInfo, QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria)
            throws StatusCodeException, ConnectivityException {
        return getPage(Content.METADATA, modifier, pagingInfo, submodelSearchCriteria, Submodel.class);
    }


    /**
     * Retrieves a List containing all Submodels matching specific search criteria in value only serialisation.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A list of Submodels
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
    public List<Submodel> getAllValues(QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(submodelSearchCriteria, Content.VALUE, modifier, Submodel.class);
    }


    /**
     * Retrieves a page containing Submodels matching specific search criteria in value only serialisation.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of Submodels in value only serialisation
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
    public Page<Submodel> getValue(PagingInfo pagingInfo, QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getPage(Content.VALUE, modifier, pagingInfo, submodelSearchCriteria, Submodel.class);
    }


    /**
     * Retrieves a list of references to Submodels matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A List of References
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
    public List<Reference> getAllReferences(SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(submodelSearchCriteria, Content.REFERENCE, QueryModifier.MINIMAL, Reference.class);
    }


    /**
     * Retrieves a page of references to Submodels matching specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of References
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
    public Page<Reference> getReference(PagingInfo pagingInfo, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getPage(Content.REFERENCE, QueryModifier.MINIMAL, pagingInfo, submodelSearchCriteria, Reference.class);
    }


    /**
     * Retrieves a list of paths to Submodels matching specific search criteria.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A list of paths
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
    public List<Reference> getAllPaths(QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(submodelSearchCriteria, Content.PATH, modifier, Reference.class);
    }


    /**
     * Retrieves a page of paths to Submodels matching specific search criteria.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A page of paths
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
    public Page<Reference> getSubmodelsPath(PagingInfo pagingInfo, QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria)
            throws StatusCodeException, ConnectivityException {
        return getPage(Content.PATH, modifier, pagingInfo, submodelSearchCriteria, Reference.class);
    }


    /**
     * Creates a new Submodel. The unique if of the new submodel must be set in the payload.
     *
     * @param submodel Submodel object
     * @return The created Submodel
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
    public Submodel post(Submodel submodel) throws StatusCodeException, ConnectivityException {
        return post(submodel, Submodel.class);
    }


    /**
     * Deletes a Submodel.
     *
     * @param submodelIdentifier The unique identifier of the Submodel to be deleted
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
    public void delete(String submodelIdentifier) throws StatusCodeException, ConnectivityException {
        super.delete(idPath(submodelIdentifier));
    }


    /**
     * Returns a Submodel Interface for use of Interface Methods.
     *
     * @param submodelId The Submodels’ unique id
     * @return The requested Submodel Interface
     */
    public SubmodelInterface getSubmodelInterface(String submodelId) {
        return new SubmodelInterface(resolve(idPath(submodelId)), httpClient);
    }

    public static class Builder extends BaseBuilder<SubmodelRepositoryInterface, Builder> {

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
            this.endpoint = endpoint;
            return getSelf();
        }


        @Override
        public SubmodelRepositoryInterface buildConcrete() {
            return new SubmodelRepositoryInterface(endpoint, httpClient(), authenticationHeaderProvider);
        }
    }
}
