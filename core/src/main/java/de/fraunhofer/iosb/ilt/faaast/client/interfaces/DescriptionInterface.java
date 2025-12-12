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
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.client.http.HttpStatus;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpClientHelper;
import de.fraunhofer.iosb.ilt.faaast.client.util.HttpRequestHelper;
import de.fraunhofer.iosb.ilt.faaast.service.model.ServiceDescription;
import org.eclipse.jetty.server.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;


/**
 * Interface for communicating the description of a server.
 * This includes the capabilities and supported features of the server.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class DescriptionInterface extends BaseInterface {

    private static final String API_PATH = "/description";

    private DescriptionInterface(URI endpoint, HttpClient httpClient, Supplier<String> authenticationHeaderProvider) {
        super(resolve(endpoint, API_PATH), httpClient, authenticationHeaderProvider);
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient custom http-client in case the user wants to set specific attributes
     */
    public DescriptionInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public DescriptionInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public DescriptionInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public DescriptionInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpClientHelper.newTrustAllCertificatesClient() : HttpClientHelper.newDefaultClient());
    }


    /**
     * Retrieves the self-describing information of a network resource (ServiceDescription) as a List of Strings.
     *
     * @return Requested self-describing information
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
    public ServiceDescription get() throws StatusCodeException, ConnectivityException {
        HttpRequest request = HttpRequestHelper.createGetRequest(endpoint, authenticationHeaderProvider.get());
        HttpResponse<String> response = HttpRequestHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        return parseBody(response, ServiceDescription.class);
    }

    public static class Builder extends AbstractBuilder<DescriptionInterface, Builder> {

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
        public DescriptionInterface buildConcrete() {
            return new DescriptionInterface(endpoint, httpClient(), authenticationHeaderProvider);
        }
    }
}
