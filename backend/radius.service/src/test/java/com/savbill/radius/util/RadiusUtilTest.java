//package com.savbill.radius.util;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import org.junit.Test;
//
//import com.savbill.radius.aaa.util.RadiusUtil;
//
//public class RadiusUtilTest {
//	
//	 
//	
//	@Test
//	public void testGetHexString() {
//		String test = RadiusUtil.getHexString("Test HaxString".getBytes());
//		assertEquals("0x5465737420486178537472696e67", test);
//	}
//
//	@Test
//	public void testGetStringFromUtf8() {
//		String test = RadiusUtil.getStringFromUtf8("Test String".getBytes());
//		assertEquals("Test String", test);
//	}
//
//	@Test
//	public void testGetUtf8BytesTest() {
//		byte[] test = RadiusUtil.getUtf8Bytes("Test HaxString");
//		assertNotNull(test);
//	}
//
//}
