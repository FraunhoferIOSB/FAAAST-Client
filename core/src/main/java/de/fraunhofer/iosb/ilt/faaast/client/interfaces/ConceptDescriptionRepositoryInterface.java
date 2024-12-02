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
import de.fraunhofer.iosb.ilt.faaast.client.query.ConceptDescriptionSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;


/**
 * Interface for managing Concept Descriptions. It further provides access to the data of these elements through
 * the AAS Interface. A repository can host multiple entities.
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class ConceptDescriptionRepositoryInterface extends BaseInterface {

    /**
     * Creates a new Concept Description Interface.
     *
     * @param serviceUri Uri used to communicate with the FA³ST service.
     */
    protected ConceptDescriptionRepositoryInterface(URI serviceUri) {
        super(serviceUri, "/concept-descriptions/");
    }


    /**
     * Creates a new Concept Description Interface
     *
     * @param user String to enable basic authentication
     * @param password String to enable basic authentication
     * @param serviceUri Uri used to communicate with the FA³ST service.
     */
    protected ConceptDescriptionRepositoryInterface(URI serviceUri, String user, String password) {
        super(serviceUri, "/concept-descriptions/", user, password);
    }


    /**
     * Creates a new Concept Description Interface.
     *
     * @param httpClient allows user to specify custom http-client.
     * @param serviceUri the serviceUri
     */
    protected ConceptDescriptionRepositoryInterface(URI serviceUri, HttpClient httpClient) {
        super(serviceUri, "/concept-descriptions/", httpClient);
    }


    /**
     * Retrieves all Concept Descriptions from the server.
     *
     * @return List of all Concept Descriptions.
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
    public List<ConceptDescription> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(ConceptDescriptionSearchCriteria.DEFAULT);
    }


    /**
     * Retrieves Concept Descriptions according to specific search criteria.
     *
     * @param conceptDescriptionSearchCriteria specific search criteria: idShort, isCaseOf or dataSpecificationRef.
     * @return List of Concept Descriptions matching search criteria.
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
    public List<ConceptDescription> getAll(ConceptDescriptionSearchCriteria conceptDescriptionSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getList(basePath(), conceptDescriptionSearchCriteria, Content.DEFAULT, QueryModifier.DEFAULT, ConceptDescription.class);
    }


    /**
     * Retrieves a page of Concept Descriptions.
     *
     * @param pagingInfo Metadata for controlling the pagination of results.
     * @return A page of Concept Descriptions.
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
    public Page<ConceptDescription> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return get(pagingInfo, ConceptDescriptionSearchCriteria.DEFAULT);
    }


    /**
     * Returns page of Concept Descriptions according to specific search criteria
     *
     * @param pagingInfo paging meta information
     * @param conceptDescriptionSearchCriteria specific search criteria
     * @return List of Concept Descriptions
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
    public Page<ConceptDescription> get(PagingInfo pagingInfo, ConceptDescriptionSearchCriteria conceptDescriptionSearchCriteria)
            throws StatusCodeException, ConnectivityException {
        return getPage(basePath(), Content.DEFAULT, QueryModifier.DEFAULT, pagingInfo, conceptDescriptionSearchCriteria, ConceptDescription.class);
    }


    /**
     * Creates a new Concept Description. The id of the new Concept Description must be set in the payload.
     *
     * @param conceptDescription Concept Description object
     * @return Requested Concept Descriptions
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
    public ConceptDescription post(ConceptDescription conceptDescription) throws StatusCodeException, ConnectivityException {
        return post(basePath(), conceptDescription, ConceptDescription.class);
    }


    /**
     * Returns a specific Concept Description
     *
     * @param cdIdentifier The Concept Description’s unique id
     * @return Requested Concept Description
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
    public ConceptDescription get(String cdIdentifier) throws StatusCodeException, ConnectivityException {
        return get(idPath(cdIdentifier), ConceptDescription.class);
    }


    /**
     * Replaces an existing Concept Description
     *
     * @param conceptDescription Concept Description object
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
    public void put(ConceptDescription conceptDescription, String cdIdentifier) throws StatusCodeException, ConnectivityException {
        put(idPath(cdIdentifier), conceptDescription);
    }


    /**
     * Deletes a Concept Description
     *
     * @param cdIdentifier The Concept Description’s unique id
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
    public void delete(String cdIdentifier) throws StatusCodeException, ConnectivityException {
        super.delete(idPath(cdIdentifier));
    }
}
