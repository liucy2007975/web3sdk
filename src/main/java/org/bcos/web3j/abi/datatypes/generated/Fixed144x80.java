package org.bcos.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.bcos.web3j.abi.datatypes.Fixed;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.bcos.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Fixed144x80 extends Fixed {
    public static final Fixed144x80 DEFAULT = new Fixed144x80(BigInteger.ZERO);

    public Fixed144x80(BigInteger value) {
        super(144, 80, value);
    }

    public Fixed144x80(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(144, 80, m, n);
    }
}
