package com.diameter.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.diameter.commons.Application;
import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.DiameterDictionary;
import com.diameter.commons.DiameterNetworkConnector;
import com.diameter.commons.InitializationFailedException;
import com.diameter.commons.LogManager;
import com.diameter.commons.PeerData;
import com.diameter.commons.PeerDataImpl;
import com.diameter.commons.PeerGroupImpl;
import com.diameter.commons.PeerInfoImpl;
import com.diameter.commons.RoutingActions;
import com.diameter.commons.RoutingEntryDataImpl;
import com.diameter.commons.SCTPDiameterNetworkConnector;
import com.diameter.commons.ServiceTypes;
import com.diameter.commons.TransportProtocols;
import com.diameter.handler.GxDispatcher;
import com.diameter.handler.ServerGyCCRHandler;
import com.diameter.handler.ServerRoCCRHandler;
import com.diameter.handler.ServerRxAAHandler;
import com.diameter.model.PeerConfiguration;
import com.diameter.model.Vendor;
import com.diameter.service.AttributeService;
import com.diameter.service.PeerConfigurationService;
import com.diameter.serviceImpl.CustomerServiceImpl;
import com.diameter.serviceImpl.LocalCacheManagerServiceImpl;
import com.diameter.serviceImpl.MappingHeaderServiceImpl;
import com.diameter.serviceImpl.PlanServiceImpl;
import com.diameter.serviceImpl.QOSPolicyServiceImpl;
import com.diameter.stack.DiameterStack;
import com.diameter.util.GenericDiameterProcessor;


@Configuration
public class DiameterServerConfig {

	private static final String MODULE = "DIAMETER-CONFIG";
	
	@Autowired
	private CustomerServiceImpl customerServiceImpl; 
	
	@Autowired
	private PlanServiceImpl planServiceImpl;
	
	@Autowired
	private QOSPolicyServiceImpl qosPolicyServiceImpl;
	
	@Autowired
	private MappingHeaderServiceImpl mappingHeaderServiceImpl;
	
	@Autowired
	private PeerConfigurationService peerConfigurationService;
	
	@Autowired
	private AttributeService attributeService;
	
	@Autowired
	private LocalCacheManagerServiceImpl cacheManagerServiceImpl; 
	
	@Autowired
	private GenericDiameterProcessor genericDiameterProcessor;
	
	@Value("${info.app.realm}")
    private String realm;

    @Value("${info.app.identity}")
    private String identity;
    
    @Value("${info.app.stackMinThreadPoolSize}")
    private String stackMinThreadPoolSize;
    
    @Value("${info.app.stackMaxThreadPoolSize}")
    private String stackMaxThreadPoolSize;
    
    @Value("${info.app.stackThreadKeepAliveTime}")
    private String stackThreadKeepAliveTime;
    
    @Value("${info.app.stackMaxRequestQueueSize}")
    private String stackMaxRequestQueueSize;
    
    @Value("${info.app.diameter-server-configuration}")
    private String diameterServerConfiguration;
    
    @Value("${info.app.stackRequestTimeout}")
    private String stackRequestTimeout;
    
    @Value("${info.app.maxChunkSizeMb}")
    private String maxChunkSizeMb;
    
    @Autowired
    private DiameterStack diameterStack;

