package com.savbill.radius.aaa.packet;

import java.security.MessageDigest;

import com.savbill.radius.aaa.util.RadiusUtil;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;


public class DmRequest extends RadiusPacket {
	private static final Logger log = LoggerFactory.getLogger(DmRequest.class);

	public DmRequest() {
		super(DISCONNECT_REQUEST, getNextPacketIdentifier());
	}
	

	
	/**
	 * @see AccountingRequest#updateRequestAuthenticator(String, int, byte[])
	 */
	protected byte[] updateRequestAuthenticator(String sharedSecret,
			int packetLength, byte[] attributes) {
		
		log.debug("Override updateRequestAuthenticator:sharedSecret:"+sharedSecret+":packetLength:"+packetLength+":attributes:"+attributes);
		byte[] authenticator = new byte[16];
		for (int i = 0; i < 16; i++)
			authenticator[i] = 0;
		MessageDigest md5 = getMd5Digest();
		md5.reset();
		md5.update((byte) getPacketType());
		md5.update((byte) getPacketIdentifier());
		md5.update((byte) (packetLength >> 8));
		md5.update((byte) (packetLength & 0xff));
		md5.update(authenticator, 0, authenticator.length);
		md5.update(attributes, 0, attributes.length);
		md5.update(RadiusUtil.getUtf8Bytes(sharedSecret));
		return md5.digest();
	}

}

