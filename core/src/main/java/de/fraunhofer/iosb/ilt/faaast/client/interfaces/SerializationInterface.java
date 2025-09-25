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
import de.fraunhofer.iosb.ilt.faaast.client.query.SerializationSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.InMemoryFile;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;


/**
 * Provides functionality for requesting aas and submodels from the server in serialized form.
 */
public class SerializationInterface extends BaseInterface {
    private static final String API_PATH = "/serialization";

    /**
     * Creates a new Serialization Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient custom http-client in case the user wants to set specific attributes
     */
    public SerializationInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Creates a new Serialization Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public SerializationInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Serialization Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public SerializationInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Serialization Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public SerializationInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Returns an appropriate serialization based on the AASX format.
     *
     * @return Requested serialization based on SerializationFormat
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
    public InMemoryFile getAASXPackage(List<String> aasIds, List<String> submodelIds) throws StatusCodeException, ConnectivityException {
        return get(null, new SerializationSearchCriteria(aasIds, submodelIds), InMemoryFile.class);
    }


    /**
     * Returns an environment serialization based on Json or xml format.
     *
     * @return Requested serialization based on SerializationFormat
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
    public Environment getEnvironment(List<String> aasIds, List<String> submodelIds) throws StatusCodeException, ConnectivityException {
        return get(null, new SerializationSearchCriteria(aasIds, submodelIds), Environment.class);
    }
}
