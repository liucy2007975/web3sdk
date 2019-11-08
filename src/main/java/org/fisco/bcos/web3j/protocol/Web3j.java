package org.fisco.bcos.web3j.protocol;

import org.fisco.bcos.web3j.protocol.core.JsonRpc2_0Web3j;
import org.fisco.bcos.web3j.protocol.core.RPC;

/** JSON-RPC Request object building factory. */
public interface Web3j extends RPC {

    static Web3j build(Web3jService web3jService, int groupId) {
        return new JsonRpc2_0Web3j(web3jService, groupId);
    }
}
