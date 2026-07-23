package com.savbill.radius.aaa.server;

import com.savbill.radius.CronJobs.PostPaidPlanExpireryJob;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.db.DataSource;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.dto.SendQuotaDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.kafka.*;
import com.savbill.radius.kafka.*;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.kafka.message.SendQuotaIntrimMsg;
import com.savbill.radius.kafka.message.SendQuotaMsg;
import com.savbill.radius.rabbitmq.message.CustServiceActivationDateMessage;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.services.impl.CustPlanMappingServiceImpl;
import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.services.impl.CustomersServiceImpl;
import com.savbill.radius.services.impl.LiveUserServiceImpl;
import com.savbill.radius.spring.SpringContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.savbill.radius.utils.RadiusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Service
@EnableAsync
public class RadiusAsyncUtility {

//    @Autowired
//    private LiveUserService liveUserService;

    private static final Logger log = LoggerFactory.getLogger(RadiusAsyncUtility.class);
    private static final String SQL_EXCEPTION = "SQLException";

    private RadiusUtility radiusUtility = new RadiusUtility();

    static Executor authProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("AUTHRESPONSE-%d").build());

    static Executor acctProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("ACCTINSERT-%d").build());

    static Executor cdrProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("CDRINSERT-%d").build());
    static Executor queueMessageProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("MSGQUEUE-%d").build());
    static Executor updateQuotaInfoProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("MSGQUEUE-%d").build());
    static Executor coaDMExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("COADM-%d").build());

    static Executor removeLiveSessionExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("REMOVESESSION-%d").build());

    static Executor updateVlanExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("UPDATEVLAN-%d").build());

    static Executor updateFaultyMacExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("UPDATEFAULTYMAC-%d").build());

    static Executor updateCustomerMacExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("UPDATEFAULTYMAC-%d").build());

    static Executor saveCustomerPlanExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("CPR-%d").build());
    static Executor saveCustomerExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("CUST_CREATION-%d").build());

    static Executor RadiusPacketProcessExecutor = new ThreadPoolExecutor(200, 200, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("RADIUSPACKETINSERT-%d").build());

    static Executor COAResponseProcessExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("COARESPONSEINSERT-%d").build());



    @Async
    public void AuthenticateAudit(String username, String type, String reason, String clientip, String clientgroup, int mvnoid) {
        DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
        HashMap hsAuthRes = new HashMap();
        hsAuthRes.put("username", username);
        hsAuthRes.put("replymessage", reason);
        if (type.equalsIgnoreCase("2")) {
            type = "ACESS-ACCEPT";
        } else {
            type = "ACESS-REJECT";
        }
        hsAuthRes.put("packettype", type);
        hsAuthRes.put("clientip", clientip);
        hsAuthRes.put("clientgroup", clientgroup);
        authProcessExecutor.execute(new AuthProcessThread(hsAuthRes, mvnoid));
    }

    @Async
    public void accountingProcess(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, Client client, Boolean addLiveSessionOnInterim, double currentUsage, long currentTimeUsage, double upload, double download) {
        acctProcessExecutor.execute(new AcctProcessThread(mvnoid, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping, sourceAdd, custRetrunData, client, addLiveSessionOnInterim, currentUsage, currentTimeUsage, upload, download, false));
    }

    @Async
    public void cdrProcess(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, double totalTimeMin, CustomerData custRetrunData, double totalUsage, double upload, double download) {
        cdrProcessExecutor.execute(new CdrProcessThread(mvnoid, totalTimeMin, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping, sourceAdd, custRetrunData, totalUsage, upload, download));
    }

    @Async
    public void radiusPacketProcess(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, double totalTimeMin, CustomerData custRetrunData, double totalUsage, double upload, double download,Timestamp curentDate) {
        RadiusPacketProcessExecutor.execute(new RadiusPacketProcessThread(mvnoid, totalTimeMin, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping, sourceAdd, custRetrunData, totalUsage, upload, download, curentDate));
    }

    @Async
    public void coaRespnseProcess(String nasIpAddress, String coaPacket, String coaResponse, String reason, int mvnoId , RadiusPacket coaDMResponse) {
        RadiusUtility radiusUtility=new RadiusUtility();
        COAResponseProcessExecutor.execute(new COAResponseProcessThread(nasIpAddress, coaPacket, coaResponse, reason, mvnoId , coaDMResponse));
    }


    @Async
    public void coaDMProcess(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation) {
        coaDMExecutor.execute(new COADMProcessThread(userList, type, custRetrunData, operation));
    }

    @Async
    public void reservedQuotaUpdateProcess(Double totalReservedQuota, boolean isChunkAvailable, Integer cprId, String queueName) {
        queueMessageProcessExecutor.execute(new QueueMsgProcessThread(totalReservedQuota, isChunkAvailable, cprId, queueName));
    }

    @Async
    public void custQuotaIntrimUpdateProcess(Integer cprid, Double currentSessionUsageTime, Double currentSessionUsageVolume, String queueName) {
        queueMessageProcessExecutor.execute(new QueueMsgProcessThread(cprid, currentSessionUsageTime, currentSessionUsageVolume, queueName));
    }

    @Async
    public void custQuotaInfoUpdateProcess(CustomerQuotaInfo custQuotaInfo, String queueName) {
        queueMessageProcessExecutor.execute(new QueueMsgProcessThread(custQuotaInfo, queueName));
    }

    @Async
    public void CustQuotaDetailUpdateProcess(Integer cprId, Double percentagequotaConsumed, Double totalQuota, Double usedQuota, String queueName) {
        queueMessageProcessExecutor.execute(new QueueMsgProcessThread(cprId, percentagequotaConsumed, totalQuota, usedQuota, queueName));
    }

    @Async
    public void updateQuotaInfoProcess(CustomerQuotaInfo custQuotaInfo, CustomerData custRetrunData, double reservedQuota, String strUsedQuota, double usedTime, CustomerPlanData customerPlanData, DBAccountingDriver dbAcct, RadiusUtility radiusUtility) {
        updateQuotaInfoProcessExecutor.execute(new UpdateQuotaInfoThread(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerPlanData, dbAcct, radiusUtility));
    }

    @Async
    public void RemoveLiveSessionOnAccountingOn(List<Long> liveUserList, Integer mvnoId) {
        removeLiveSessionExecutor.execute(new RemoveLiveSessionOnAccountingOn(liveUserList, mvnoId));
    }

    @Async
    public void UpdateVlanManagement(VLANManagement vlanManagement) {
        updateVlanExecutor.execute(new UpdateVlanDetails(vlanManagement));
    }

    @Async
    public void UpdateFaultyMacData(FaultyMAC faultyMAC, LocalDateTime localDateTime) {
        updateFaultyMacExecutor.execute(new UpdateFaultyMacDetails(faultyMAC, localDateTime));
    }

    @Async
    public void UpdateCustomerMac(String newMac, String oldMac, CustomerData custReturnData, Integer mvnoId, boolean isUpdate) {
        updateCustomerMacExecutor.execute(new UpdateCustomerMacDetails(newMac, oldMac, custReturnData, mvnoId, isUpdate));
    }

    @Async
    public void saveCustomerPlan(KafkaMessageData message) {
        saveCustomerPlanExecutor.execute(new saveCustPlanMapping(message));
    }

    @Async
    public void saveCustomer(KafkaMessageData message) {
        saveCustomerExecutor.execute(new saveCustomer(message));
    }

}

