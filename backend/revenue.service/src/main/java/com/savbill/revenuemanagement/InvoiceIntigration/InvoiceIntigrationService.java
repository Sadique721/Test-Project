package com.savbill.revenuemanagement.InvoiceIntigration;

import com.savbill.revenuemanagement.core.constants.IntigrationConstant;
import com.savbill.revenuemanagement.core.integrationMenu.ThirdPartyIntegrationMenuRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceIntigrationService {

      private static final org.apache.log4j.Logger logger = Logger.getLogger(InvoiceIntigrationService.class);

      @Autowired
      private DebitDocRepository debitDocRepository;

      @Autowired
      KafkaMessageSender kafkaMessageSender;

      @Autowired
      private EntityManager entityManager;

      @Value("${max.retries}")
      private String maxRetry;

      @Value("${max.retryDelay}")
      private String maxDelays;

      @Autowired
      private ThirdPartyIntegrationMenuRepository thirdPartyIntegrationMenuRepository;

      /**This is not a common method this will send data for tra invoice detail **/

      public void sendInvoiceDetailsToIntigration(List<Integer> debitDocIds){
            logger.info("debitdocIds with no qr list : "+debitDocIds);
            List<SendInvoiceDTO> sendInvoiceDTOList = debitDocRepository.findDebitDocumentsWithTaxesAsList(debitDocIds);
            sendInvoiceDTOList =  addInvoicePayment(sendInvoiceDTOList);
            if(!sendInvoiceDTOList.isEmpty()) {
                  logger.info("Invoice Data list is found going to send data in kafka for intigration");
                  SendInvoiceMessage sendInvoiceMessage = new SendInvoiceMessage();
                  sendInvoiceMessage.setSendInvoiceDTOList(sendInvoiceDTOList);
                  kafkaMessageSender.send(new KafkaMessageData(sendInvoiceMessage, SendInvoiceMessage.class.getSimpleName() , "TRA_INTEGRATION"));
            }
            else{
                  logger.error("No invoice data found");
            }
      }



      /**This method will be add payment to invoice**/
      public List<SendInvoiceDTO> addInvoicePayment(List<SendInvoiceDTO> sendInvoiceDTOList){
            Map<Integer, SendInvoiceDTO> documentMap = new HashMap<>();
            for (SendInvoiceDTO dto : sendInvoiceDTOList) {
                  documentMap.computeIfAbsent(dto.getDebitdocId(), k -> dto).addTaxAmount(dto.getTempTaxAmount());
            }
            List<SendInvoiceDTO> finalResults = new ArrayList<>(documentMap.values());
            return finalResults;
      }

      /**This Method will be set qr code given by tra set into debitdocument**/
      public void saveInvoiceQr(SendinvoiceQRMessage message){
            if(message != null && !message.getSendQRDTOList().isEmpty()){
                for(SendQRDTO sendQRDTO : message.getSendQRDTOList()){
                      debitDocRepository.updateDebitDocumentQr(sendQRDTO.getDebitdocId() , sendQRDTO.getQr());
//                      String updateQuery = "UPDATE tbltdebitdocument SET qr_code='" + sendQRDTO.getQr() + "' WHERE debitdocumentid=" + sendQRDTO.getDebitdocId();
//                      entityManager.createNativeQuery(updateQuery).executeUpdate();

                }
            }
            else{
                  logger.error("SendQRDTOLIST is empty");
            }
      }

      public void processIntegrationForMvno(Integer mvnoId , List<Integer> debitDocIds , String intigrationClient){
            switch (intigrationClient) {
                  case IntigrationConstant.IntigrationList.TRA_Integration:
                        /** Call TRA-specific API **/
                        logger.info("Calling TRA Integration API for MVNO: " + mvnoId + ", DebitDocIds: " + debitDocIds);
                        sendInvoiceDetailsToIntigration(debitDocIds);
                        break;
            }
      }

      public List<String> getIntegrationTypeForMvno(Integer mvnoId){
            List<String> intigrationList = thirdPartyIntegrationMenuRepository.findAllIntigrationByEventAndByMvnoId(IntigrationConstant.EventList.Invoice_Creation , mvnoId);
            logger.info("IntigrationList :"+intigrationList);
            return intigrationList;
      }

}
