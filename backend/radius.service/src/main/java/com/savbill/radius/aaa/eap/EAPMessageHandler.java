package com.savbill.radius.aaa.eap;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.constant.RadiusAttributes;
import com.savbill.radius.aaa.eap.constant.EapType;
import com.savbill.radius.aaa.eap.data.*;
import com.savbill.radius.aaa.eap.data.*;
import com.savbill.radius.aaa.eap.generater.ServerKeyExchangeGenerator;
import com.savbill.radius.aaa.eap.util.*;
import com.savbill.radius.aaa.eap.util.*;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusUtil;
import com.savbill.radius.entity.Client;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.savbill.radius.aaa.eap.util.CalculateKeyingMaterial.*;
import static com.savbill.radius.aaa.eap.util.CalculateMasterSecret.removeZeroBytes;
import static com.savbill.radius.aaa.server.RadiusUtility.REPLY_MESSAGE;

@Service
public class EAPMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(EAPMessageHandler.class);
    private Map<String, EAPSession> eapSessions= new ConcurrentHashMap<>(10);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public RadiusPacket handleEapMessage(AccessRequest request, Client cltData, KeyStoreImpl keyStore, String authenticationType) throws Exception {
        EAPSession eapSession = null;
        EAPPacket eapRequestPacket;
        List<RadiusAttribute> eapAttributes = request.getAttributes(AccessRequest.EAP);
        int attributesLength = eapAttributes.size();

        RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_CHALLENGE, request.getPacketIdentifier());

        EAPAttribute attributeFromRes = new EAPAttribute(AccessRequest.EAP);
        if (attributesLength == 1 ) {
            EAPAttribute attribute = (EAPAttribute) request.getAttribute(79);

            //TODO: Need to restructure below lines
            attribute.readEAPPacket(attribute.getAttributeData());
            EAPPacket requestPacket = attribute.getResponsePacket();

            // If this is first EAP packet we have to create EAP session for eap flow
            //else we have to get eap session and to check for state attribute. We are sending state attribute
            //in access-challenge, NAS must have to send state attribute unmodified in next EAP request.
            //TODO: we have to check for state attribute consistency. NEED to check RFC for same.

            if (Integer.valueOf(1).equals(requestPacket.getType())) {
                eapSession = new EAPSession(requestPacket.identity);
                eapSessions.put(requestPacket.identity, eapSession);
                eapSession.setAuthenticationType(EapType.fromName(authenticationType).getValue());

                //TODO: Response for above request should be part part of EAP state machine.

                EAPPacket eapResponosePacket = new EAPPacket(1, requestPacket.getEapIdentifier() + 1, 6, EapType.fromName(authenticationType).getValue(), "", 32);
                attributeFromRes.setAttributeValue(eapResponosePacket);
                accessResponse.addAttribute(attributeFromRes);

                byte[] zeroData = {00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00};
                RadiusAttribute messageAuthenticator = new RadiusAttribute(80, zeroData);
                accessResponse.addAttribute(messageAuthenticator);

                RadiusAttribute stateAttribute = new RadiusAttribute(24, DatatypeConverter.parseHexBinary("2122576121c65acd6821274141f61af3"));
                accessResponse.addAttribute(stateAttribute);

                byte[] messageAuthenticaorAttibute = EAPUtility.getMessageAuthenticatorAttribute(accessResponse, request, cltData);

                accessResponse.getAttribute(80).setAttributeData(messageAuthenticaorAttibute);

                return accessResponse;

            } else {
                RadiusAttribute userName = request.getAttribute(1);
                eapSession = eapSessions.get(userName.getAttributeValue());
                RadiusAttribute stateAttribute = request.getAttribute(24);
                if (stateAttribute == null) {
                    accessResponse = new RadiusPacket((AAAConstant.ACCESS_REJECT), request.getPacketIdentifier());
                    accessResponse.addAttribute("Reply-Message", "Missing state attribute");
                    return  accessResponse;
                }

                /** Handle client Hello Request */

                eapRequestPacket = evaluateEapPacket(attribute.getAttributeData(), eapSession);
                log.debug("Data from one EAP packet: " + RadiusUtil.getHexString(eapRequestPacket.getEapData()));

                if ((EAPConstant.EAP_TLS_TYPE.equals(eapRequestPacket.type) || EAPConstant.EAP_TTLS_TYPE.equals(eapRequestPacket.type) ) && Integer.valueOf(6).equals(eapRequestPacket.getEapLength()) && !eapSession.isServerFinishedDone()) {
                    log.debug("EAP acknowledgement of previous message is received, needs to send remaining data from the eap session: ");

                    int tlsDataLengthToBeSend = eapSession.getTlsDataLengthToBeSend();
                    byte[] toBeSendData = eapSession.getToBeSendData();
                    log.debug("Remaining data of eap of length: " + toBeSendData.length);
                    accessResponse = new RadiusPacket((AAAConstant.ACCESS_CHALLENGE), request.getPacketIdentifier());

                    accessResponse = generateEapResponse(toBeSendData, eapRequestPacket, accessResponse, eapSession);

                    return formEapPacket(accessResponse, request, cltData);

                } else if (EAPConstant.EAP_TTLS_TYPE.equals(eapRequestPacket.type)  && eapSession.isServerFinishedDone() && eapRequestPacket.getEapLength() > 6) {
                    log.debug("Encrypted EAP Message: or Application message");

                    byte[] eapData = eapRequestPacket.getEapData();

                    InputStream in = new ByteArrayInputStream(eapData);

                    ApplicationData applicationDatas = null;
                    while(in.available() > 1) {
                        int type = in.read() & 0xff;

                        if (EAPConstant.APPLICATION_DATA.equals(type)) {
                            applicationDatas = new ApplicationData(in);
                        }}

                    byte[] encryptedData = applicationDatas.getEncryptedDataBytes();

                    byte[] decryptedData = decryptAES(Arrays.copyOfRange(encryptedData, 0, 64), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientWriteKey(), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientIv());

                    System.out.println("Decrypted data: " + RadiusUtil.getHexString(decryptedData));

                    System.out.println("Decrypted data length: " + decryptedData.length);

                    byte[] applicationData = Arrays.copyOfRange(decryptedData, 16, decryptedData.length);

                    System.out.println("Application data: " + RadiusUtil.getHexString(applicationData));
                    System.out.println("Application data length: " + applicationData.length);

                    ByteArrayInputStream inputStream = new ByteArrayInputStream(applicationData);


                    int code = ( (inputStream.read() & 0x0ff) << 24 | inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

                    System.out.println("code of user-name: " + code);

                    int flag = inputStream.read() & 0x0ff;

                    System.out.println("flag of user-name: " + flag);

                    int length = (inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

                    byte[] value = new byte[length - 8];

                    inputStream.read(value);

                    int paddingUser = 0;
                            //inputStream.read() & 0x0ff;

                   // System.out.println("padding : " + paddingUser);

                    byte[] finalValue = Arrays.copyOfRange(value, 0, (value.length-paddingUser));

                    System.out.println("User name is: " + RadiusUtil.getStringFromUtf8(finalValue));

                  //  byte[] padding = new byte[3];
                    //inputStream.read(padding);

                    int code1 = ( (inputStream.read() & 0x0ff) << 24 | inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

                    System.out.println("code of user-password: " + code1);

                    int flag1 = inputStream.read() & 0x0ff;

                    System.out.println("flag of user-password: " + flag1);

                    int length1 = (inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

                    byte[] value1 = new byte[length1 - 8];

                    inputStream.read(value1);

                    System.out.println( "password bytes: "+ RadiusUtil.getHexString(value1));

                    byte[] bytes2 = removeZeroBytes(value1);

                    System.out.println( "password bytes value: " +  RadiusUtil.getStringFromUtf8(bytes2));


                    // for valid customer it should be set to accept
                    accessResponse = new RadiusPacket((AAAConstant.ACCESS_REJECT), request.getPacketIdentifier());

                    EAPPacket eapPacket = new EAPPacket(3,eapRequestPacket.getEapIdentifier(),4);

                    request.removeAttribute(request.getAttribute(RadiusAttributes.USER_NAME.getName()));

                    request.addAttribute(RadiusAttributes.USER_NAME.getName(), RadiusUtil.getStringFromUtf8(finalValue));
                    request.setUserPassword(RadiusUtil.getStringFromUtf8(bytes2));

                    EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
                    eapPacket1.setAttributeValue(eapPacket);
                    accessResponse.addAttribute(eapPacket1);

                    /**
                     * Create encryption key, encrypt it and add as VSA in access accept
                     *
                     */

                    byte[] keyBytes = CalculateMasterSecret.calculateMSKForTTLS128(eapSession.getTlsSecurityParameter().getClientRandom(), eapSession.getTlsSecurityParameter().getServerRandom(), eapSession.getTlsSecurityParameter().getMasterKey());
                    byte[] EncRECVKey = Arrays.copyOfRange(keyBytes, 0, 32);
                    byte[] EncSENDKey = Arrays.copyOfRange(keyBytes, 32, 64);

                    byte[] encryptedRecvKey = MsMppeRecvKey.encryptKey(EncRECVKey, cltData.getSharedKey(), request.getAuthenticator());
                    byte[] encryptedSendKey = MsMppeRecvKey.encryptKey(EncSENDKey, cltData.getSharedKey(), request.getAuthenticator());

                    RadiusAttribute recv = new RadiusAttribute(17, encryptedRecvKey);
                    recv.setVendorId(311);

                    RadiusAttribute send = new RadiusAttribute(16, encryptedSendKey);
                    send.setVendorId(311);

                    accessResponse.addAttribute(recv);
                    accessResponse.addAttribute(send);

                    return accessResponse;
                } else if (EAPConstant.EAP_TLS_TYPE.equals(eapRequestPacket.type)  && eapSession.isServerFinishedDone()) {
                    log.debug("EAP success Message to be send");

                    accessResponse = new RadiusPacket((AAAConstant.ACCESS_ACCEPT), request.getPacketIdentifier());

                    EAPPacket eapPacket = new EAPPacket(3,eapRequestPacket.getEapIdentifier(),4);

                    EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
                    eapPacket1.setAttributeValue(eapPacket);
                    accessResponse.addAttribute(eapPacket1);

                    /**
                     * Create encryption key, encrypt it and add as VSA in access accept
                     *
                     */

                    byte[] keyBytes = CalculateMasterSecret.calculateMSKForTLS128(eapSession.getTlsSecurityParameter().getClientRandom(), eapSession.getTlsSecurityParameter().getServerRandom(), eapSession.getTlsSecurityParameter().getMasterKey());
                    byte[] EncRECVKey = Arrays.copyOfRange(keyBytes, 0, 32);
                    byte[] EncSENDKey = Arrays.copyOfRange(keyBytes, 32, 64);

                    byte[] encryptedRecvKey = MsMppeRecvKey.encryptKey(EncRECVKey, cltData.getSharedKey(), request.getAuthenticator());
                    byte[] encryptedSendKey = MsMppeRecvKey.encryptKey(EncSENDKey, cltData.getSharedKey(), request.getAuthenticator());

                    RadiusAttribute recv = new RadiusAttribute(17, encryptedRecvKey);
                    recv.setVendorId(311);

                    RadiusAttribute send = new RadiusAttribute(16, encryptedSendKey);
                    send.setVendorId(311);

                    accessResponse.addAttribute(recv);
                    accessResponse.addAttribute(send);

                    return accessResponse;
                }


                if (!eapRequestPacket.isMoreFragmented)
                    {
                    byte[] eapData = eapRequestPacket.getEapData();
                    if (EAPConstant.EAP_TLS_TYPE.equals(eapRequestPacket.type) || EAPConstant.EAP_TTLS_TYPE.equals(eapRequestPacket.type)) {
                        log.debug("EAP data string: " + RadiusUtil.getHexString(eapData));

                        InputStream in = new ByteArrayInputStream(eapData);

                        HandshakeProtocol handshakeProtocol = null;

                        ClientHello clientHello = null;
                        ServerCertificate serverCertificate = null;
                        ClientKeyExchange clientKeyExchange = null;
                        CertificateVerify certificateVerify = null;
                        ChangeCipherSpecProtocol changeCipherSpecProtocol = null;
                        ApplicationData applicationData = null;
                        byte[] encryptedBytes = null;
                        boolean isFinishedMessage = false;

                        ByteArrayOutputStream outer = new ByteArrayOutputStream();
                        if (eapSession.getStream() != null) {
                            outer.write(eapSession.getStream());
                        }

                        while(in.available() > 1) {
                            int type = in.read() & 0xff;

                            if (EAPConstant.HANDSHAKE_PROTOCOL.equals(type)) {

                                if (changeCipherSpecProtocol != null) {
                                    isFinishedMessage = true;
                                    handshakeProtocol = new HandshakeProtocol(in, isFinishedMessage);
                                } else {
                                    handshakeProtocol = new HandshakeProtocol(in, isFinishedMessage);
                                }

                                outer.write(handshakeProtocol.getBytes());

                                if (handshakeProtocol.getClientHello() !=null ) {
                                    clientHello = handshakeProtocol.getClientHello();
                                    eapSession.getTlsSecurityParameter().setClientRandom(clientHello.getRandom());
                                } else if (handshakeProtocol.getServerCertificate() != null ) {
                                    serverCertificate = handshakeProtocol.getServerCertificate();
                                } else if (handshakeProtocol.getClientKeyExchange()!= null) {
                                    clientKeyExchange = handshakeProtocol.getClientKeyExchange();
                                } else if (handshakeProtocol.getCertificateVerify() != null) {
                                    certificateVerify = handshakeProtocol.getCertificateVerify();
                                } else {
                                    encryptedBytes = handshakeProtocol.getEncryptedMessage();
                                }
                            } else if (EAPConstant.CHANGE_CIPHER_SPEC.equals(type)) {
                                changeCipherSpecProtocol = new ChangeCipherSpecProtocol(in);
                            } else if (EAPConstant.APPLICATION_DATA.equals(type)) {
                                applicationData = new ApplicationData(in);
                                System.out.println("Application encrypted data: " + applicationData);
                            }
                        }
                            eapSession.setStream(outer.toByteArray());

                        byte[] tlsRecordResponse = new byte[0];

                        if (handshakeProtocol != null  && clientHello!= null) {
                            tlsRecordResponse = getTlsRecordBytesInRespondWithClientHello(clientHello.getRandom(), eapSession, keyStore);
                            log.debug("Executed client hello");
                        } else if (handshakeProtocol != null /*&& serverCertificate != null*/) {
                            tlsRecordResponse = getTlsRecordBytesInRespondWithClientCertificate(serverCertificate, clientKeyExchange, certificateVerify, encryptedBytes, eapSession, keyStore);
                            log.debug("Executed client verification");
                        }

                        accessResponse = new RadiusPacket((AAAConstant.ACCESS_CHALLENGE), request.getPacketIdentifier());

                        if (tlsRecordResponse == null) {
                            accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
                            accessResponse.addAttribute(REPLY_MESSAGE, "Unknown CA");
                            return accessResponse;
                        }

                        accessResponse = generateEapResponse(tlsRecordResponse, eapRequestPacket, accessResponse, eapSession);

                        // accessResponse = new RadiusPacket((AAAConstant.ACCESS_ACCEPT), request.getPacketIdentifier());
                        // EAPPacket eapResponosePacket = new EAPPacket(3, eapRequestPacket.getEapIdentifier() + 1, 4);

                        return formEapPacket(accessResponse, request, cltData);

                    }
                }

                //TODO: this should be point which could be part of EAP state machine
            }

        } else {

            RadiusAttribute userName = request.getAttribute(1);
            eapSession = eapSessions.get(userName.getAttributeValue());

            RadiusAttribute stateAttribute = request.getAttribute(24);
            if (stateAttribute == null) {
                accessResponse = new RadiusPacket((AAAConstant.ACCESS_REJECT), request.getPacketIdentifier());
                accessResponse.addAttribute("Reply-Message", "Missing state attribute");
                return accessResponse;
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            for (RadiusAttribute attribute : eapAttributes) {
                outStream.write(attribute.getAttributeData());
            }
            outStream.flush();

            byte[] eapData = outStream.toByteArray();

            log.debug("Multiple EAP attribute combine data : " + RadiusUtil.getHexString(eapData));

            eapRequestPacket = evaluateEapPacket(eapData, eapSession);
            eapSession.setBufferData(eapRequestPacket.getEapData());

            if (!eapRequestPacket.isMoreFragmented) {
                //TODO: this should be point to evaluate Handshake message and answer accordingly i.e. should be part of eap-state machine.
                log.debug("Entered once");
                {
                    byte[] eapData1 = eapRequestPacket.getEapData();
                    if (EAPConstant.EAP_TLS_TYPE.equals(eapRequestPacket.type) || EAPConstant.EAP_TTLS_TYPE.equals(eapRequestPacket.type)) {
                        log.debug("EAP data string: " + RadiusUtil.getHexString(eapData1));

                        InputStream in = new ByteArrayInputStream(eapData1);

                        HandshakeProtocol handshakeProtocol = null;

                        ClientHello clientHello = null;
                        ServerCertificate serverCertificate = null;
                        ClientKeyExchange clientKeyExchange = null;
                        CertificateVerify certificateVerify = null;
                        ChangeCipherSpecProtocol changeCipherSpecProtocol = null;
                        byte[] encryptedBytes = null;
                        boolean isFinishedMessage = false;

                        ByteArrayOutputStream sessionHandshakeMessages = new ByteArrayOutputStream();

                        if (eapSession.getStream() != null) {
                            sessionHandshakeMessages.write(eapSession.getStream());
                        }

                        while(in.available() > 1) {
                            int type = in.read() & 0xff;

                            if (EAPConstant.HANDSHAKE_PROTOCOL.equals(type)) {
                                if (changeCipherSpecProtocol != null) {
                                    isFinishedMessage = true;
                                    handshakeProtocol = new HandshakeProtocol(in, isFinishedMessage);
                                } else {
                                    handshakeProtocol = new HandshakeProtocol(in, isFinishedMessage);
                                }

                                log.debug("Handshake protocol: " + handshakeProtocol.toString());
                                sessionHandshakeMessages.write(handshakeProtocol.getBytes());

                                if (handshakeProtocol.getClientHello() !=null ) {
                                    clientHello = handshakeProtocol.getClientHello();
                                    eapSession.getTlsSecurityParameter().setClientRandom(clientHello.getRandom());
                                } else if (handshakeProtocol.getServerCertificate() != null ) {
                                    serverCertificate = handshakeProtocol.getServerCertificate();
                                } else if (handshakeProtocol.getClientKeyExchange()!= null) {
                                    log.debug("Executed client key exchange");
                                    clientKeyExchange = handshakeProtocol.getClientKeyExchange();
                                } else if (handshakeProtocol.getCertificateVerify() != null) {
                                    certificateVerify = handshakeProtocol.getCertificateVerify();
                                } else {
                                    encryptedBytes = handshakeProtocol.getEncryptedMessage();
                                }
                            } else if (EAPConstant.CHANGE_CIPHER_SPEC.equals(type)) {
                                changeCipherSpecProtocol = new ChangeCipherSpecProtocol(in);
                            }
                        }

                        eapSession.setStream(sessionHandshakeMessages.toByteArray());

                        byte[] tlsRecordResponse = new byte[0];

                        if (handshakeProtocol != null  && clientHello!= null) {
                            tlsRecordResponse = getTlsRecordBytesInRespondWithClientHello(clientHello.getRandom(), eapSession, keyStore);
                            log.debug("Executed client hello");
                        } else if (handshakeProtocol != null /*&& serverCertificate != null*/) {
                            tlsRecordResponse = getTlsRecordBytesInRespondWithClientCertificate(serverCertificate, clientKeyExchange, certificateVerify, encryptedBytes, eapSession, keyStore);
                            log.debug("Executed client verification hello");
                        }

                        accessResponse = new RadiusPacket((AAAConstant.ACCESS_CHALLENGE), request.getPacketIdentifier());

                        if (tlsRecordResponse == null) {
                            accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
                            accessResponse.addAttribute(REPLY_MESSAGE, "Unknown CA");
                            return accessResponse;
                        }

                        accessResponse = generateEapResponse(tlsRecordResponse, eapRequestPacket, accessResponse, eapSession);

                        // accessResponse = new RadiusPacket((AAAConstant.ACCESS_ACCEPT), request.getPacketIdentifier());
                        // EAPPacket eapResponosePacket = new EAPPacket(3, eapRequestPacket.getEapIdentifier() + 1, 4);

                        return formEapPacket(accessResponse, request, cltData);

                    }
                }

            }

            log.debug("TLS message will be like: " + RadiusUtil.getHexString(eapRequestPacket.getEapData()));

            log.debug("Response would be based on evaluation of TLS-Message which pending as of now, so sending normal ack message");

        }

        EAPPacket eapResponosePacket = new EAPPacket(1, eapRequestPacket.getEapIdentifier() + 1, 6, EapType.fromName(authenticationType).getValue(), "", 00);

        EAPAttribute eapAttribute = new EAPAttribute(AccessRequest.EAP);
        attributeFromRes.setAttributeValue(eapResponosePacket);
        accessResponse.addAttribute(attributeFromRes);

        return formEapPacket(accessResponse, request, cltData);

    }

    private byte[] generateServerVerifyMessage(EAPSession eapSession, byte[] clientFinishedBytes) throws Exception {

        ChangeCipherSpecProtocol cc = new ChangeCipherSpecProtocol();

        ByteArrayOutputStream handShakeData = new ByteArrayOutputStream();
        handShakeData.write(eapSession.getStream());
        handShakeData.write(clientFinishedBytes);

        //  byte[] allHandShakeData = eapSession.getStream();


        // generate encrypted data for server finished message

        log.debug("Handshake data for server finished: " + RadiusUtil.getHexString(handShakeData.toByteArray()));

        String serverFinished = "server finished";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashMessage = digest.digest(handShakeData.toByteArray());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(serverFinished.getBytes(StandardCharsets.UTF_8));
        out.write(hashMessage);

        byte[] seeds = out.toByteArray();

        byte[] a0 = seeds;

        byte[] a1 = calculateHMAC(a0, eapSession.getTlsSecurityParameter().getMasterKey());


        byte[] p1 = calculateHMAC(CalculateMasterSecret.concatenateByteArrays(a1, seeds), eapSession.getTlsSecurityParameter().getMasterKey());

        byte[] first12Bytes = Arrays.copyOfRange(p1, 0, 12);

        log.debug("Verify data for server finished: " + RadiusUtil.getHexString(first12Bytes));
        log.debug("Verify data for server finished length: " + first12Bytes.length);

        byte[] explicitIV = new byte[16];
        new SecureRandom().nextBytes(explicitIV);

        Finished finished = new Finished();
        finished.setVerifyData(first12Bytes);

        ByteArrayOutputStream encryptData = new ByteArrayOutputStream();
        encryptData.write(explicitIV);
        encryptData.write(finished.getBytes());
      //  encryptData.write(macHex);

        byte[] dataTobeEncrypted = encryptData.toByteArray();

        int remainder = (dataTobeEncrypted.length + 1) % 16;
        int padding = 16-remainder;

        for (int i=0; i < padding + 1; i++) {
            encryptData.write(padding & 0x0ff);
        }

        log.debug("Data to be encrypted: " + RadiusUtil.getHexString(encryptData.toByteArray()) + ": length: " + encryptData.toByteArray().length);
        byte[] serverWriteKey = eapSession.getTlsSecurityParameter().getSecurityKeys().getServerWriteKey();
        byte[] serverIv = eapSession.getTlsSecurityParameter().getSecurityKeys().getServerIv();
        byte[] serverMACKey = eapSession.getTlsSecurityParameter().getSecurityKeys().getServerMACKey();

        log.debug("Server write key: " + RadiusUtil.getHexString(serverWriteKey));
        log.debug("Server IV key: " + RadiusUtil.getHexString(serverIv));
        log.debug("Server MAC key: " + RadiusUtil.getHexString(serverMACKey));

        byte[] encrypt = encryptAES(encryptData.toByteArray(), serverWriteKey, serverIv);

        byte[] mac = generateMACForEncryptThenMac(serverMACKey, encrypt);
        log.debug("Encrypted server finished message: " + RadiusUtil.getHexString(encrypt));

        byte[] finalEncryptedData = concatenateByteArrays(encrypt, mac);

        byte[] encryptedHandshakeData = wrappedInTLSRecord(finalEncryptedData, EAPConstant.HANDSHAKE_PROTOCOL);

        log.debug("Encrypted server finished with Handshake data: " + RadiusUtil.getHexString(encryptedHandshakeData));

        ByteArrayOutputStream dataToBeSend = new ByteArrayOutputStream();
        dataToBeSend.write(cc.getBytes());
        dataToBeSend.write(encryptedHandshakeData);
        dataToBeSend.flush();

        log.debug("Final tls data: " + RadiusUtil.getHexString(dataToBeSend.toByteArray()));

        eapSession.setBufferData(new byte[0]);
        eapSession.setStream(new byte[0]);
        eapSession.setServerFinishedDone(true);

        return dataToBeSend.toByteArray();

        ///////////////////

       /*byte[] sequence= {00,00,00,00,00,00,00,00}; // 64 bit data
       byte[] handShake_header_version = {16,03, 03};
        byte[] datalen= {00, 10};
        //  finished;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(sequence);
        outputStream.write(handShake_header_version);
        outputStream.write(datalen);
        outputStream.write(finishedDataBytes);

        byte[] dataForMac = outputStream.toByteArray();

        byte[] macBytes = calculateHMAC(dataForMac, eapSession.getTlsSecurityParameter().getSecurityKeys().getServerMACKey());

        System.out.println("Calculated mac for server finished message: " + RadiusUtil.getHexString(macBytes));

        System.out.println("Calculated mac for server finished message length: " + macBytes.length);

        ByteArrayOutputStream dataTobeEncrypted = new ByteArrayOutputStream();
      //  dataTobeEncrypted.write(encryptionIV);
        dataTobeEncrypted.write(finishedDataBytes);
        dataTobeEncrypted.write(macBytes);

        int length = dataTobeEncrypted.toByteArray().length + 1;

        int remaining = length % 16;

        int paddingNumber = 16 - remaining;
        int noDataToBeInserted = paddingNumber + 1;

        byte[] padding = new byte[noDataToBeInserted];
        int a = 0;
        while (noDataToBeInserted > 0) {
            padding[a] = (byte) paddingNumber;
            a++;
            noDataToBeInserted--;
        }

        dataTobeEncrypted.write(padding);

        byte[] finalDataToBeEncrypted = dataTobeEncrypted.toByteArray();

        System.out.println("Data to be encrypted is: " + RadiusUtil.getHexString(finalDataToBeEncrypted));
        System.out.println("Data to be encrypted length: " + finalDataToBeEncrypted.length);

        byte[] encryptedData = encrypt(finalDataToBeEncrypted, eapSession.getTlsSecurityParameter().getSecurityKeys().getServerWriteKey(), encryptionIV).getBytes();

        System.out.println("Encrypted data: " + RadiusUtil.getHexString(encryptedData));
        System.out.println("Encrypted data length: " + encryptedData.length);

        ByteArrayOutputStream finalEncryptedPlusEncryptionIv = new ByteArrayOutputStream();
        finalEncryptedPlusEncryptionIv.write(encryptionIV);
        finalEncryptedPlusEncryptionIv.write(encryptedData);

        byte[] encryptedHandshakeData = wrappedInTLSRecord(finalEncryptedPlusEncryptionIv.toByteArray(), EAPConstant.HANDSHAKE_PROTOCOL);

      //  byte[] encryptedHandshakeData = wrappedInTLSRecord(finishedDataBytes, EAPConstant.HANDSHAKE_PROTOCOL);

        ByteArrayOutputStream dataToBeSend = new ByteArrayOutputStream();
        dataToBeSend.write(cc.getBytes());
   //     dataToBeSend.write(encryptedHandshakeData);
        dataToBeSend.write(encryptedHandshakeData);

        byte[] byteArray = dataToBeSend.toByteArray();
        System.out.println(" Response of server finished will be: " + RadiusUtil.getHexString(byteArray));
        System.out.println(" Response of server finished length: " + byteArray.length);

        return dataToBeSend.toByteArray();*/
    }

    private byte[] getTlsRecordBytesInRespondWithClientCertificate(ServerCertificate serverCertificate, ClientKeyExchange clientKeyExchange, CertificateVerify certificateVerify, byte[] encryptedBytes, EAPSession eapSession, KeyStoreImpl keyStore) throws Exception {
        byte[] bytesWithoutCertificateVerify = new byte[0];

        if (serverCertificate != null ) {
            byte[] clientCertificate = serverCertificate.getServerCertificate();
            Certificate certificate = CertificateUtil.parseCertificate(clientCertificate).get();

            boolean isVerified = keyStore.verifyCertificate(certificate);

            log.debug("Client certificate verified: " + isVerified);

            if (!isVerified) {
                return null;
            }

            PublicKey clientPublicKey = certificate.getPublicKey();
            byte[] signature = certificateVerify.getSignature();


            byte[] totalByteData = eapSession.getStream();


            int certificateVerifyLength = certificateVerify.getBytes().length;

            bytesWithoutCertificateVerify = Arrays.copyOfRange(totalByteData, 0, totalByteData.length - certificateVerifyLength);

            log.debug("Calculated hash of all packet bytes: " + RadiusUtil.getHexString(totalByteData));

            boolean verified = RSAPSSSignature.isVerified(clientPublicKey, bytesWithoutCertificateVerify, signature);

            log.debug("Client signature verified updated: " + verified);

        }
        if (serverCertificate == null) {
            bytesWithoutCertificateVerify = eapSession.getStream();
        }

        byte[] encryptedPreMasterSecret = clientKeyExchange.getPublicKey();

        byte[] preMasterSecretBytes = new byte[0];
        int cipherSuite1 = eapSession.getTlsSecurityParameter().getCipherSuite();
        if (Integer.valueOf(103).equals(cipherSuite1)) {

            DHKeyExchange dheKeyExchange = eapSession.getDheKeyExchange();
            preMasterSecretBytes = dheKeyExchange.generatePMS(encryptedPreMasterSecret);

        } else {
            Key serverPrivateKey = keyStore.getServerKey();
            preMasterSecretBytes = decryptWithJRadiusCode(serverPrivateKey, encryptedPreMasterSecret);
        }

        eapSession.getTlsSecurityParameter().setPreMasterKey(preMasterSecretBytes);
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashForMasterSecret = digest.digest(bytesWithoutCertificateVerify);

        System.out.println("****** Has for master secret *****" + RadiusUtil.getHexString(hashForMasterSecret));

        System.out.println("****** Pre Master secret *****" + RadiusUtil.getHexString(preMasterSecretBytes));

        byte[] extendedMasterSecret = CalculateMasterSecret.calculateExtendedMasterSecret(hashForMasterSecret, preMasterSecretBytes);

        System.out.println("****** extended master secret *****" + RadiusUtil.getHexString(extendedMasterSecret));

        CalculateMasterSecret.calculateMasterSecret(eapSession.getTlsSecurityParameter().getClientRandom(), eapSession.getTlsSecurityParameter().getServerRandom(), preMasterSecretBytes);

        eapSession.getTlsSecurityParameter().setMasterKey(extendedMasterSecret);

        int cipherSuite = eapSession.getTlsSecurityParameter().getCipherSuite();

        SecurityKeys securityKeys;
        if (Integer.valueOf(47).equals(cipherSuite) || Integer.valueOf(103).equals(cipherSuite1) ) {
            securityKeys = securityKeyForAES128(eapSession.getTlsSecurityParameter().getClientRandom(), eapSession.getTlsSecurityParameter().getServerRandom(), extendedMasterSecret);
        } else {
            securityKeys = securityKeyForAES256(eapSession.getTlsSecurityParameter().getClientRandom(), eapSession.getTlsSecurityParameter().getServerRandom(), extendedMasterSecret);
        }

        eapSession.getTlsSecurityParameter().setSecurityKeys(securityKeys);


        byte[] clientFinishedBytes = generateAndVerifyClientFinishedMAC(eapSession, eapSession.getStream(), encryptedBytes);

        /**
         * Once we found that encrypted data and calculate mac is same, then we have to create server
         * finished message.
         */
        return  generateServerVerifyMessage(eapSession, clientFinishedBytes);
        // byte[] bytes = EAPUtility.P_HASH(eapSession.getTlsSecurityParameter().getMasterKey(), clientVerify.getBytes(), seeds, 32, "SHA-256");

        // String calculatedHashString = RadiusUtil.getHexString(bytes);

        // System.out.println("Newly created hash would be: " + calculatedHashString.length()/2 + ": " + calculatedHashString);


        //  System.out.println("Decrypt encrypted received from pcap for encrypted data and above value should be same");

        //  byte[] masterSecret = CalculateMasterSecret.calculateMasterSecret(RadiusUtil.getBytesFromHexString("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f"), RadiusUtil.getBytesFromHexString("707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f"), RadiusUtil.getBytesFromHexString("df4a291baa1eb7cfa6934b29b474baad2697e29f1f920dcc77c8a0a088447624"));

        // calculate master secret

    }

    private byte[] generateAndVerifyClientFinishedMAC(EAPSession eapSession, byte[] totalByteData, byte[] encryptedBytes) throws Exception {
        log.debug("All handshake message string for client finished: " + RadiusUtil.getHexString(totalByteData));

        String encryptedBytesHexString = RadiusUtil.getHexString(encryptedBytes);
        log.debug("Received Encrypted Data is: " + encryptedBytesHexString);
        log.debug("Encrypted data length: " + encryptedBytes.length);

        byte[] clientFinishedDecryptedBytes = decryptAES(Arrays.copyOfRange(encryptedBytes,0,48), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientWriteKey(), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientIv());
        log.debug("decrypted data: " + RadiusUtil.getHexString(clientFinishedDecryptedBytes));
        log.debug("decrypted data length: " + clientFinishedDecryptedBytes.length);

        byte[] explicitIV = Arrays.copyOfRange(clientFinishedDecryptedBytes, 0, 16);
        log.debug("Initialization vector from decrypted data: " + RadiusUtil.getHexString(explicitIV));

        byte[] clientMAC = Arrays.copyOfRange(encryptedBytes, 48, 68);
        log.debug("Client finished received MAC is: " + RadiusUtil.getHexString(clientMAC));

        String clientVerify = "client finished";

        MessageDigest digest1 = MessageDigest.getInstance("SHA-256");
        byte[] hashMessage = digest1.digest(totalByteData);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(clientVerify.getBytes(StandardCharsets.UTF_8));
        out.write(hashMessage);

        byte[] seeds = out.toByteArray();

        byte[] a0 = seeds;

        byte[] a1 = calculateHMAC(a0, eapSession.getTlsSecurityParameter().getMasterKey());


        byte[] p1 = calculateHMAC(CalculateMasterSecret.concatenateByteArrays(a1, seeds), eapSession.getTlsSecurityParameter().getMasterKey());

        byte[] first12Bytes = Arrays.copyOfRange(p1, 0, 12);

        log.debug("Verify data for calculated client finished: " + RadiusUtil.getHexString(first12Bytes));
        log.debug("Verify data for calculated client finished length: " + first12Bytes.length);

        ByteArrayOutputStream encryptData = new ByteArrayOutputStream();

        Finished finished = new Finished();
        finished.setVerifyData(first12Bytes);

        encryptData.write(explicitIV);
        encryptData.write(finished.getBytes());
        //  encryptData.write(macHex);

        byte[] dataTobeEncrypted = encryptData.toByteArray();

        int remainder = (dataTobeEncrypted.length + 1) % 16;
        int padding = 16-remainder;

        for (int i=0; i < padding + 1; i++) {
            encryptData.write(padding & 0x0ff);
        }

        log.debug("Data to be encrypted: " + RadiusUtil.getHexString(encryptData.toByteArray()) + ": length: " + encryptData.toByteArray().length);

        byte[] encrypt = encryptAES(encryptData.toByteArray(), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientWriteKey(), eapSession.getTlsSecurityParameter().getSecurityKeys().getClientIv());

        byte[] mac = generateMACForEncryptThenMac(eapSession.getTlsSecurityParameter().getSecurityKeys().getClientMACKey(), encrypt);
        log.debug("Calculated MAC for client finished message: " + RadiusUtil.getHexString(mac));

        boolean isMacEquals = Arrays.equals(clientMAC, mac);

        log.debug("Calculated and received mac for client finished message is: " + isMacEquals);

      //  encryptData.write(mac);

        log.debug("Client encrypted data and calculated encrypted data is equal: " + Arrays.equals(encryptedBytes, concatenateByteArrays(encrypt, mac)));

        //TODO: Based on above equals we have to send server finished message.

        return finished.getBytes();
    }

    public static byte[] decryptWithPrivateKey(byte[] input, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    private RadiusPacket generateEapResponse(byte[] tlsRecordStream, EAPPacket eapRequestPacket, RadiusPacket accessResponse, EAPSession eapSession) throws IOException {
        int tlsRecordLength = tlsRecordStream.length;
        ByteArrayInputStream in = new ByteArrayInputStream(tlsRecordStream);

        log.debug("Total length of all TLS record is: " + tlsRecordStream.length);
        log.debug("Total TLS data hexString: " + RadiusUtil.getHexString(tlsRecordStream));

        if (tlsRecordLength > 243) {
            // length flag would be set true
            // suppose we have decided to send maximum 6 EAP packets in one RADIUS packet, then we will check for
            // fragmentation is required or not and based on that fragmented flag will be set.

            // 5 *253 ()+ 243 = 1508 initially set this, was working fine with test tool eapol_test
            // 3* 253 + 243 = 1002

            // suppose we have tlsRecordLength > 1512 then we need fragmentation and we set more fragment flag true.
            //

            if (tlsRecordLength > 1002) {
                // set first packet with every details and send data in multiple packet,
                EAPPacket eapPacket = new EAPPacket(1,eapRequestPacket.getEapIdentifier()+1, 1002+10);
                eapPacket.setFlag(192);
                eapPacket.setType(eapRequestPacket.getType());
                eapPacket.setEapTlsLength(tlsRecordLength);
                byte[] eapData = new byte[243];
                in.read(eapData);
                eapPacket.setEapData(eapData);

                EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
                eapPacket1.setAttributeValue(eapPacket);
                accessResponse.addAttribute(eapPacket1);

                EAPAttribute eapPacket2 = new EAPAttribute(AccessRequest.EAP);
                byte[] eapBytes2 = new byte[253];
                in.read(eapBytes2);
                eapPacket2.setAttributeData(eapBytes2);
                accessResponse.addAttribute(eapPacket2);

                EAPAttribute eapPacket3 = new EAPAttribute(AccessRequest.EAP);
                byte[] eapBytes3 = new byte[253];
                in.read(eapBytes3);
                eapPacket3.setAttributeData(eapBytes3);
                accessResponse.addAttribute(eapPacket3);

                EAPAttribute eapPacket4 = new EAPAttribute(AccessRequest.EAP);
                byte[] eapBytes4 = new byte[253];
                in.read(eapBytes4);
                eapPacket4.setAttributeData(eapBytes4);
                accessResponse.addAttribute(eapPacket4);

                /*EAPAttribute eapPacket5 = new EAPAttribute(AccessRequest.EAP);
                byte[] eapBytes5 = new byte[253];
                in.read(eapBytes5);
                eapPacket5.setAttributeData(eapBytes5);
                accessResponse.addAttribute(eapPacket5);

                EAPAttribute eapPacket6 = new EAPAttribute(AccessRequest.EAP);
                byte[] eapBytes6 = new byte[253];
                in.read(eapBytes6);
                eapPacket6.setAttributeData(eapBytes6);
                accessResponse.addAttribute(eapPacket6);
*/
                eapSession.setTlsDataLengthToBeSend(tlsRecordLength);

                int remainingData = in.available();
                byte[] remainingBytes = new byte[remainingData];
                in.read(remainingBytes);
                eapSession.setToBeSendData(remainingBytes);
                eapSession.setTlsDataLengthToBeSend(tlsRecordLength);
            } else {
                // more fragments flag would be not set as whole data can reside into one  RADIUS packet
                EAPPacket eapPacket = new EAPPacket(1,eapRequestPacket.getEapIdentifier()+1, tlsRecordLength + 10);
                eapPacket.setFlag(128);
                eapPacket.setType(eapRequestPacket.getType());
                eapPacket.setEapTlsLength(tlsRecordLength);
                byte[] eapData = new byte[243];
                in.read(eapData);
                eapPacket.setEapData(eapData);

                EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
                eapPacket1.setAttributeValue(eapPacket);
                accessResponse.addAttribute(eapPacket1);

                int remainingDataLength = tlsRecordLength - 243;

                while(remainingDataLength > 253) {
                    byte[] remainingBytes = new byte[253];
                    in.read(remainingBytes);

                    EAPAttribute eapPackets = new EAPAttribute(AccessRequest.EAP);
                    eapPackets.setAttributeData(remainingBytes);
                    accessResponse.addAttribute(eapPackets);
                    remainingDataLength = in.available();
                }

                if (remainingDataLength > 0) {
                    byte[] remainingBytes = new byte[remainingDataLength];
                    in.read(remainingBytes);

                    EAPAttribute eapPackets = new EAPAttribute(AccessRequest.EAP);
                    eapPackets.setAttributeData(remainingBytes);
                    accessResponse.addAttribute(eapPackets);
                }
            }
        } else {
            EAPPacket eapPacket = new EAPPacket(1,eapRequestPacket.getEapIdentifier()+1, tlsRecordLength + 10);
            eapPacket.setFlag(128);
            eapPacket.setType(eapRequestPacket.getType());
            eapSession.setTlsDataLengthToBeSend(tlsRecordLength);
            eapPacket.setEapTlsLength(tlsRecordLength);
            byte[] eapData = new byte[tlsRecordLength];
            in.read(eapData);
            eapPacket.setEapData(eapData);

            EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
            eapPacket1.setAttributeValue(eapPacket);
            eapPacket1.setAttributeData(eapData);
            accessResponse.addAttribute(eapPacket1);
        }
        return accessResponse;
    }

    public byte[] getTlsRecordBytesInRespondWithClientHello(byte[] random, EAPSession eapSession, KeyStoreImpl keyStore) throws IOException, CertificateEncodingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, KeyStoreException {

        ByteArrayOutputStream outputStream = null;
        if (eapSession.getStream() == null) {
            outputStream = new ByteArrayOutputStream();
        } else {
            outputStream = new ByteArrayOutputStream();
            outputStream.write(eapSession.getStream());
        }

        ServerHello serverHello = new ServerHello();
        byte[] serverHelloBytes = serverHello.getBytes();
        eapSession.getTlsSecurityParameter().setServerRandom(serverHello.getServerRandom());

        eapSession.getTlsSecurityParameter().setCipherSuite(serverHello.getCipherSuite());

        log.debug("ServerHello: " + RadiusUtil.getHexString(serverHelloBytes));

        outputStream.write(serverHelloBytes);

        byte[] HPServerHello = wrappedInTLSRecord(serverHelloBytes, EAPConstant.HANDSHAKE_PROTOCOL);

        byte[] serverCertificate = keyStore.getKeystore().getCertificate("1").getEncoded();
        byte[] caCertificate = keyStore.getTrustStore().getCertificate("serverca").getEncoded();

        ServerCertificate serverCertificateRequest = new ServerCertificate(serverCertificate, caCertificate);
        byte[] serverCertificateRequestBytes = serverCertificateRequest.getBytes();

        outputStream.write(serverCertificateRequestBytes);

        log.debug("ServerCertificate: " + serverCertificateRequestBytes.length + " data: " + RadiusUtil.getHexString(serverCertificateRequestBytes));

        byte[] HPServerCertificate = wrappedInTLSRecord(serverCertificateRequestBytes, EAPConstant.HANDSHAKE_PROTOCOL);


        byte[] HP_serverKeyExchange = new byte[0];
        if (Integer.valueOf(103).equals(serverHello.getCipherSuite())) {
            ServerKeyExchangeGenerator generator = new ServerKeyExchangeGenerator();
            ServerKeyExchange serverKeyExchange = generator.generateServeKeyExchange(random, serverHello.getServerRandom(), serverHello.getCipherSuite(), serverHello.getServerRandom(), eapSession.getTlsSecurityParameter().getClientRandom(), eapSession);

            byte[] serverKeyExchangeBytes = serverKeyExchange.getBytes();
            outputStream.write(serverKeyExchangeBytes);

            log.debug("ServerKeyExchange: " + serverCertificateRequestBytes.length + " data: " + RadiusUtil.getHexString(serverKeyExchangeBytes));

            HP_serverKeyExchange = wrappedInTLSRecord(serverKeyExchangeBytes, EAPConstant.HANDSHAKE_PROTOCOL);

        }

        X509Certificate certificate = (X509Certificate) keyStore.getTrustStore().getCertificate("serverca");
        byte[] distingused = certificate.getSubjectX500Principal().getEncoded();
        //  byte[] dis = new byte[distingused.length - 3];

        //dis =  Arrays.copyOfRange(distingused,3,distingused.length);

        //    System.out.println(" dis" + RadiusUtil.getHexString(dis));


        // System.out.println("Distingused data length: " + distingused.length);
        byte[] HPClientCertificateRequest = new byte[0];

        if (EapType.EAP_TLS.getValue().compareTo(eapSession.getAuthenticationType()) == 0) {
            ClientCertificateRequest clientCertificateRequest = new ClientCertificateRequest(distingused);
            byte[] clientRequestBytes = clientCertificateRequest.getBytes();
            outputStream.write(clientRequestBytes);

            log.debug("client request: " + clientRequestBytes.length + " Data: " + RadiusUtil.getHexString(clientRequestBytes));

            System.out.println("********************************* client request: " + clientRequestBytes.length + " Data: " + RadiusUtil.getHexString(clientRequestBytes));

            HPClientCertificateRequest = wrappedInTLSRecord(clientRequestBytes, EAPConstant.HANDSHAKE_PROTOCOL);

        }

        ServerHelloDone serverHelloDone = new ServerHelloDone();
        byte[] serverHelloDoneBytes = serverHelloDone.getBytes();

        outputStream.write(serverHelloDoneBytes);

        log.debug("server hello done: " + serverHelloBytes.length + " Data: " + RadiusUtil.getHexString(serverHelloDoneBytes));
        byte[] HPServerHelloDone = wrappedInTLSRecord(serverHelloDoneBytes, EAPConstant.HANDSHAKE_PROTOCOL);


        eapSession.setStream(outputStream.toByteArray());

        int clientHelloResponseLength = HPServerHello.length
                + HPServerCertificate.length +
                HP_serverKeyExchange.length +
                HPClientCertificateRequest.length +
                + HPServerHelloDone.length;

        ByteArrayOutputStream tlsRecordStream = new ByteArrayOutputStream(clientHelloResponseLength);
        tlsRecordStream.write(HPServerHello);
        tlsRecordStream.write(HPServerCertificate);
        tlsRecordStream.write(HP_serverKeyExchange);
        tlsRecordStream.write(HPClientCertificateRequest);
        tlsRecordStream.write(HPServerHelloDone);

        //  eapSession.setStream(tlsRecordStream);

        eapSession.setBufferData(new byte[0]);
        return tlsRecordStream.toByteArray();
    }

    private byte[] wrappedInTLSRecord(byte[] serverHelloBytes, Integer handshakeProtocol) throws IOException {
        int recordLength = serverHelloBytes.length;
        ByteArrayOutputStream bos= new ByteArrayOutputStream(recordLength + 5);
        bos.write(handshakeProtocol & 0x0ff);
        bos.write(771 >> 8 & 0x0ff);
        bos.write(771 & 0x0ff);
        bos.write(recordLength >> 8 & 0x0ff);
        bos.write(recordLength & 0x0ff);
        bos.write(serverHelloBytes);
        return bos.toByteArray();
    }

    private EAPPacket evaluateEapPacket(byte[] eapData, EAPSession eapSession) {
        EAPPacket eapPacket = new EAPPacket();
        byte[] bufferEapData = new byte[0];
        int pos = 0;
        int eapCodeType = eapData[pos] & 0x0ff;
        int eapIdentifier = eapData[++pos] & 0x0ff;
        int eapLength = (eapData[++pos] & 0x0ff) << 8 | (eapData[++pos] & 0x0ff);
        int type = eapData[++pos] & 0x0ff;
        int eapFlag = 0;
        boolean moreFragmentIncluded = false;

        if (type == 21 || type == 13) {
            eapFlag = eapData[++pos] & 0x0ff;

            //Check for isLengthAvailable flag and if set, set total length of EAP data in
            //EAP session
            //use stored byte from eap session and append data from current packet

            int lengthFlag = eapFlag & 0x80;
            boolean lengthIncluded = lengthFlag > 1;

            int moreFragmentFlag = eapFlag & 0x40;
            moreFragmentIncluded = moreFragmentFlag > 1;

            int tlsStartFlag = eapFlag & 0x20;
            boolean tlsStartIncluded = tlsStartFlag > 1;

            int eapWholePacketLength = 0;
            int offSetPoint = 0;

            if (lengthIncluded) {
                eapWholePacketLength = ((eapData[++pos] & 0x0ff) << 24 | (eapData[++pos] & 0x0ff) << 16 | (eapData[++pos] & 0xff)  << 8 | (eapData[++pos] & 0xff));
                offSetPoint = 10;
            } else {
                offSetPoint = 6;
            }

            if (moreFragmentIncluded) {
                log.debug("More fragmented true");

                byte[] oldEapData = eapSession.getBufferData();
                int bufferDataLength = oldEapData.length;

                if (bufferDataLength > 1) {
                    log.debug("More that 2 fragmented packet: ");
                    log.debug("We already some eap data in session: " + RadiusUtil.getHexString(eapSession.getBufferData()));
                    bufferEapData = new byte[(bufferDataLength) + (eapData.length - offSetPoint)];
                    System.arraycopy(oldEapData,0, bufferEapData, 0, bufferDataLength);
                    System.arraycopy(eapData, offSetPoint, bufferEapData, bufferDataLength, eapData.length - offSetPoint);
                    log.debug("Accumalted EAP Data  : " + RadiusUtil.getHexString(bufferEapData));
                } else {
                    // here whole data can be added
                    log.debug("First fragmented data: ");
                    bufferEapData = new byte[eapData.length-offSetPoint];
                    System.arraycopy(eapData, offSetPoint, bufferEapData,0,eapData.length - offSetPoint);
                    log.debug("Fragmented EAP Data : " + RadiusUtil.getHexString(bufferEapData));
                }

                log.debug("Data will be added into EAP session as buffer data : " + RadiusUtil.getHexString(bufferEapData));
                eapSession.setBufferData(bufferEapData);

                log.debug("Sending normal ack message as more fragmented flag is true: ");

            } else {
                log.debug("No more fragmented data");

                byte[] oldEapData = eapSession.getBufferData();
                int oldBufferDataLength = oldEapData.length;

                if (oldBufferDataLength > 0) {
                    log.debug("Last fragmented data:");
                    log.debug("We already some eap data in session: " + RadiusUtil.getHexString(eapSession.getBufferData()));
                    bufferEapData = new byte[(oldBufferDataLength ) + (eapData.length - offSetPoint)];
                    System.arraycopy(oldEapData,0, bufferEapData, 0, oldBufferDataLength);
                    System.arraycopy(eapData, offSetPoint, bufferEapData, oldBufferDataLength, eapData.length - offSetPoint);
                    log.debug("Accumalated EAP Data  : " + RadiusUtil.getHexString(bufferEapData));

                } else {
                    bufferEapData = new byte[eapData.length-offSetPoint];
                    System.arraycopy(eapData, offSetPoint, bufferEapData,0,eapData.length-offSetPoint);
                }

            }
        }

        eapPacket.setEapCodeType(eapCodeType);
        eapPacket.setEapIdentifier(eapIdentifier);
        eapPacket.setEapLength(eapLength);
        eapPacket.setType(type);
        eapPacket.setFlag(eapFlag);
        eapPacket.setEapData(bufferEapData);
        eapPacket.setMoreFragmented(moreFragmentIncluded);

        return eapPacket;
    }

    public static RadiusPacket formEapPacket(RadiusPacket accessResponse, AccessRequest request, Client cltData) {

        /*EAPAttribute attributeFromRes = new EAPAttribute(AccessRequest.EAP);
        attributeFromRes.setAttributeValue(eapPacket);
        accessResponse.addAttribute(attributeFromRes);*/

        byte[] zeroData = {00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00};
        RadiusAttribute messageAuthenticator = new RadiusAttribute(80, zeroData);
        accessResponse.addAttribute(messageAuthenticator);

        RadiusAttribute stateAttributes = new RadiusAttribute(24, DatatypeConverter.parseHexBinary("2122576121c65acd6821274141f61af3"));
        accessResponse.addAttribute(stateAttributes);

        byte[] messageAuthenticaorAttibute = EAPUtility.getMessageAuthenticatorAttribute(accessResponse, request, cltData);

        accessResponse.getAttribute(80).setAttributeData(messageAuthenticaorAttibute);
        return  accessResponse;
    }

    public static byte[] decryptWithJRadiusCode(Key privateKey, byte[] encrypted) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        Cipher cipher =  Cipher.getInstance( "RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        SecretKey preMaster = (SecretKey) cipher.unwrap(encrypted,
                "TlsRsaPremasterSecret", Cipher.SECRET_KEY);
        byte[] encoded = preMaster.getEncoded();
        log.debug("pre-master key length: " + encoded.length);
        log.debug("pre-master key: " + RadiusUtil.getHexString(encoded));
        return encoded;
    }

    private static void hmac_hash(Digest digest, byte[] secret, byte[] seed, byte[] out)
    {
        HMac mac = new HMac(digest);
        KeyParameter param = new KeyParameter(secret);
        byte[] a = seed;
        int size = digest.getDigestSize();
        int iterations = (out.length + size - 1) / size;
        byte[] buf = new byte[mac.getMacSize()];
        byte[] buf2 = new byte[mac.getMacSize()];
        for (int i = 0; i < iterations; i++)
        {
            mac.init(param);
            mac.update(a, 0, a.length);
            mac.doFinal(buf, 0);
            a = buf;
            mac.init(param);
            mac.update(a, 0, a.length);
            mac.update(seed, 0, seed.length);
            mac.doFinal(buf2, 0);
            System.arraycopy(buf2, 0, out, (size * i), Math.min(size, out.length - (size * i)));
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] macKey = RadiusUtil.getBytesFromHexString("76c53218a7e637c4503f1a092ec749581054e5ed");

        byte[] encryptedValue = RadiusUtil.getBytesFromHexString(
                "8f90b758d6d19f80c0995860e1be0cfe383c71d17d6abd3ff8363db77fb2f955fc26416922c9bdf87d2c0c32db3468ce");

        byte[] expectedDecrypted = RadiusUtil.getBytesFromHexString("c732d73e5b98a8c21d8b1b338ec1c608" +
                "1400000c986cd226785525955fcf89570f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f");
        byte[] expectedMac = RadiusUtil.getBytesFromHexString("e8302c1f94b7a157d983da0876652acd734301aa");

        byte[] bytes1 = generateMACForEncryptThenMac(macKey, encryptedValue);

        log.debug("Expected Mac and calculated Mac: " + Arrays.equals(expectedMac, bytes1));


        byte[] encryptedValueForLocal = RadiusUtil.getBytesFromHexString("db1fa666cffa0ea48c519b20533ad6b8079cf673520e9c25fe982ac3f66c9ca7c3feb98110c1471638efac5e372698fa");

        byte[] macKeyLocal = RadiusUtil.getBytesFromHexString("9e76f79d3b13f26199ef510863c26f3985ad5e8c");

        log.debug("local mac: " + RadiusUtil.getHexString(generateMACForEncryptThenMac(macKeyLocal, encryptedValueForLocal)));

        byte[] encryptedData = RadiusUtil.getBytesFromHexString("63fd5ee06850aec8c96c93b8526b8334b2a677d62bf8d38fa6e4b827cc29c87356908cd56b3108fde07bcf040fee7acb");
        byte[] bytes = decryptAES(encryptedData, RadiusUtil.getBytesFromHexString("96fa13a1b668babdbb7d3eb57f27d5d8"), RadiusUtil.getBytesFromHexString("ab9e1f86d79c2a08a2af5eadb3babee6"));

        log.debug(RadiusUtil.getHexString(bytes));
    }
    public static byte[] generateMACForEncryptThenMac(byte[] macKey, byte[] encryptedIvVerifyPadding) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        int sequenceNuber = 0; // 8 byte
        int messageType = 22; // 1 byte
        int vesion = 771; //2 bytes

        ByteArrayOutputStream macOut = new ByteArrayOutputStream();

        macOut.write(sequenceNuber >> 56 & 0x0ff);
        macOut.write(sequenceNuber >> 48 & 0x0ff);
        macOut.write(sequenceNuber >> 40 & 0x0ff);
        macOut.write(sequenceNuber >> 32 & 0x0ff);
        macOut.write(sequenceNuber >> 24 & 0x0ff);
        macOut.write(sequenceNuber >> 16 & 0x0ff);
        macOut.write(sequenceNuber >> 8 & 0x0ff);
        macOut.write(sequenceNuber & 0x0ff);

        macOut.write(messageType  & 0x0ff);

        macOut.write(vesion >> 8 & 0x0ff);
        macOut.write(vesion & 0x0ff);

        macOut.write(48 >> 8 & 0x0ff);
        macOut.write(48 & 0x0ff);

        macOut.write(encryptedIvVerifyPadding);

        byte[] macHex = calculateHMACSHA(macOut.toByteArray(), macKey);

        log.debug("Calculated Mac: " + RadiusUtil.getHexString(macHex));

        return  macHex;
    }

    public static byte[] generateMacThenEncrypt(byte[] verifyData, byte[] macKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        byte[] encryptionIV = new byte[16]; // default server random value is 32 byte
        new SecureRandom().nextBytes(encryptionIV);

        Finished finished = new Finished();
        finished.setVerifyData(verifyData);

        byte[] finishedDataBytes = finished.getBytes();

        int sequenceNuber = 0; // 8 byte
        int messageType = 22; // 1 byte
        int vesion = 771; //2 bytes

        ByteArrayOutputStream macOut = new ByteArrayOutputStream();

        macOut.write(sequenceNuber >> 56 & 0x0ff);
        macOut.write(sequenceNuber >> 48 & 0x0ff);
        macOut.write(sequenceNuber >> 40 & 0x0ff);
        macOut.write(sequenceNuber >> 32 & 0x0ff);
        macOut.write(sequenceNuber >> 24 & 0x0ff);
        macOut.write(sequenceNuber >> 16 & 0x0ff);
        macOut.write(sequenceNuber >> 8 & 0x0ff);
        macOut.write(sequenceNuber & 0x0ff);


        macOut.write(messageType  & 0x0ff);

        macOut.write(vesion >> 8 & 0x0ff);
        macOut.write(vesion & 0x0ff);

        macOut.write(16 >> 8 & 0x0ff);
        macOut.write(16 & 0x0ff);

        macOut.write(finished.getBytes());


        byte[] macHex = calculateHMACSHA(macOut.toByteArray(), macKey);

        log.debug("Calculated Mac for server finished: " + RadiusUtil.getHexString(macHex));

        return macHex;


    }
}