class QueueMsgProcessThread implements Runnable {
    Double totalReservedQuota;
    boolean isChunkAvailable;
    Integer cprId;
    String queueName;
    CustomerQuotaInfo custQuotaInfo;
    Double currentSessionUsageTime;
    Double currentSessionUsageVolume;
    Double percentagequotaConsumed;
    Double totalQuota;

    Double usedQuota;

    public QueueMsgProcessThread(Integer cprId, Double percentagequotaConsumed, Double totalQuota, Double usedQuota, String queueName) {
        this.cprId = cprId;
        this.queueName = queueName;
        this.percentagequotaConsumed = percentagequotaConsumed;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
    }

    public QueueMsgProcessThread(CustomerQuotaInfo custQuotaInfo, String queueName) {
        this.custQuotaInfo = custQuotaInfo;
        this.queueName = queueName;
    }

    public QueueMsgProcessThread(Integer cprId, Double currentSessionUsageTime, Double currentSessionUsageVolume, String queueName) {
        this.cprId = cprId;
        this.currentSessionUsageTime = currentSessionUsageTime;
        this.currentSessionUsageVolume = currentSessionUsageVolume;
        this.queueName = queueName;
    }

    public QueueMsgProcessThread(Double totalReservedQuota, boolean isChunkAvailable, Integer cprId, String queueName) {
        this.totalReservedQuota = totalReservedQuota;
        this.isChunkAvailable = isChunkAvailable;
        this.cprId = cprId;
        this.queueName = queueName;
    }

