package org.fisco.bcos.web3j.precompile.cns;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.fisco.bcos.web3j.precompile.common.PrecompiledCommon;
import org.fisco.bcos.web3j.protocol.ObjectMapperFactory;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameterName;
import org.fisco.bcos.web3j.protocol.core.methods.response.EthBlock;
import org.fisco.bcos.web3j.protocol.core.methods.response.EthSyncing;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.ClientTransactionManager;
import org.fisco.bcos.web3j.tx.TransactionManager;
import org.fisco.bcos.web3j.tx.gas.DefaultGasProvider;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Resolution logic for contract addresses.
 */
public class CnsService {
    static Logger logger = LoggerFactory.getLogger(CnsService.class);
    static final long DEFAULT_SYNC_THRESHOLD = 1000 * 60 * 3;
    static final String REVERSE_NAME_SUFFIX = ".addr.reverse";

    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private long syncThreshold;  // non-final in case this value needs to be tweaked
    private static String registryContract = "0x0000000000000000000000000000000000001004";

    private CnsTable cnsRegistry;

    public CnsService(Web3j web3j, long syncThreshold, Credentials credentials) {
        this.web3j = web3j;
        transactionManager = new ClientTransactionManager(web3j, credentials);  // don't use empty string
        this.syncThreshold = syncThreshold;
    }

    public CnsService(Web3j web3j,Credentials credentials) {
        this(web3j, DEFAULT_SYNC_THRESHOLD,credentials);
    }

    public void setSyncThreshold(long syncThreshold) {
        this.syncThreshold = syncThreshold;
    }

    public long getSyncThreshold() {
        return syncThreshold;
    }

    public String getAddressByContractNameAndVersion(String contractNameAndVersion) {

        if (!isValidCnsName(contractNameAndVersion)) {
             return contractNameAndVersion;
        }
            CnsTable cns;
            cns = lookupResolver();
            String contractAddressInfo;
            String address;

            try {
                // if has version
                if (contractNameAndVersion.contains(":")) {
                    String contractName = contractNameAndVersion.split(":")[0];
                    String contractVersion = contractNameAndVersion.split(":")[1];

                    contractAddressInfo = cns.selectByNameAndVersion(contractName, contractVersion).send();
                    logger.debug("get contractName ", contractAddressInfo);
                    List<CNSInfo> cNSInfos = jsonToCNSInfos(contractAddressInfo);
                    address = cNSInfos.get(0).getAddress();
                } else {
                    // only contract name
                    contractAddressInfo = cns.selectByName(contractNameAndVersion).send();
                    logger.debug("get contractName ", contractAddressInfo);
                    List<CNSInfo> CNSInfos = jsonToCNSInfos(contractAddressInfo);
                    CNSInfo c = CNSInfos.get(CNSInfos.size() - 1);
                    address = c.getAddress();
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to execute Ethereum request", e);
            }

        if (!WalletUtils.isValidAddress(address)) {
            throw new RuntimeException("Unable to resolve address for name: " + contractNameAndVersion);
        } else {
            return address;
        }
    }

    public String registerCns(String name, String version, String addr, String abi) throws Exception {
        CnsTable cns = lookupResolver();
        TransactionReceipt receipt = cns.insert(name, version, addr, abi).send();
        return PrecompiledCommon.getJsonStr(receipt.getOutput());
    }
    
    public List<CNSInfo> queryCnsByName(String name) throws Exception {
    	CnsTable cns = lookupResolver();
    	String cnsInfo = cns.selectByName(name).send();
    	ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
		return objectMapper.readValue(cnsInfo,
				objectMapper.getTypeFactory().constructCollectionType(List.class, CNSInfo.class));
    }
    
    public List<CNSInfo> queryCnsByNameAndVersion(String name, String version) throws Exception {
    	CnsTable cns = lookupResolver();
    	String cnsInfo = cns.selectByNameAndVersion(name, version).send();
    	ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    	return objectMapper.readValue(cnsInfo,
    			objectMapper.getTypeFactory().constructCollectionType(List.class, CNSInfo.class));
    }

    private List<CNSInfo> jsonToCNSInfos(String contractAddressInfo) throws IOException {

        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        List<CNSInfo> cnsInfo = objectMapper.readValue(contractAddressInfo, objectMapper.getTypeFactory().constructCollectionType(List.class, CNSInfo.class));
        return cnsInfo;
    }

    public CnsTable lookupResolver() {

        if (this.cnsRegistry == null) {
            CnsTable cnsRegistry = CnsTable.load(
                    registryContract, web3j, transactionManager,
                    new StaticGasProvider(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT));
            this.cnsRegistry = cnsRegistry;
        }
        return this.cnsRegistry;
    }

    boolean isSynced() throws Exception {
        EthSyncing ethSyncing = web3j.ethSyncing().send();
        if (ethSyncing.isSyncing()) {
            return false;
        } else {
            EthBlock ethBlock =
                    web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            long timestamp = ethBlock.getBlock().getTimestamp().longValueExact() * 1000;

            return System.currentTimeMillis() - syncThreshold < timestamp;
        }
    }

    public static boolean isValidCnsName(String input) {
        return input != null  // will be set to null on new Contract creation
                && (input.contains(":") || !WalletUtils.isValidAddress(input));
    }
}