    @PostConstruct
	public void diameterServerStack() throws IOException, Exception {
		// Realm
		diameterStack.setDiameterStackRealm(realm);

		// Identity
		diameterStack.setDiameterStackIdentity(identity);

		// Stack URI
		diameterStack.setDiameterStackURI("aaa://0.0.0.0:3869");
		
		diameterStack.setMinThreadPoolSize(Integer.valueOf(stackMinThreadPoolSize));
		diameterStack.setMaxThreadPoolSize(Integer.valueOf(stackMaxThreadPoolSize));
		diameterStack.setThreadKeepAliveTime(Integer.valueOf(stackThreadKeepAliveTime));
		diameterStack.setMaxRequestQueueSize(Integer.valueOf(stackMaxRequestQueueSize));

		// Network Connector
		List<String> skipScopes = new ArrayList<>(Arrays.asList(diameterServerConfiguration.split(";")));
		
		if(skipScopes.contains("SCTP") || skipScopes.contains("BOTH")) {
			SCTPDiameterNetworkConnector sctpDiameterNetworkConnector= new SCTPDiameterNetworkConnector(diameterStack);
			sctpDiameterNetworkConnector.setNetworkAddress("0.0.0.0");
			sctpDiameterNetworkConnector.setNetworkPort(3869);
			diameterStack.addNetworkConnector(sctpDiameterNetworkConnector);
		}
		
		if(skipScopes.contains("TCP") || skipScopes.contains("BOTH")) {
			DiameterNetworkConnector diameterNetworkConnector = new DiameterNetworkConnector(diameterStack);
			diameterNetworkConnector.setNetworkAddress("0.0.0.0");
			diameterNetworkConnector.setNetworkPort(3868);
			diameterStack.addNetworkConnector(diameterNetworkConnector);
		}
		
		//Load Dictionary
		DiameterDictionary diameterDictionary =DiameterDictionary.getInstance();

		Resource resource = new ClassPathResource("dictionary/3GPP.xml");
		diameterDictionary.load(extractToTempFile(resource), null);

		resource = new ClassPathResource("dictionary/Base.xml");
		diameterDictionary.load(extractToTempFile(resource), null);

		resource = new ClassPathResource("dictionary/Custom.xml");
		diameterDictionary.load(extractToTempFile(resource), null);
		
		
		List<Vendor> attributeWithVendors= attributeService.getAllActiveAttributes("ACTIVE");
		if(attributeWithVendors !=null && !attributeWithVendors.isEmpty()) {
			diameterDictionary.loadFromDB(attributeWithVendors);
		}
		
		// Add Peer
		List<PeerData> peersDataList = new ArrayList<>();
		List<PeerGroupImpl> peerGroupsList = new ArrayList<>();
		List<PeerInfoImpl> peerInfoList = null;
		
		//fetch peers from database
		List<PeerConfiguration> dbPeers = peerConfigurationService.getPeerConfigByStatus(PeerConfiguration.Status.ACTIVE);
		for (PeerConfiguration peerConfig : dbPeers) {
			PeerData dbPeer = new PeerDataImpl();
			dbPeer.setPeerName(peerConfig.getNodeName());
			dbPeer.setHostIdentity(peerConfig.getFqdn());
			dbPeer.setRealmName(peerConfig.getRealm());
			dbPeer.setRemotePort(peerConfig.getRemotePort());
			dbPeer.setRemoteIPAddress(peerConfig.getRemoteIpAddress());
			dbPeer.setLocalIPAddress(peerConfig.getIpAddresses().get(0));
			dbPeer.setWatchdogInterval(peerConfig.getWatchdogInterval());
			dbPeer.setRequestTimeout(Long.valueOf(stackRequestTimeout));
			if(peerConfig.getTcpListenPort() !=null && peerConfig.getTcpListenPort() >= 0) {
				dbPeer.setLocalPort(peerConfig.getTcpListenPort());
				dbPeer.setTransportProtocol(TransportProtocols.TCP);
			}else {
				dbPeer.setLocalPort(peerConfig.getSctpListenPort());
				dbPeer.setTransportProtocol(TransportProtocols.SCTP);
			}

			PeerGroupImpl peerGroupImpl = new PeerGroupImpl();
			peerInfoList = new ArrayList<>();
			PeerInfoImpl peerInfoImpl= new PeerInfoImpl();
			peerInfoImpl.setPeerName(peerConfig.getNodeName());
			peerInfoList.add(peerInfoImpl);
			
			peerGroupImpl.setPeerInfoList(peerInfoList);
			peerGroupsList.add(peerGroupImpl);
			
			peersDataList.add(dbPeer);
		}
		
		diameterStack.registerPeers(peersDataList);
		
		//Gx Application Listener
		ApplicationEnum gxApplicationEnum = new ApplicationEnum() {
			public long getVendorId() {
				return 10415;
			}

			public ServiceTypes getApplicationType() {
				return ServiceTypes.BOTH;
			}

			public long getApplicationId() {
				return 16777238;
			}

			public Application getApplication() {
				return Application.TGPP_GX_29_212_18;
			}

			public String toString() {
				return getVendorId() + ":" +

						getApplicationId() + " [" + getApplication().getDisplayName() + "]";
			}
		};

		ApplicationEnum[] gxApplicationEnums = new ApplicationEnum[] { gxApplicationEnum };

		GxDispatcher serverGxCCRHandler = new GxDispatcher(diameterStack.getStackContext(), gxApplicationEnums);
		serverGxCCRHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
		serverGxCCRHandler.setCustomerServiceImpl(customerServiceImpl);
		serverGxCCRHandler.setPlanServiceImpl(planServiceImpl);
		serverGxCCRHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
		serverGxCCRHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
		serverGxCCRHandler.setGenericDiameterProcessor(genericDiameterProcessor);
		diameterStack.registerApplicationListener(serverGxCCRHandler);
				
		
		//Gy Application Listener
		ApplicationEnum applicationEnum = new ApplicationEnum() {
			public long getVendorId() {
				return 193;
			}

			public ServiceTypes getApplicationType() {
				return ServiceTypes.AUTH;
			}

			public long getApplicationId() {
				return 4;
			}

			public Application getApplication() {
				return Application.CC;
			}

			public String toString() {
				return getVendorId() + ":" +

						getApplicationId() + " [" + getApplication().getDisplayName() + "]";
			}
		};

		ApplicationEnum[] applicationEnums = new ApplicationEnum[] { applicationEnum };

		ServerGyCCRHandler serverCCRHandler = new ServerGyCCRHandler(diameterStack.getStackContext(), applicationEnums);
		serverCCRHandler.setCustomerServiceImpl(customerServiceImpl);
		serverCCRHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
		serverCCRHandler.setPlanServiceImpl(planServiceImpl);
		serverCCRHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
		serverCCRHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
		serverCCRHandler.setGenericDiameterProcessor(genericDiameterProcessor);
		serverCCRHandler.setMaxChunkSizeMb(Integer.valueOf(maxChunkSizeMb));
		diameterStack.registerApplicationListener(serverCCRHandler);
		
		// Ro Application Listener
		ApplicationEnum rxApplicationEnum = new ApplicationEnum() {

			public long getVendorId() {
				return 0;
			}

			public ServiceTypes getApplicationType() {
				return ServiceTypes.AUTH;
			}

			public long getApplicationId() {
				return 16777236;
			}

			public Application getApplication() {
				return Application.TGPP_GX_29_212_18;
			}

			public String toString() {
				return getVendorId() + ":" + getApplicationId() + " [" + getApplication().getDisplayName() + "]";
			}
		};

		ApplicationEnum[] rxApplicationEnums = new ApplicationEnum[] { rxApplicationEnum };

		ServerRxAAHandler serverRxAAHandler = new ServerRxAAHandler(diameterStack.getStackContext(),
				rxApplicationEnums);
		serverRxAAHandler.setCustomerServiceImpl(customerServiceImpl);
		serverRxAAHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
		serverRxAAHandler.setPlanServiceImpl(planServiceImpl);
		serverRxAAHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
		serverRxAAHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
		serverRxAAHandler.setGenericDiameterProcessor(genericDiameterProcessor);
		diameterStack.registerApplicationListener(serverRxAAHandler);
		
		// Ro Application Listener
		ApplicationEnum roApplicationEnum = new ApplicationEnum() {

		    public long getVendorId() {
		        return 0; 
		    }

		    public ServiceTypes getApplicationType() {
		        return ServiceTypes.AUTH;
		    }

		    public long getApplicationId() {
		        return 4;
		    }

		    public Application getApplication() {
		        return Application.CC;
		    }

		    public String toString() {
		        return getVendorId() + ":" +
		               getApplicationId() + " [" +
		               getApplication().getDisplayName() + "]";
		    }
		};
		
		ApplicationEnum[] roApplicationEnums = new ApplicationEnum[] { roApplicationEnum };

		ServerRoCCRHandler serverRoCCRHandler = new ServerRoCCRHandler(diameterStack.getStackContext(), roApplicationEnums);
		serverRoCCRHandler.setCustomerServiceImpl(customerServiceImpl);
		serverRoCCRHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
		serverRoCCRHandler.setPlanServiceImpl(planServiceImpl);
		serverRoCCRHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
		serverRoCCRHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
		serverRoCCRHandler.setGenericDiameterProcessor(genericDiameterProcessor);
		serverRoCCRHandler.setMaxChunkSizeMb(Integer.valueOf(maxChunkSizeMb));
		diameterStack.registerApplicationListener(serverRoCCRHandler);
		
		
		//
		RoutingEntryDataImpl realmData = new RoutingEntryDataImpl("*", RoutingActions.LOCAL, "0", null);
		realmData.setRoutingName("local");
		realmData.setPeerGroupList(peerGroupsList);
		diameterStack.registerRoutingEntry(realmData);
		
		LogManager.getLogger().info(MODULE, "Diameter Stack initialization started.");
		diameterStack.init();
		LogManager.getLogger().info(MODULE, "Diameter Stack initialization completed.");

		LogManager.getLogger().info(MODULE, "Starting Diameter Stack.");
		if (!diameterStack.start()) {
			throw new InitializationFailedException("Failed to start Diameter Stack");
		}
		LogManager.getLogger().info(MODULE, "Successfully Diameter Stack started.");
	}

	private File extractToTempFile(Resource resource) throws IOException {
	    File tempFile = File.createTempFile("dict_", "_" + resource.getFilename());
	    try (InputStream in = resource.getInputStream();
	         OutputStream out = new FileOutputStream(tempFile)) {
	        in.transferTo(out);
	    }
	    tempFile.deleteOnExit();
	    return tempFile;
	}
}