    public void run() {
//		MessageSender messageSender = SpringContext.getBean(MessageSender.class);
        KafkaMessageSender kafkaMessageSender = SpringContext.getBean(KafkaMessageSender.class);
        SendQuotaDTO sendQuotaDTO = new SendQuotaDTO();
        switch (queueName) {
            case MessageConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS:
                SendQuotaMsg sendQuotaMsg = new SendQuotaMsg(totalReservedQuota, isChunkAvailable, cprId);
                //	messageSender.send(sendQuotaMsg , RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS);
                kafkaMessageSender.send(new KafkaMessageData(sendQuotaMsg, sendQuotaMsg.getClass().getSimpleName(), KafkaConstant.CUSTOMERS_UPDATE_RESERVED_QUOTA));
                break;
            case MessageConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS:
                sendQuotaDTO.setCprId(cprId);
                sendQuotaDTO.setCurrentSessionUsageTime(currentSessionUsageTime);
                sendQuotaDTO.setCurrentSessionUsageVolume(currentSessionUsageVolume);
                SendQuotaIntrimMsg sendQuotaIntrimMsg = new SendQuotaIntrimMsg(sendQuotaDTO);
                Gson gson = new Gson();
                gson.toJson(sendQuotaIntrimMsg);
//				messageSender.send(sendQuotaIntrimMsg , RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS);
                kafkaMessageSender.send(new KafkaMessageData(sendQuotaIntrimMsg, sendQuotaIntrimMsg.getClass().getSimpleName(), KafkaConstant.QUOTA_INTRIM));
                break;
            case MessageConstants.QUEUE_UPDATE_QUOTA:
                //messageSender.send(custQuotaInfo,RabbitMqConstants.QUEUE_UPDATE_QUOTA);
                kafkaMessageSender.send(new KafkaMessageData(custQuotaInfo, custQuotaInfo.getClass().getSimpleName()));
                break;
            case MessageConstants.QUEUE_SEND_QUOTA_FROM_RADIUS:
                sendQuotaDTO.setCprId(cprId);
                sendQuotaDTO.setPercentage(percentagequotaConsumed);
                sendQuotaDTO.setTotalQuota(totalQuota);
                sendQuotaDTO.setUsedQuota(usedQuota);
                sendQuotaMsg = new SendQuotaMsg(sendQuotaDTO);
                gson = new Gson();
                gson.toJson(sendQuotaMsg);
                //messageSender.send(sendQuotaMsg , RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS);
                kafkaMessageSender.send(new KafkaMessageData(sendQuotaMsg, sendQuotaMsg.getClass().getSimpleName(), KafkaConstant.SEND_QUOTA));
                break;
            default:
                break;
        }
    }
}

class CdrProcessThread implements Runnable {

    int mvnoid = 0;

    Double totalTimeMin;
    String acctStatusValue;
    RadiusUtility radiusUtility;
    AccountingRequest request;
    RadiusPacket accoutningResponse;
    ConcurrentMap dbFieldMapping;
    String sourceAdd;
    CustomerData custRetrunData;
    double totalUsage;
    double upload;
    double download;

