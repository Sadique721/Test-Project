//package com.savbill.radius.controller;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.ResponseEntity;
//
//import com.savbill.radius.entity.ProxyServer;
//import com.savbill.radius.helper.ProxyServerDto;
//import com.savbill.radius.repository.ProxyServerRepository;
//import com.savbill.radius.utils.RadiusConstants;
//
////@RunWith(SpringRunner.class)
////@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
//public class ProxyServerTest {
//	
//	
//	
//	public ProxyServerTest() {
//		super();
//	}
//
//	@Autowired
//	private TestRestTemplate restTemplate;
//
//	@Autowired
//	private ProxyServerRepository proxyServerRepository;
//
//	@LocalServerPort
//	private int port;
//
//	private String getRootUrl() {
//		return "http://localhost:" + port;
//	}
//
//
//	public void testCreateServer() {
//		ProxyServerDto proxyServerDto = new ProxyServerDto();
//		proxyServerDto.setAcctport("9090");
//		proxyServerDto.setAuthport("9090");
//		proxyServerDto.setIp("172.24.16.104");
//		proxyServerDto.setName("Test-Server");
//		proxyServerDto.setSecretkey("1ASD6rtgJk");
//		proxyServerDto.setStatus(RadiusConstants.ACTIVE);
//		restTemplate =  restTemplate.withBasicAuth("admin", "admin123");
//		ResponseEntity<HashMap> postResponse = restTemplate.postForEntity(getRootUrl() + "/api/v1/proxyserver",
//				proxyServerDto, HashMap.class);
//
//		assertNotNull(postResponse);
//		assertNotNull(postResponse.getBody());
//		assertEquals("Test-Server", ((LinkedHashMap) postResponse.getBody().get("proxyServer")).get("name"));
//		assertEquals("1ASD6rtgJk", ((LinkedHashMap) postResponse.getBody().get("proxyServer")).get("secretkey"));
//	}
//
//	
//	public void testGetServer() {
//		ProxyServer proxyServer = new ProxyServer();
//		proxyServer.setAcctport("9090");
//		proxyServer.setAuthport("9090");
//		proxyServer.setIp("172.24.16.104");
//		proxyServer.setName("Test-Server-2");
//		proxyServer.setSecretkey("1ASD6rtgJk");
//		proxyServer.setStatus("A");
//		
//		proxyServer = proxyServerRepository.save(proxyServer);
//		restTemplate =  restTemplate.withBasicAuth("admin", "admin123");
//		ResponseEntity<HashMap> getResponse = restTemplate
//				.getForEntity(getRootUrl() + "/api/v1/proxyserver/" + proxyServer.getId(), HashMap.class);
//
//		assertNotNull(getResponse);
//		assertNotNull(getResponse.getBody());
//		assertEquals("Test-Server-2", ((LinkedHashMap) getResponse.getBody().get("proxyServer")).get("name"));
//		assertEquals("172.24.16.104", ((LinkedHashMap) getResponse.getBody().get("proxyServer")).get("ip"));
//	}
//
//	
//	public void testGetProfileByName() {
//		ProxyServer proxyServer = new ProxyServer();
//		proxyServer.setAcctport("9090");
//		proxyServer.setAuthport("9090");
//		proxyServer.setIp("172.24.16.104");
//		proxyServer.setName("Test-Server-3");
//		proxyServer.setSecretkey("1ASD6rtgJk");
//		proxyServer.setStatus("A");
//		
//		proxyServer = proxyServerRepository.save(proxyServer);
//		restTemplate =  restTemplate.withBasicAuth("admin", "admin123");
//		ResponseEntity<HashMap> getResponse = restTemplate.getForEntity(
//				getRootUrl() + "/api/v1/proxyserver/name/" + proxyServer.getName(), HashMap.class);
//
//		assertNotNull(getResponse);
//		assertNotNull(getResponse.getBody());
//		assertEquals("Test-Server-3", ((LinkedHashMap) getResponse.getBody().get("proxyServer")).get("name"));
//		assertEquals("172.24.16.104", ((LinkedHashMap) getResponse.getBody().get("proxyServer")).get("ip"));
//	}
//
//	
//	public void testDeleteProfile() {
//		ProxyServer proxyServer = new ProxyServer();
//		proxyServer.setAcctport("9090");
//		proxyServer.setAuthport("9090");
//		proxyServer.setIp("172.24.16.104");
//		proxyServer.setName("Test-Server-4");
//		proxyServer.setSecretkey("1ASD6rtgJk");
//		proxyServer.setStatus("A");
//		
//		proxyServer = proxyServerRepository.save(proxyServer);
//		restTemplate =  restTemplate.withBasicAuth("admin", "admin123");
//		restTemplate.delete(getRootUrl() + "/api/v1/proxyserver" + proxyServer.getId());
//
//		assertFalse(proxyServerRepository.findById(proxyServer.getId()).isPresent());
//	}
//}
