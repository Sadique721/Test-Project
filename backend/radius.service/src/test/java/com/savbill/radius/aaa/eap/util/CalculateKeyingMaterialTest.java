package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.eap.data.SecurityKeys;
import com.savbill.radius.aaa.util.RadiusUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.savbill.radius.aaa.eap.util.CalculateKeyingMaterial.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class CalculateKeyingMaterialTest {

    @Test
    public void validateCalculateKeyingMaterial() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String expectedKeyingMaterial = "2890572d4de0fc8915581188589b1ac686e4daadb510c16ff65992eb5bd8c052ccf74797ff772112beadfd1279d506cbc814b5855ca95d34cbf9f41ddf032a76d24f2504cd05d81a3e91d969caca5ad007b3beb27ccce38d646d305c46a97610ec1b7e32efae0bc9272c98725ce9447e7ec826a88416d57c5ef63ae5f3ae8157a54b5b7cce6932d4d3ccf2e3722bd271d728115b6b0e739ec6c217c77d23428d";

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] masterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        byte[] bytes = calculateKeyingMaterial(clientRandom, serverRandom, masterSecret);

        assertEquals(expectedKeyingMaterial,RadiusUtil.getHexString(bytes));
        assertEquals(160,bytes.length);
    }

    @Test
    public void validateSecurityKeyMaterialsAES256() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SecurityKeys securityKeys = new SecurityKeys();

        securityKeys.setClientWriteKey(RadiusUtil.getBytesFromHexString("d24f2504cd05d81a3e91d969caca5ad007b3beb27ccce38d646d305c46a97610"));
        securityKeys.setClientMACKey(RadiusUtil.getBytesFromHexString("2890572d4de0fc8915581188589b1ac686e4daadb510c16ff65992eb5bd8c052"));
        securityKeys.setServerWriteKey(RadiusUtil.getBytesFromHexString("ec1b7e32efae0bc9272c98725ce9447e7ec826a88416d57c5ef63ae5f3ae8157"));
        securityKeys.setServerMACKey(RadiusUtil.getBytesFromHexString("ccf74797ff772112beadfd1279d506cbc814b5855ca95d34cbf9f41ddf032a76"));
        securityKeys.setClientIv(RadiusUtil.getBytesFromHexString("a54b5b7cce6932d4d3ccf2e3722bd271"));
        securityKeys.setServerIv(RadiusUtil.getBytesFromHexString("d728115b6b0e739ec6c217c77d23428d"));

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] masterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        SecurityKeys bytes = securityKeyForAES256(clientRandom, serverRandom, masterSecret);

        assertEquals(RadiusUtil.getHexString(securityKeys.getClientWriteKey()),RadiusUtil.getHexString(bytes.getClientWriteKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getClientMACKey()),RadiusUtil.getHexString(bytes.getClientMACKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerWriteKey()),RadiusUtil.getHexString(bytes.getServerWriteKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerMACKey()),RadiusUtil.getHexString(bytes.getServerMACKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getClientIv()),RadiusUtil.getHexString(bytes.getClientIv()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerIv()),RadiusUtil.getHexString(bytes.getServerIv()));
    }

    @Test
    public void validateSecurityKeyMaterialsForAES128() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SecurityKeys securityKeys = new SecurityKeys();

        securityKeys.setClientWriteKey(RadiusUtil.getBytesFromHexString("beadfd1279d506cbc814b5855ca95d34"));
        securityKeys.setClientMACKey(RadiusUtil.getBytesFromHexString("2890572d4de0fc8915581188589b1ac686e4daad"));
        securityKeys.setServerWriteKey(RadiusUtil.getBytesFromHexString("cbf9f41ddf032a76d24f2504cd05d81a"));
        securityKeys.setServerMACKey(RadiusUtil.getBytesFromHexString("b510c16ff65992eb5bd8c052ccf74797ff772112"));
        securityKeys.setClientIv(RadiusUtil.getBytesFromHexString("3e91d969caca5ad007b3beb27ccce38d"));
        securityKeys.setServerIv(RadiusUtil.getBytesFromHexString("646d305c46a97610ec1b7e32efae0bc9"));

        byte[] clientRandom = RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34");
        byte[] serverRandom = RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401");
        byte[] masterSecret = RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055");

        SecurityKeys bytes = securityKeyForAES128(clientRandom, serverRandom, masterSecret);

        assertEquals(RadiusUtil.getHexString(securityKeys.getClientWriteKey()),RadiusUtil.getHexString(bytes.getClientWriteKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getClientMACKey()),RadiusUtil.getHexString(bytes.getClientMACKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerWriteKey()),RadiusUtil.getHexString(bytes.getServerWriteKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerMACKey()),RadiusUtil.getHexString(bytes.getServerMACKey()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getClientIv()),RadiusUtil.getHexString(bytes.getClientIv()));
        assertEquals(RadiusUtil.getHexString(securityKeys.getServerIv()),RadiusUtil.getHexString(bytes.getServerIv()));
    }
}
