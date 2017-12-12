package org.bcos.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.bcos.web3j.abi.datatypes.Ufixed;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.bcos.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Ufixed128x72 extends Ufixed {
    public static final Ufixed128x72 DEFAULT = new Ufixed128x72(BigInteger.ZERO);

    public Ufixed128x72(BigInteger value) {
        super(128, 72, value);
    }

    public Ufixed128x72(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(128, 72, m, n);
    }
}
