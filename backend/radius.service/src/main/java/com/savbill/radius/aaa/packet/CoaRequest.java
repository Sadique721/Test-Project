package com.savbill.radius.aaa.packet;

import java.security.MessageDigest;

import com.savbill.radius.aaa.util.RadiusUtil;

public class CoaRequest extends RadiusPacket {

	public CoaRequest() {
		super(COA_REQUEST, getNextPacketIdentifier()); 
	}
	
	public CoaRequest(int RequestType) {
		super(RequestType, getNextPacketIdentifier()); 
	}
	
	/**
	 * @see AccountingRequest#updateRequestAuthenticator(String, int, byte[])
	 */
	protected byte[] updateRequestAuthenticator(String sharedSecret,
			int packetLength, byte[] attributes) {
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

	/**
	 * This method encodes the plaintext user password according to RFC 2865.
	 * @param userPass the password to encrypt
	 * @param sharedSecret shared secret
	 * @return the byte array containing the encrypted password
	 */
	public byte[] encodePapPassword(final byte[] userPass, byte[] sharedSecret,byte[] authenticator) {
		// the password must be a multiple of 16 bytes and less than or equal
		// to 128 bytes. If it isn't a multiple of 16 bytes fill it out with zeroes
		// to make it a multiple of 16 bytes. If it is greater than 128 bytes
		// truncate it at 128.
		byte[] userPassBytes = null;
		if (userPass.length > 128){
			userPassBytes = new byte[128];
			System.arraycopy(userPass, 0, userPassBytes, 0, 128);
		} else {
			userPassBytes = userPass;
		}

		// declare the byte array to hold the final product
		byte[] encryptedPass = null;
		if (userPassBytes.length < 128) {
			if (userPassBytes.length % 16 == 0) {
				// tt is already a multiple of 16 bytes
				encryptedPass = new byte[userPassBytes.length];
			} else {
				// make it a multiple of 16 bytes
				encryptedPass = new byte[((userPassBytes.length / 16) * 16) + 16];
			}
		} else {
			// the encrypted password must be between 16 and 128 bytes
			encryptedPass = new byte[128];
		}

		// copy the userPass into the encrypted pass and then fill it out with zeroes
		System.arraycopy(userPassBytes, 0, encryptedPass, 0, userPassBytes.length);
		for (int i = userPassBytes.length; i < encryptedPass.length; i++) {
			encryptedPass[i] = 0;
		}

		// digest shared secret and authenticator
		MessageDigest md5 = getMd5Digest();
		byte[] lastBlock = new byte[16];

		for (int i = 0; i < encryptedPass.length; i+=16) {
			md5.reset();
			md5.update(sharedSecret);
			md5.update(i == 0 ? authenticator : lastBlock);
			byte bn[] = md5.digest();

			System.arraycopy(encryptedPass, i, lastBlock, 0, 16);

			// perform the XOR as specified by RFC 2865.
			for (int j = 0; j < 16; j++)
				encryptedPass[i + j] = (byte)(bn[j] ^ encryptedPass[i + j]);
		}

		return encryptedPass;
	}
	
}
