package com.savbill.integrationsystem.Mpesa.Service;

import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaBrokerRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaC2BRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.TransactionStatusRequestDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaBrokerResponseDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaC2BValidateResponseDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaQrResponseDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public interface MpesaBrokerService {

    void validateProcessTxRequestData(MpesaBrokerRequestDTO request);
    MpesaBrokerResponseDTO processB2CRequest(MpesaBrokerRequestDTO request, String token) throws JAXBException;
    MpesaBrokerResponseDTO generateResponse(MpesaBrokerResponseDTO response) throws JAXBException;
    MpesaC2BValidateResponseDTO validateC2BRequest(MpesaC2BRequestDTO requestDTO, HttpServletRequest request);

    MpesaC2BValidateResponseDTO handleC2BConfirmation(MpesaC2BRequestDTO requestDTO, String token, HttpServletRequest request);
    GenericDataDTO initiateB2CMpesaPayment(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request);
    void validateRequestForInitiatePayment(CustomerPaymentDTO paymentDTO);
    GenericDataDTO checkTransactionStatusResponse(TransactionStatusRequestDTO transactionStatusRequestDTO);

    GenericDataDTO initiateC2BMpesaExpressSimulate(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request);
    MpesaQrResponseDTO initiateQRCodePayment(CustomerPaymentDTO customerPaymentDTO,HttpServletRequest request);
}
