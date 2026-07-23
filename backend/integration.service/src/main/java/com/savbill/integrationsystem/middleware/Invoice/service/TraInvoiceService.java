package com.savbill.integrationsystem.middleware.Invoice.service;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.integrationMenu.ThirdPartyIntegrationMenuService;
import com.savbill.integrationsystem.integrationMenu.ThirdPartyIntigrationConstant;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail.*;
import com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail.*;
import com.savbill.integrationsystem.mvno.MvnoRepository;
import com.savbill.integrationsystem.utility.SendRestApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TraInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(TraInvoiceService.class);

    @Value("${qr.width}")
    private Integer qrWidth;

    @Value("${qr.height}")
    private Integer qrHeight;

    @Autowired
    private SendRestApiService sendRestApiService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private ThirdPartyIntegrationMenuService thirdPartyIntegrationMenuService;

    /**This will create pojo for tri invoice **/
    public TraInvoiceDTO createTrainvoiceDTO(SendInvoiceDTO sendInvoiceDTO){
        TraInvoiceDTO traInvoiceDTO = new TraInvoiceDTO();
        traInvoiceDTO.setInvoiceDate(convertDateToStringDate(sendInvoiceDTO.getDebitDocDate()));
        traInvoiceDTO.setInvoiceNumber(sendInvoiceDTO.getDebitDocNumber());
        traInvoiceDTO.setInvoiceTin(sendInvoiceDTO.getClientId());
        traInvoiceDTO.setCustomerName(sendInvoiceDTO.getFirstname()+" "+ sendInvoiceDTO.getLastname());
        traInvoiceDTO.setCustomerTin(sendInvoiceDTO.getCustomerTin());
        traInvoiceDTO.setCustomerPhone(sendInvoiceDTO.getPhoneNumber());
        traInvoiceDTO.setCustomerVrn(sendInvoiceDTO.getCustomerVrn());
        traInvoiceDTO.setPassportId(sendInvoiceDTO.getCustomerPassport());
        traInvoiceDTO.setDrivingLic(sendInvoiceDTO.getCustomerDrivingLicence());
        traInvoiceDTO.setCustomerNid(sendInvoiceDTO.getCustomerNid());
        traInvoiceDTO.setGrossAmount(String.valueOf((sendInvoiceDTO.getBasePrice()+sendInvoiceDTO.getTaxAmounts().get(0))));
        traInvoiceDTO.setVatAmount(String.valueOf(sendInvoiceDTO.getTaxAmounts().get(1)));
        traInvoiceDTO.setGrandTotal(sendInvoiceDTO.getTotalAmount().toString());
        return traInvoiceDTO;
    }

    /**This method will be send data to tra invoice and get data from tra**/
    public GenericDataDTO sendTraInvoiceRequest(TraInvoiceDTO traInvoiceDTO , String endpoint , String auth,Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Tra invoice request send start with DTO: " + traInvoiceDTO + " with endpoint " + endpoint);
            String qrResponse = "";
            ObjectMapper mapper = new ObjectMapper();
            String dto = mapper.writeValueAsString(traInvoiceDTO);
            String response = sendRestApiService.sendHttpPostRequest(endpoint, dto, auth,mvnoId);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("verify_url")) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                logger.info("Tra invoice api get Success Response with " + response + " going for a create qr code");
                /**Generate QR method is here **/
                qrResponse = generateQRCodeBase64(jsonNode.get("verify_url").toString());
                genericDataDTO.setData(qrResponse);
                genericDataDTO.setResponseMessage("Tra invoice qr generate successfully");

            } else {
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("Error occurred while sending the invoice to the TRA API. Please check the API response or verify the request format.");
                logger.info("Tra invoice api get Success Response with " + response + " going for a create qr code");

            }
        }
        catch(Exception e){
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("Something went wrong");
            e.printStackTrace();

        }
        return genericDataDTO;
    }

    public  String generateQRCodeBase64(String data) throws Exception {
        // Create a QR Code Writer
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = qrWidth; // Width of the QR code
        int height = qrHeight; // Height of the QR code

        // Generate the QR Code as a BitMatrix
        BitMatrix bitMatrix = qrCodeWriter.encode(data.replaceAll("^\"|\"$", ""), BarcodeFormat.QR_CODE, width, height);

        // Convert the BitMatrix to BufferedImage
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF); // Black & White
            }
        }

        // Convert BufferedImage to Base64 String
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", outputStream);
        byte[] qrBytes = outputStream.toByteArray();

        // Encode to Base64
        return Base64.getEncoder().encodeToString(qrBytes);
    }

    public String convertDateToStringDate(LocalDateTime date){
        LocalDateTime dateTime = LocalDateTime.parse(date.toString());

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");

        // Format the LocalDateTime
        String formattedDate = dateTime.format(formatter);

        return  formattedDate;
    }

    public void processInvoiceMessage(SendInvoiceMessage sendInvoiceMessage) throws Exception {
        if(sendInvoiceMessage != null && !sendInvoiceMessage.getSendInvoiceDTOList().isEmpty()){
            List<SendQRDTO> sendQRDTOList = new ArrayList<>();
             List<SendInvoiceDTO> sendInvoiceDTOList = sendInvoiceMessage.getSendInvoiceDTOList().stream().filter(sendInvoiceDTO -> sendInvoiceDTO.getTaxAmounts().size() == 2).collect(Collectors.toList());
             logger.info("final dto that will send :"+sendInvoiceDTOList.size());
             HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.TRA_Integration,sendInvoiceDTOList.get(0).getMvnoId());
             if(!sendInvoiceDTOList.isEmpty()) {
                 for (SendInvoiceDTO sendInvoiceDTO : sendInvoiceDTOList) {
                     TraInvoiceDTO traInvoiceDTO = createTrainvoiceDTO(sendInvoiceDTO);
                     SendQRDTO sendQRDTO = new SendQRDTO();
                     GenericDataDTO genericDataDTO = sendTraInvoiceRequest(traInvoiceDTO, intigrationParameters.get(ThirdPartyIntigrationConstant.TRA_Integration.TRA_API), intigrationParameters.get(ThirdPartyIntigrationConstant.TRA_Integration.TRA_AUTH), sendInvoiceDTO.getMvnoId());
                     sendQRDTO.setQr(genericDataDTO.getData().toString());
                     sendQRDTO.setDebitdocId(sendInvoiceDTO.getDebitdocId());
                     sendQRDTOList.add(sendQRDTO);
                 }
             }
              SendinvoiceQRMessage sendinvoiceQRMessage = new SendinvoiceQRMessage();
              sendinvoiceQRMessage.setSendQRDTOList(sendQRDTOList);
              kafkaMessageSender.send(new KafkaMessageData(sendinvoiceQRMessage, sendinvoiceQRMessage.getClass().getSimpleName(),"SEND_QR"));


        }
        else{
            logger.error("No invoice list is found");
        }
    }
}
