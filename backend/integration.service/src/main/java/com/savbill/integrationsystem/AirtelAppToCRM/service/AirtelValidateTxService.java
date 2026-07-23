package com.savbill.integrationsystem.AirtelAppToCRM.service;


import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelValidateTxRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.TransactionEnquiryRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.AirtelValidateTxResponse;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.TransactionEnquiryResponse;

import javax.xml.bind.JAXBException;

public interface AirtelValidateTxService {

   void validateLookUpRequestData(AirtelValidateTxRequest request);

   void validateBillFetchRequestData(AirtelValidateTxRequest request);

   void validateC2BRequestData(AirtelValidateTxRequest request);

   void validateProcessTxRequestData(AirtelValidateTxRequest request);

   void validateTransactionRequestData(TransactionEnquiryRequest request);

   AirtelValidateTxResponse processB2CRequest(AirtelValidateTxRequest request, String token) throws JAXBException;

   AirtelValidateTxResponse validateC2BRequest(AirtelValidateTxRequest request, String token) throws JAXBException;

   AirtelValidateTxResponse processLOOKUPRequest(AirtelValidateTxRequest request, String token)  throws JAXBException;

   AirtelValidateTxResponse processBILLFETCHRequest(AirtelValidateTxRequest request) throws JAXBException;

   AirtelValidateTxResponse generateRespons(AirtelValidateTxResponse response) throws JAXBException;

   TransactionEnquiryResponse transactionrespons(TransactionEnquiryRequest request) throws JAXBException;

   TransactionEnquiryResponse generatetransactionRespons(TransactionEnquiryResponse response) throws JAXBException;
}
