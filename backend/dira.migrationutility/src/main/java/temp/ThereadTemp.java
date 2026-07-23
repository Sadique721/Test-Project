//package temp;
//
//public class ThereadTemp {
//	package com.savbillt.radius.aaa.server;
//
//	import com.savbillt.radius.aaa.data.CustomerData;
//	import com.savbillt.radius.aaa.data.CustomerPlanData;
//	import com.savbillt.radius.aaa.db.DBAccountingDriver;
//	import com.savbillt.radius.aaa.db.DBAuthenticationDriver;
//	import com.savbillt.radius.aaa.db.DataSource;
//	import com.savbillt.radius.aaa.packet.AccountingRequest;
//	import com.savbillt.radius.aaa.packet.RadiusPacket;
//	import com.savbillt.radius.dto.SendQuotaDTO;
//	import com.savbillt.radius.entity.Client;
//	import com.savbillt.radius.helper.changeUserData;
//	import com.savbillt.radius.kafka.KafkaConstant;
//	import com.savbillt.radius.kafka.KafkaMessageData;
//	import com.savbillt.radius.kafka.KafkaMessageSender;
//	import com.savbillt.radius.kafka.MessageConstants;
//	import com.savbillt.radius.kafka.message.CustomerQuotaInfo;
//	import com.savbillt.radius.kafka.message.SendQuotaIntrimMsg;
//	import com.savbillt.radius.kafka.message.SendQuotaMsg;
//	import com.savbillt.radius.services.CustomerService;
//	import com.savbillt.radius.spring.SpringContext;
//	import com.google.common.util.concurrent.ThreadFactoryBuilder;
//	import com.google.gson.Gson;
//	import org.slf4j.Logger;
//	import org.slf4j.LoggerFactory;
//	import org.springframework.scheduling.annotation.Async;
//	import org.springframework.scheduling.annotation.EnableAsync;
//	import org.springframework.stereotype.Service;
//
//	import java.sql.Connection;
//	import java.sql.PreparedStatement;
//	import java.sql.SQLException;
//	import java.sql.Timestamp;
//	import java.util.Date;
//	import java.util.HashMap;
//	import java.util.List;
//	import java.util.concurrent.*;
//
//	@Service
//	@EnableAsync
//	public class RadiusAsyncUtility {
//
//		private static final Logger log = LoggerFactory.getLogger(RadiusAsyncUtility.class);
//		private static final String SQL_EXCEPTION = "SQLException";
//
//		private RadiusUtility radiusUtility = new RadiusUtility();
//
//		static Executor authProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("AUTHRESPONSE-%d").build());
//
//		static Executor acctProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("ACCTINSERT-%d").build());
//
//		static Executor cdrProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("CDRINSERT-%d").build());
//		static Executor queueMessageProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("MSGQUEUE-%d").build());
//		static Executor updateQuotaInfoProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("MSGQUEUE-%d").build());
//		static Executor coaDMExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("COADM-%d").build());
//
//		@Async
//		public void AuthenticateAudit(String username,String type,String reason,String clientip,String clientgroup,int mvnoid) {
//			DBAuthenticationDriver dbAuthDrive=new DBAuthenticationDriver();
//			HashMap hsAuthRes=new HashMap();
//			hsAuthRes.put("username", username);
//			hsAuthRes.put("replymessage", reason);
//			if(type.equalsIgnoreCase("2")) {
//				type="ACESS-ACCEPT";
//			} else {
//				type="ACESS-REJECT";
//			}
//			hsAuthRes.put("packettype", type);
//			hsAuthRes.put("clientip",clientip);
//			hsAuthRes.put("clientgroup",clientgroup);
//			authProcessExecutor.execute(new AuthProcessThread(hsAuthRes,mvnoid));
//		}
//
//		@Async
//		public void accountingProcess(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, Client client, Boolean addLiveSessionOnInterim) {
//			acctProcessExecutor.execute(new AcctProcessThread(mvnoid, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping,sourceAdd, custRetrunData,client,addLiveSessionOnInterim));
//		}
//
//		@Async
//		public void cdrProcess(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, double totalTimeMin, CustomerData custRetrunData) {
//			cdrProcessExecutor.execute(new CdrProcessThread(mvnoid, totalTimeMin, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping,sourceAdd, custRetrunData));
//		}
//
//		@Async
//		public void coaDMProcess(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation) {
//			coaDMExecutor.execute(new COADMProcessThread(userList,type,custRetrunData,operation));
//		}
//
//		@Async
//		public void reservedQuotaUpdateProcess(Double totalReservedQuota, boolean isChunkAvailable, Integer cprId, String queueName) {
//			queueMessageProcessExecutor.execute(new QueueMsgProcessThread(totalReservedQuota, isChunkAvailable, cprId, queueName));
//		}
//
//		@Async
//		public void custQuotaIntrimUpdateProcess(Integer cprid , Double currentSessionUsageTime ,Double currentSessionUsageVolume, String queueName) {
//			queueMessageProcessExecutor.execute(new QueueMsgProcessThread(cprid, currentSessionUsageTime, currentSessionUsageVolume, queueName));
//		}
//
//		@Async
//		public void custQuotaInfoUpdateProcess(CustomerQuotaInfo custQuotaInfo, String queueName) {
//			queueMessageProcessExecutor.execute(new QueueMsgProcessThread(custQuotaInfo, queueName));
//		}
//
//		@Async
//		public void CustQuotaDetailUpdateProcess(Integer cprId, Double percentagequotaConsumed , Double totalQuota , Double usedQuota, String queueName) {
//			queueMessageProcessExecutor.execute(new QueueMsgProcessThread(cprId,percentagequotaConsumed, totalQuota, usedQuota, queueName));
//		}
//
//		@Async
//		public void updateQuotaInfoProcess(CustomerQuotaInfo custQuotaInfo, CustomerData custRetrunData, double reservedQuota, String strUsedQuota, double usedTime, CustomerPlanData customerPlanData, DBAccountingDriver dbAcct, RadiusUtility radiusUtility) {
//			updateQuotaInfoProcessExecutor.execute(new UpdateQuotaInfoThread(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerPlanData, dbAcct, radiusUtility));
//		}
//
//	}
//
//	class QueueMsgProcessThread implements Runnable{
//		Double totalReservedQuota;
//		boolean isChunkAvailable;
//		Integer cprId;
//		String queueName;
//		CustomerQuotaInfo custQuotaInfo;
//		Double currentSessionUsageTime;
//		Double currentSessionUsageVolume;
//		Double percentagequotaConsumed;
//		Double totalQuota;
//
//		Double usedQuota;
//
//		public QueueMsgProcessThread(Integer cprId, Double percentagequotaConsumed, Double totalQuota, Double usedQuota, String queueName) {
//			this.cprId = cprId;
//			this.queueName = queueName;
//			this.percentagequotaConsumed = percentagequotaConsumed;
//			this.totalQuota = totalQuota;
//			this.usedQuota = usedQuota;
//		}
//
//		public QueueMsgProcessThread(CustomerQuotaInfo custQuotaInfo, String queueName) {
//			this.custQuotaInfo = custQuotaInfo;
//			this.queueName = queueName;
//		}
//
//		public QueueMsgProcessThread(Integer cprId, Double currentSessionUsageTime, Double currentSessionUsageVolume, String queueName) {
//			this.cprId = cprId;
//			this.currentSessionUsageTime = currentSessionUsageTime;
//			this.currentSessionUsageVolume = currentSessionUsageVolume;
//			this.queueName = queueName;
//		}
//
//		public QueueMsgProcessThread(Double totalReservedQuota, boolean isChunkAvailable, Integer cprId, String queueName) {
//			this.totalReservedQuota = totalReservedQuota;
//			this.isChunkAvailable = isChunkAvailable;
//			this.cprId = cprId;
//			this.queueName = queueName;
//		}
//
//		public void run() {
////			MessageSender messageSender = SpringContext.getBean(MessageSender.class);
//			KafkaMessageSender kafkaMessageSender = SpringContext.getBean(KafkaMessageSender.class);
//			SendQuotaDTO sendQuotaDTO = new SendQuotaDTO();
//			switch (queueName) {
//				case MessageConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS:
//					SendQuotaMsg sendQuotaMsg = new SendQuotaMsg(totalReservedQuota, isChunkAvailable, cprId);
//					//	messageSender.send(sendQuotaMsg , RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS);
//					kafkaMessageSender.send(new KafkaMessageData(sendQuotaMsg,sendQuotaMsg.getClass().getSimpleName(),KafkaConstant.CUSTOMERS_UPDATE_RESERVED_QUOTA));
//					break;
//				case MessageConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS:
//					sendQuotaDTO.setCprId(cprId);
//					sendQuotaDTO.setCurrentSessionUsageTime(currentSessionUsageTime);
//					sendQuotaDTO.setCurrentSessionUsageVolume(currentSessionUsageVolume);
//					SendQuotaIntrimMsg sendQuotaIntrimMsg = new SendQuotaIntrimMsg(sendQuotaDTO);
//					Gson gson = new Gson();
//					gson.toJson(sendQuotaIntrimMsg);
////					messageSender.send(sendQuotaIntrimMsg , RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS);
//					kafkaMessageSender.send(new KafkaMessageData(sendQuotaIntrimMsg,sendQuotaIntrimMsg.getClass().getSimpleName(),KafkaConstant.QUOTA_INTRIM));
//					break;
//				case MessageConstants.QUEUE_UPDATE_QUOTA:
//					//messageSender.send(custQuotaInfo,RabbitMqConstants.QUEUE_UPDATE_QUOTA);
//					kafkaMessageSender.send(new KafkaMessageData(custQuotaInfo,custQuotaInfo.getClass().getSimpleName()));
//					break;
//				case MessageConstants.QUEUE_SEND_QUOTA_FROM_RADIUS:
//					sendQuotaDTO.setCprId(cprId);
//					sendQuotaDTO.setPercentage(percentagequotaConsumed);
//					sendQuotaDTO.setTotalQuota(totalQuota);
//					sendQuotaDTO.setUsedQuota(usedQuota);
//					sendQuotaMsg = new SendQuotaMsg(sendQuotaDTO);
//					gson = new Gson();
//					gson.toJson(sendQuotaMsg);
//					//messageSender.send(sendQuotaMsg , RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS);
//					kafkaMessageSender.send(new KafkaMessageData(sendQuotaMsg,sendQuotaMsg.getClass().getSimpleName(), KafkaConstant.SEND_QUOTA));
//					break;
//				default:
//					break;
//			}
//		}
//	}
//
//	class CdrProcessThread implements Runnable{
//
//		int mvnoid=0;
//
//		Double totalTimeMin;
//		String acctStatusValue;
//		RadiusUtility radiusUtility;
//		AccountingRequest request;
//		RadiusPacket accoutningResponse;
//		ConcurrentMap dbFieldMapping;
//		String sourceAdd;
//		CustomerData custRetrunData;
//
//		public CdrProcessThread(int mvnoid, Double totalTimeMin, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData) {
//			this.mvnoid = mvnoid;
//			this.totalTimeMin = totalTimeMin;
//			this.acctStatusValue = acctStatusValue;
//			this.radiusUtility = radiusUtility;
//			this.request = request;
//			this.accoutningResponse = accoutningResponse;
//			this.dbFieldMapping = dbFieldMapping;
//			this.sourceAdd = sourceAdd;
//			this.custRetrunData = custRetrunData;
//		}
//
//		public void run() {
//			radiusUtility.processAcctPacketCDR(request, accoutningResponse, dbFieldMapping, mvnoid, sourceAdd, totalTimeMin,custRetrunData, acctStatusValue);
//		}
//	}
//
//	class COADMProcessThread implements Runnable{
//
//		private List<changeUserData> userList;
//		private String type;
//		private CustomerData custRetrunData;
//		private String operation;
//
//
//		public COADMProcessThread(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation) {
//			this.userList = userList;
//			this.type = type;
//			this.custRetrunData = custRetrunData;
//			this.operation = operation;
//		}
//
//		public void run() {
//			CustomerService customerService = SpringContext.getBean(CustomerService.class);
//			customerService.CoADMSupport(userList,type,custRetrunData,operation);
//		}
//	}
//
//	class AcctProcessThread implements Runnable{
//
//		int mvnoid=0;
//		String acctStatusValue;
//		RadiusUtility radiusUtility;
//		AccountingRequest request;
//		RadiusPacket accoutningResponse;
//		ConcurrentMap dbFieldMapping;
//		String sourceAdd;
//		CustomerData custRetrunData;
//
//		Client client;
//
//		Boolean addLiveSessionOnInterim;
//
//		public AcctProcessThread(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, Client client, Boolean addLiveSessionOnInterim) {
//			this.mvnoid = mvnoid;
//			this.acctStatusValue = acctStatusValue;
//			this.radiusUtility = radiusUtility;
//			this.request = request;
//			this.accoutningResponse = accoutningResponse;
//			this.dbFieldMapping = dbFieldMapping;
//			this.sourceAdd = sourceAdd;
//			this.custRetrunData = custRetrunData;
//			this.client = client;
//			this.addLiveSessionOnInterim = addLiveSessionOnInterim;
//		}
//
//		public void run() {
//			radiusUtility.processAcctPacketSession(request, accoutningResponse, dbFieldMapping, mvnoid, sourceAdd, custRetrunData, acctStatusValue,client,addLiveSessionOnInterim);
//		}
//	}
//
//	class AuthProcessThread implements Runnable{
//
//
//		HashMap<String,String> hsAuthRes=null;
//		int mvnoid=0;
//
//		AuthProcessThread(HashMap<String,String> hsAuthRes,int mvnoid){
//			this.hsAuthRes=hsAuthRes;
//			this.mvnoid=mvnoid;
//		}
//
//		public void run() {
//			Connection conn = null;
//			PreparedStatement  stmt = null;
//	        try {
//				Timestamp currentDate = (new Timestamp(new Date().getTime()));
//	            conn = DataSource.getConnection();
//	            stmt=conn.prepareStatement("insert into TBLMAUTHRESPONSE(username,replymessage,packettype,clientip,clientgroup,eventtime,mvnoid) values(?,?,?,?,?,?,?)");
//	            stmt.setString(1, hsAuthRes.get("username"));
//	            stmt.setString(2, hsAuthRes.get("replymessage"));
//	            stmt.setString(3, hsAuthRes.get("packettype"));
//	            stmt.setString(4, hsAuthRes.get("clientip"));
//	            stmt.setString(5, hsAuthRes.get("clientgroup"));
//	            stmt.setTimestamp(6, currentDate);
//	            stmt.setInt(7,mvnoid);
//	            stmt.executeUpdate();
//	        } 
//	        catch(SQLException e) {
//				e.printStackTrace();
//	        } 
//	        finally {
//	            try { if (stmt != null) stmt.close(); } catch(Exception e) {e.printStackTrace(); }
//	            try { if (conn != null) conn.close(); } catch(Exception e) {e.printStackTrace();}
//	        }
//
//		}
//	}
//
//	class UpdateQuotaInfoThread implements Runnable{
//		CustomerQuotaInfo custQuotaInfo; CustomerData custRetrunData;
//		double reservedQuota; String strUsedQuota; double usedTime;
//		CustomerPlanData customerPlanData; DBAccountingDriver dbAcct;
//		RadiusUtility radiusUtility;
//
//		public UpdateQuotaInfoThread(CustomerQuotaInfo custQuotaInfo, CustomerData custRetrunData, double reservedQuota, String strUsedQuota, double usedTime, CustomerPlanData customerPlanData, DBAccountingDriver dbAcct, RadiusUtility radiusUtility) {
//			this.custQuotaInfo = custQuotaInfo;
//			this.custRetrunData = custRetrunData;
//			this.reservedQuota = reservedQuota;
//			this.strUsedQuota = strUsedQuota;
//			this.usedTime = usedTime;
//			this.customerPlanData = customerPlanData;
//			this.dbAcct = dbAcct;
//			this.radiusUtility = radiusUtility;
//		}
//
//		@Override
//		public void run() {
//			AuthAcctUtilityServiceImpl authAcctUtilityImpl=new AuthAcctUtilityServiceImpl();
//			custQuotaInfo.setPlanType(customerPlanData.getPlanType());
//			custQuotaInfo.setPlanName(customerPlanData.getPlanName());
//			custQuotaInfo.setCustpackageid(customerPlanData.getCustpackageid());
//			String username = custRetrunData.getUsername();
//			try {
//				authAcctUtilityImpl.updateAcountingQuotaUseAsync(custQuotaInfo, username, strUsedQuota, String.valueOf(usedTime), customerPlanData.getCustpackageid(), dbAcct, custRetrunData.isFreeQuota(), radiusUtility);
////				log.debug("Quota Updated");
////				if (customerPlanData.getReservedQuotaInPer() != null && customerPlanData.isChunkAvailable() && customerPlanData.getTotalReservedQuota() > 0) {
////					double totalReservedQuota = customerPlanData.getTotalReservedQuota() - reservedQuota;
////					if (totalReservedQuota >= 0) {
////						// Delete quota from tblreservedquotadtls
////						dbAcct.updateReservedQuotaForChild(username, totalReservedQuota);
//////						log.debug("Reserved quota updated for customer: " + username + " available reserved quota: " + totalReservedQuota);
////						CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
////						customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(customerPlanData.getCustpackageid(), true, totalReservedQuota);
////						dbAcct.deleteReservedQuotaDtls(custRetrunData.getCustid());
////					}
////				}
//			} catch (Exception e) {
////				log.debug("Sync Quota with BSS Failed:" + e.getMessage());
//				e.printStackTrace();
//			}
//		}
//	}
//
//
//}
