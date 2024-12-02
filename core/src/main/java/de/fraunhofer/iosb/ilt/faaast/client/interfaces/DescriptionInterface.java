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

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;


/**
 * Interface for communicating the description of a server.
 * This includes the capabilities and supported features of the server.
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class DescriptionInterface extends BaseInterface {
    /**
     * Creates a new Description Interface.
     *
     * @param serviceUri Uri used to communicate with the FA³ST service.
     */
    public DescriptionInterface(URI serviceUri) {
        super(serviceUri, "/description");
    }


    /**
     * Creates a new Description Interface.
     *
     * @param serviceUri Uri used to communicate with the FA³ST service.
     * @param httpClient custom http-client in case the user wants to set specific attributes.
     */
    public DescriptionInterface(URI serviceUri, HttpClient httpClient) {
        super(serviceUri, "", httpClient);
    }


    /**
     * Retrieves the self-describing information of a network resource (ServiceDescription) as a List of Strings.
     *
     * @return Requested self-describing information.
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
    public List<String> get() throws StatusCodeException, ConnectivityException {
        return getList(basePath(), String.class);
    }

}
