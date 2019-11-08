package org.fisco.bcos.web3j.protocol;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.web3j.protocol.core.Request;
import org.fisco.bcos.web3j.protocol.core.Response;

/** Services API. */
public interface Web3jService {
    <T extends Response> T send(Request request, Class<T> responseType) throws IOException;

    void sendOnly(Request request) throws IOException;

    <T extends Response> CompletableFuture<T> sendAsync(Request request, Class<T> responseType);

    /**
     * Closes resources used by the service.
     *
     * @throws IOException thrown if a service failed to close all resources
     */
    void close() throws IOException;
}