    public CdrProcessThread(int mvnoid, Double totalTimeMin, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, double totalUsage, double upload, double download) {
        this.mvnoid = mvnoid;
        this.totalTimeMin = totalTimeMin;
        this.acctStatusValue = acctStatusValue;
        this.radiusUtility = radiusUtility;
        this.request = request;
        this.accoutningResponse = accoutningResponse;
        this.dbFieldMapping = dbFieldMapping;
        this.sourceAdd = sourceAdd;
        this.custRetrunData = custRetrunData;
        this.totalUsage = totalUsage;
        this.upload = upload;
        this.download = download;
    }

    public void run() {
        radiusUtility.processAcctPacketCDR(request, accoutningResponse, dbFieldMapping, mvnoid, sourceAdd, totalTimeMin, custRetrunData, acctStatusValue, totalUsage, upload, download);
    }
}

class COADMProcessThread implements Runnable {

    private List<changeUserData> userList;
    private String type;
    private CustomerData custRetrunData;
    private String operation;


    public COADMProcessThread(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation) {
        this.userList = userList;
        this.type = type;
        this.custRetrunData = custRetrunData;
        this.operation = operation;
    }

    public void run() {
        CustomerService customerService = SpringContext.getBean(CustomerService.class);
        customerService.CoADMSupport(userList, type, custRetrunData, operation);
    }
}

class AcctProcessThread implements Runnable {

    int mvnoid = 0;
    String acctStatusValue;
    RadiusUtility radiusUtility;
    AccountingRequest request;
    RadiusPacket accoutningResponse;
    ConcurrentMap dbFieldMapping;
    String sourceAdd;
    CustomerData custRetrunData;

    Client client;

    Boolean addLiveSessionOnInterim;
    double currentUsage;

    long currentTimeUsage;

    double upload;
    double download;

    Boolean isFaultyMac;

    public AcctProcessThread(int mvnoid, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, Client client, Boolean addLiveSessionOnInterim, double currentUsage, long currentTimeUsage, double upload, double download, boolean isFaultyMac) {
        this.mvnoid = mvnoid;
        this.acctStatusValue = acctStatusValue;
        this.radiusUtility = radiusUtility;
        this.request = request;
        this.accoutningResponse = accoutningResponse;
        this.dbFieldMapping = dbFieldMapping;
        this.sourceAdd = sourceAdd;
        this.custRetrunData = custRetrunData;
        this.client = client;
        this.addLiveSessionOnInterim = addLiveSessionOnInterim;
        this.currentUsage = currentUsage;
        this.currentTimeUsage = currentTimeUsage;
        this.upload = upload;
        this.download = download;
        this.isFaultyMac = isFaultyMac;
    }

    public void run() {
        radiusUtility.processAcctPacketSession(request, accoutningResponse, dbFieldMapping, mvnoid, sourceAdd, custRetrunData, acctStatusValue, client, addLiveSessionOnInterim, currentUsage, currentTimeUsage, upload, download, isFaultyMac);
    }
}

class AuthProcessThread implements Runnable {
    KafkaMessageSender kafkaMessageSender = SpringContext.getApplicationContext().getBean(KafkaMessageSender.class);
    CustomersRepository customerRepository = SpringContext.getApplicationContext().getBean(CustomersRepository.class);
    HashMap<String, String> hsAuthRes = null;
    int mvnoid = 0;

    AuthProcessThread(HashMap<String, String> hsAuthRes, int mvnoid) {
        this.hsAuthRes = hsAuthRes;
        this.mvnoid = mvnoid;
    }

    public void run() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Timestamp currentDate = (new Timestamp(new Date().getTime()));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement("insert into TBLMAUTHRESPONSE(username,replymessage,packettype,clientip,clientgroup,eventtime,mvnoid) values(?,?,?,?,?,?,?)");
            stmt.setString(1, hsAuthRes.get("username"));
            stmt.setString(2, hsAuthRes.get("replymessage"));
            stmt.setString(3, hsAuthRes.get("packettype"));
            stmt.setString(4, hsAuthRes.get("clientip"));
            stmt.setString(5, hsAuthRes.get("clientgroup"));
            stmt.setTimestamp(6, currentDate);
            stmt.setInt(7, mvnoid);
            stmt.executeUpdate();

