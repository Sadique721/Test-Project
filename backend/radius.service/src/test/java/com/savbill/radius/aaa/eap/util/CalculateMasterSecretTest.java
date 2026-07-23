package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.util.RadiusUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.savbill.radius.aaa.eap.util.CalculateMasterSecret.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class CalculateMasterSecretTest {

    @Test
    public void validateCalculationForMSKForTLS128() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String expectedMSKForTTLS128 = "d26952ec3907e79a6f0ab4288849223e3125bbcc8258b7aa490b9f6cb9cc9124c1997f9f6210da70a6c2f63bc94b66d27b13696afb9cc6a38c9bcb72d65704bffa8cb481dc460ffeb5931d23f94f8254ed95cb4f0dcbbd00e2e92970f7838617d958346c26c8c4f5def8517eac995f405c0e969d3fb66a0b2fedd71d44020bd4";

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] masterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        byte[] bytes = calculateMSKForTLS128(clientRandom, serverRandom, masterSecret);

        assertEquals(expectedMSKForTTLS128,RadiusUtil.getHexString(bytes));
        assertEquals(128,bytes.length);
    }

    @Test
    public void validateCalculationForMSKForTTLS128() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String expectedMSKForTTLS128 = "4e15ce4f73f6920bfe1c4aedfcb5dcb88a4f2e215503138ad8d80abd7343f80037ddef9553ad3c6cc0c02578476bda84ecea8fe2b36c9f0fcb8378e109b0ad87585c9a4b11e38bb5c7101a14b84154e3e0056befa0e9f69010a8bb03c15fced2be066c914e7224fa75596684280b7e89475261f8097ceed39b7c9b3303df9da1";

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] masterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        byte[] bytes = calculateMSKForTTLS128(clientRandom, serverRandom, masterSecret);

        assertEquals(expectedMSKForTTLS128,RadiusUtil.getHexString(bytes));
        assertEquals(128,bytes.length);
    }


    @Test
    public void validateExtendedMasterSecretCalculation() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String expectedMasterSecret = "bb69a1291a96c349da8c9511b3ae11b9f855fc9a9d172d4d39cca5f45f069799d07617c1fd41184433a4183185f9bb2f";

        byte[] messageHash = RadiusUtil.getBytesFromHexString("76a742a104daa98258d297a4c76dac2e3d40221c7a9d489d09cfe90665a88d5c");
        byte[] preMasterSecret = RadiusUtil.getBytesFromHexString("0303788a0d6e1bf33310583134517891c24043f70ac8a862c7e77f23316982926e9b3e1b735ace9a313757ef9d9415a9");

        byte[] bytes = calculateExtendedMasterSecret(messageHash, preMasterSecret);

        assertEquals(expectedMasterSecret,RadiusUtil.getHexString(bytes));
        assertEquals(48, bytes.length);
    }

    @Test
    public void validateCalculationOfMasterSecret() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String expectedMSKForTTLS128 = "9c10a108d79cb355510485c00ee32a789117c6ab7345fc61ef08d51bf56539db49b96661a2d26d33a8cc22d9f98c8bdf";

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] preMasterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        byte[] bytes = calculateMasterSecret(clientRandom, serverRandom, preMasterSecret);

        assertEquals(expectedMSKForTTLS128,RadiusUtil.getHexString(bytes));
        assertEquals(48,bytes.length);
    }

}
