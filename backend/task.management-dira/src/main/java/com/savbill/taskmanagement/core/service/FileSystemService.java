package com.savbill.taskmanagement.core.service;

import com.savbill.taskmanagement.core.constants.ClientServiceConstant;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.utils.CommonUtils;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class FileSystemService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    private String invoiceDir;

    private String trialInvoiceDir;

    private String partnerinvoiceDir;

    private String paymentDir;

    private Path custDocDir;

    private Path barterDocDir;

    private Path itemDocDir;

    private Path invoicePath;

    private Path trialInvoicePath;

    private Path partnerinvoicePath;

    private Path paymentPath;

    private Path custDocPath;

    private Path barterDocPath;
    
    private Path leadDocDir;
    
    private Path leadDocPath;
    private Path partnerDocDir;

    private Path partnerDocPath;

    private Path podocdir;

    private Path podocpath;

    private Path mailDocPath;

    private Path mailDocDir;

    @Autowired
    ClientServiceSrv clientServiceSrv;


    public Resource getInvoice(String docNo) {
        ApplicationLogger.logger.info("In getInvoice");
        Resource resource = null;
        try {
            if (invoiceDir == null || "".equals(invoiceDir)) {
                invoiceDir = CommonUtils.getBillPath();
                ApplicationLogger.logger.info("BILL_PATH:" + invoiceDir);
                invoicePath = Paths.get(invoiceDir).toAbsolutePath().normalize();
            }

            Path filePath = this.invoicePath.resolve(docNo).normalize();
            ApplicationLogger.logger.info("Invoice PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + docNo);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }

//    public Resource getPaymentReceipt(String paymentNo) {
//        ApplicationLogger.logger.info("In getPaymentReceipt");
//        Resource resource = null;
//        try {
//            if (paymentDir == null || "".equals(paymentDir)) {
//                paymentDir = CommonUtils.getPaymentPath();
//                ApplicationLogger.logger.info("BILL_PATH:" + paymentDir);
//                paymentPath = Paths.get(paymentDir).toAbsolutePath().normalize();
//            }
//
//            Path filePath = this.paymentPath.resolve(paymentNo).normalize();
//            ApplicationLogger.logger.info("payment PATH:" + filePath.toString());
//            resource = new UrlResource(filePath.toUri());
//            if (!resource.exists()) {
//                ApplicationLogger.logger.info("File not found " + paymentNo);
//            }
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            resource = null;
//        }
//        return resource;
//    }

    public Resource getCustDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        custDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = custDocDir + "/" + userName.trim() + "/";
            this.custDocPath = Paths.get(subFolderName);

            Path filePath = this.custDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }



    public Resource getBarterDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        barterDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = barterDocDir + userName.trim() + "/";
            this.barterDocPath = Paths.get(subFolderName);

            Path filePath = this.barterDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }

    public Resource getItemDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getItemDoc");
        itemDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.ITEM_COMPLAIN).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = itemDocDir + userName.trim() + "/";
            this.itemDocDir = Paths.get(subFolderName);

            Path filePath = this.itemDocDir.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }
    
    public Resource getLeadDoc(Long id, String file) {
        ApplicationLogger.logger.info("In getLeadDoc");
        leadDocDir = Paths.get(ClientServiceConstant.LEAD_DOC_PATH);
        //leadDocDir = Paths.get("E:\\Users\\savbill\\leaddoc\\");
        Resource resource = null;
        try {
            String subFolderName = leadDocDir + "/" + id + "/";
            this.leadDocPath = Paths.get(subFolderName);
            Path filePath = this.leadDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("LeadDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }

//    public Resource getPartnerInvoice(String docNo) {
//        ApplicationLogger.logger.info("In getPartnerInvoice");
//
//        Resource resource = null;
//        try {
//            if (partnerinvoiceDir == null || "".equals(partnerinvoiceDir)) {
//                partnerinvoiceDir = CommonUtils.getPartnerBillPath();
//                ApplicationLogger.logger.info("BILL_PATH:" + partnerinvoiceDir);
//                partnerinvoicePath = Paths.get(partnerinvoiceDir).toAbsolutePath().normalize();
//            }
//
//            Path filePath = this.partnerinvoicePath.resolve(docNo).normalize();
//            ApplicationLogger.logger.info("partner Invoice PATH:" + filePath.toString());
//            resource = new UrlResource(filePath.toUri());
//            if (!resource.exists()) {
//                ApplicationLogger.logger.info("File not found " + docNo);
//            }
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            resource = null;
//        }
//        return resource;
//    }

//    public Resource getTrialInvoice(String docNo) {
//        ApplicationLogger.logger.info("In getTrialInvoice");
//        Resource resource = null;
//        try {
//            if (trialInvoiceDir == null || "".equals(trialInvoiceDir)) {
//                trialInvoiceDir = CommonUtils.getTrialBillPath();
//                logger.info("TRIAL_BILL_PATH:" + trialInvoiceDir);
//                trialInvoicePath = Paths.get(trialInvoiceDir).toAbsolutePath().normalize();
//            }
//
//            Path filePath = this.trialInvoicePath.resolve(docNo).normalize();
//            ApplicationLogger.logger.info("Trial Invoice PATH:" + filePath.toString());
//            resource = new UrlResource(filePath.toUri());
//            if (!resource.exists()) {
//                ApplicationLogger.logger.info("File not found " + docNo);
//            }
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            resource = null;
//        }
//        return resource;
//    }

    public Resource getTicketDoc(String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        String custDocDir = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH_READ).get(0).getValue();
        List<String> paths = Arrays.asList(custDocDir.split(","));
        Path filePaths = null;
        Path docPath;
        Resource resource = null;
        try {
            for (String path : paths){
                logger.info("Ticket document Path is :" + path);
                docPath = Paths.get(path).toAbsolutePath().normalize();
                filePaths = docPath.resolve(file).normalize();
                logger.info("Ticket document Path is :" + filePaths.toString());
                resource = new UrlResource(filePaths.toUri());
                if (resource.exists()){
                    break;
                }
            }
            if (!resource.exists()) {
                logger.info("File not found " + file);
                return null;
            }
//            String subFolderName = String.valueOf(custDocDir);
//            this.custDocPath = Paths.get(subFolderName);
//            Path filePath = this.custDocPath.resolve(file).normalize();
//            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
//            resource = new UrlResource(filePath.toUri());
//            if (!resource.exists()) {
//                ApplicationLogger.logger.info("File not found " + file);
//            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }

    public Resource getPartnerDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        partnerDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.PARTNER_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = partnerDocDir + "/" + userName.trim() + "/";
            this.partnerDocPath = Paths.get(subFolderName);

            Path filePath = this.partnerDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("PartnerDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }


    public Resource getpurchaseOrderDoc(String poNumber, String uniquename) {
        ApplicationLogger.logger.info("In getpurchaseOrderDoc");
        podocdir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.ENTERPRISE_PO_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = podocdir + "/" + poNumber.trim() + "/";
            this.podocpath = Paths.get(subFolderName);

            Path filePath = this.podocpath.resolve(uniquename).normalize();
            ApplicationLogger.logger.info("POdoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + uniquename);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;

    }

    public Resource getMailDoc(String file) {
        ApplicationLogger.logger.info("In getMailDoc");
        mailDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAIL_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = String.valueOf(mailDocDir);
            this.mailDocPath = Paths.get(subFolderName);
            Path filePath = this.mailDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("Mail Doc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }
}