            Optional<Customers> customer = customerRepository.findByUsernameAndMvnoId(hsAuthRes.get("username"), mvnoid);
            if (customer.isPresent() && null == customer.get().getServiceActivationDate()) {
                customer.get().setServiceActivationDate(currentDate);
                customerRepository.save(customer.get());
                Timestamp ts = customer.get().getServiceActivationDate();
                Long epochMillis = ts != null ? ts.getTime() : null;
                CustServiceActivationDateMessage custServiceActivationDateMessage= new CustServiceActivationDateMessage(customer.get().getId(),epochMillis);

                //Updating Service Activation date in CPM service
                kafkaMessageSender.send(new KafkaMessageData(custServiceActivationDateMessage, custServiceActivationDateMessage.getClass().getSimpleName(), "SERVICE_ACTIVATION_DATE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

class UpdateQuotaInfoThread implements Runnable {
    CustomerQuotaInfo custQuotaInfo;
    CustomerData custRetrunData;
    double reservedQuota;
    String strUsedQuota;
    double usedTime;
    CustomerPlanData customerPlanData;
    DBAccountingDriver dbAcct;
    RadiusUtility radiusUtility;

    public UpdateQuotaInfoThread(CustomerQuotaInfo custQuotaInfo, CustomerData custRetrunData, double reservedQuota, String strUsedQuota, double usedTime, CustomerPlanData customerPlanData, DBAccountingDriver dbAcct, RadiusUtility radiusUtility) {
        this.custQuotaInfo = custQuotaInfo;
        this.custRetrunData = custRetrunData;
        this.reservedQuota = reservedQuota;
        this.strUsedQuota = strUsedQuota;
        this.usedTime = usedTime;
        this.customerPlanData = customerPlanData;
        this.dbAcct = dbAcct;
        this.radiusUtility = radiusUtility;
    }

    @Override
    public void run() {
        AuthAcctUtilityServiceImpl authAcctUtilityImpl = new AuthAcctUtilityServiceImpl();
        custQuotaInfo.setPlanType(customerPlanData.getPlanType());
        custQuotaInfo.setPlanName(customerPlanData.getPlanName());
        custQuotaInfo.setCustpackageid(customerPlanData.getCustpackageid());
        String username = custRetrunData.getUsername();
        try {
            authAcctUtilityImpl.updateAcountingQuotaUseAsync(custQuotaInfo, username, strUsedQuota, String.valueOf(usedTime), customerPlanData.getCustpackageid(), dbAcct, custRetrunData.isFreeQuota(), radiusUtility);
//			log.debug("Quota Updated");
//			if (customerPlanData.getReservedQuotaInPer() != null && customerPlanData.isChunkAvailable() && customerPlanData.getTotalReservedQuota() > 0) {
//				double totalReservedQuota = customerPlanData.getTotalReservedQuota() - reservedQuota;
//				if (totalReservedQuota >= 0) {
//					// Delete quota from tblreservedquotadtls
//					dbAcct.updateReservedQuotaForChild(username, totalReservedQuota);
////					log.debug("Reserved quota updated for customer: " + username + " available reserved quota: " + totalReservedQuota);
//					CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
//					customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(customerPlanData.getCustpackageid(), true, totalReservedQuota);
//					dbAcct.deleteReservedQuotaDtls(custRetrunData.getCustid());
//				}
//			}
        } catch (Exception e) {
//			log.debug("Sync Quota with BSS Failed:" + e.getMessage());
            e.printStackTrace();
        }
    }
}

class RadiusPacketProcessThread implements Runnable {

    int mvnoid = 0;

    Double totalTimeMin;
    String acctStatusValue;
    RadiusUtility radiusUtility;
    AccountingRequest request;
    RadiusPacket accoutningResponse;
    ConcurrentMap dbFieldMapping;
    String sourceAdd;
    CustomerData custRetrunData;
    double totalUsage;
    double upload;
    double download;
    Timestamp curentDate;

    public RadiusPacketProcessThread(int mvnoid, Double totalTimeMin, String acctStatusValue, RadiusUtility radiusUtility, AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping, String sourceAdd, CustomerData custRetrunData, double totalUsage, double upload, double download, Timestamp curentDate) {
        this.mvnoid = mvnoid;
        this.totalTimeMin = totalTimeMin;
        this.acctStatusValue = acctStatusValue;
        this.radiusUtility = radiusUtility;
        this.request = request;
        this.accoutningResponse = accoutningResponse;
        this.dbFieldMapping = dbFieldMapping;
        this.sourceAdd = sourceAdd;
        this.custRetrunData = custRetrunData;
        this.totalUsage = totalUsage;
        this.upload = upload;
        this.download = download;
        this.curentDate = curentDate;
    }

    public void run() {
        radiusUtility.processRadiusPacketCDR(request, accoutningResponse, dbFieldMapping, mvnoid, sourceAdd, totalTimeMin, custRetrunData, acctStatusValue, totalUsage, upload, download, curentDate);
    }
}

class COAResponseProcessThread implements Runnable {

    String nasIpAddress;
    String coaPacket;
    String coaResponse;
    String reason;
    int mvnoId;
    RadiusPacket coaDMResponse;

    public COAResponseProcessThread(String nasIpAddress, String coaPacket, String coaResponse, String reason, int mvnoId , RadiusPacket coaDMResponse) {
        this.nasIpAddress = nasIpAddress;
        this.coaPacket = coaPacket;
        this.coaResponse = coaResponse;
        this.reason = reason;
        this.mvnoId = mvnoId;
        this.coaDMResponse = coaDMResponse;
    }

    public void run() {
        String sqlInsertToCOAResponse = "INSERT INTO tblmcoaresponse (nasipaddress, coapacket, coaresponse, reason, createdate, mvnoid,coaresponsemessage) VALUES (?, ?, ?, ?, ?, ?,?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(sqlInsertToCOAResponse);
            stmt.setString(1, nasIpAddress);
            stmt.setString(2, coaPacket);
            stmt.setString(3, reason);
            stmt.setString(4, coaResponse);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, mvnoId);
            stmt.setString(7, coaDMResponse != null ? coaDMResponse.toString() : "");
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
            }
        }
    }
}



class RemoveLiveSessionOnAccountingOn implements Runnable {
    LiveUserServiceImpl liverUserService = SpringContext.getApplicationContext().getBean(LiveUserServiceImpl.class);
    //    private LiveUserService liverUserService;
    private List<Long> liveUserList;
    private Integer mvnoId;

    public RemoveLiveSessionOnAccountingOn(List<Long> liveUserList, Integer mvnoId) {
        this.liveUserList = liveUserList;
        this.mvnoId = mvnoId;
    }

    @Override
    public void run() {
        try {
            List<LiveUser> liveUsers = liverUserService.findLiveUserByCdrId(liveUserList);
            liverUserService.disconnectLiveUsersOfStaleSession(liveUsers, mvnoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class UpdateFaultyMacDetails implements Runnable {
    private FaultyMAC faultyMAC;
    private LocalDateTime localDateTime;

    DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();

    public UpdateFaultyMacDetails(FaultyMAC faultyMAC, LocalDateTime localDateTime) {
        this.faultyMAC = faultyMAC;
        this.localDateTime = localDateTime;
    }

    @Override
    public void run() {
        try {
            dbAuth.updateLastConnectedInFaultyMac(faultyMAC.getId(), faultyMAC.getMackId(), LocalDateTime.now().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class UpdateCustomerMacDetails implements Runnable {
    String newMac;
    String oldMac;
    CustomerData custReturnData;
    Integer mvnoId;
    DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
    boolean isUpdate;

    public UpdateCustomerMacDetails(String newMac, String oldMac, CustomerData custReturnData, Integer mvnoId, boolean isUpdate) {
        this.newMac = newMac;
        this.oldMac = oldMac;
        this.custReturnData = custReturnData;
        this.mvnoId = mvnoId;
        this.isUpdate = isUpdate;
    }

    @Override
    public void run() {
        try {
            RadiusUtility utility = new RadiusUtility();
            utility.saveOrUpdateCustomerMac(newMac, oldMac, custReturnData, mvnoId, dbAuthDrive, isUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class UpdateVlanDetails implements Runnable {

    VLANManagement vlanManagement;

    public UpdateVlanDetails(VLANManagement vlanManagement) {
        this.vlanManagement = vlanManagement;
    }

    @Override
    public void run() {
        try {
            DBAccountingDriver dbAccountingDriver = new DBAccountingDriver();
            dbAccountingDriver.updatevlanManagement(vlanManagement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class saveCustPlanMapping implements Runnable {
    private static final Logger log = LoggerFactory.getLogger("savbillradiusqueue");
    CustPlanMappingServiceImpl custPlanMappingService = SpringContext.getApplicationContext().getBean(CustPlanMappingServiceImpl.class);
    CustomerServiceImpl customerServiceImpl = SpringContext.getApplicationContext().getBean(CustomerServiceImpl.class);
    CustomersRepository customerRepository = SpringContext.getApplicationContext().getBean(CustomersRepository.class);
    PostPaidPlanExpireryJob postPaidPlanExpireryJob = SpringContext.getApplicationContext().getBean(PostPaidPlanExpireryJob.class);
    private KafkaMessageData message;

    public saveCustPlanMapping(KafkaMessageData message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Handled CustomMessage for CUSTOMER PLAN MAPPING SAVE successfully: " + message);
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            Map data = dataMessage.getData();

            /**
             Before Activating New-plan
             STOP Existing Base-Plan
             */
            if(data.get("CHANG-PLAN")!=null) {
                Map map = (Map) data.get("CHANG-PLAN");
                try {
                    custPlanMappingService.Update01(map, "CHANGE-PLAN");
                }catch (Exception e){
                    log.error("[Update01()] Error handling CustomMessage for CUSTOMER PLAN MAPPING SAVE: " + e.getMessage(), e);
                }
            }

            CustPlanMappping custPlanMappping = custPlanMappingService.save(dataMessage, dataMessage.getOperation());
            Customers customers = customerServiceImpl.findCustomerById(custPlanMappping.getCustid());
            if (customers != null && data.get("nextQuotaResetDate") != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate nextQuotaResetDate = LocalDate.parse(data.get("nextQuotaResetDate").toString(), formatter);
                customers.setNextQuotaResetDate(nextQuotaResetDate);
                customerRepository.save(customers);
            } else if (customers != null) {
                postPaidPlanExpireryJob.updateCustomerNextQuotaDate(customers, custPlanMappping);
            }
            long endTime = System.currentTimeMillis();
            log.warn("Time Taken for Customer Plan creation: " + (endTime - startTime) + " customers: " + custPlanMappping.getCustid());

        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER PLAN MAPPING SAVE: " + e.getMessage(), e);
        }
    }
}

class saveCustomer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger("savbillradiusqueue");
    CustPlanMappingServiceImpl custPlanMappingService = SpringContext.getApplicationContext().getBean(CustPlanMappingServiceImpl.class);
    CustomerServiceImpl customerServiceImpl = SpringContext.getApplicationContext().getBean(CustomerServiceImpl.class);
    PostPaidPlanExpireryJob postPaidPlanExpireryJob = SpringContext.getApplicationContext().getBean(PostPaidPlanExpireryJob.class);

    private CustomersServiceImpl customersService = SpringContext.getApplicationContext().getBean(CustomersServiceImpl.class);
    private KafkaMessageData message;

    public saveCustomer(KafkaMessageData message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Handled CustomMessage for CUSTOMER CREATE successfully: " + message);
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            customersService.customMessage(dataMessage);
            long endTime = System.currentTimeMillis();
            log.warn("Time Taken for Customer creation: " + (endTime - startTime));
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER CREATE: " + e.getMessage(), e);
        }
    }
}
