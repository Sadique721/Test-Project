package com.savbill.integrationsystem.CRDB.Service;

import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBPaymentPostRequestDTO;
import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBVerificationRequestDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBPaymentPostResponseDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBVerificationResponseDTO;
import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;

import javax.servlet.http.HttpServletRequest;

public interface CRDBBillsPaymentService {

    /**
     * Handles the CRDB Verification Request.
     * <ol>
     *   <li>Validates JWT token and SHA1 checksum.</li>
     *   <li>Looks up customer by account number (paymentReference).</li>
     *   <li>Creates a pending {@code CustomerPayment} record (INITIATE status).</li>
     *   <li>Returns bill details to CRDB Bank.</li>
     * </ol>
     *
     * @param requestDTO inbound verification payload from CRDB Bank
     * @param request    raw HTTP request (used for audit logging)
     * @return verification response containing payer/bill details
     */
    CRDBVerificationResponseDTO verifyBillPayment(CRDBVerificationRequestDTO requestDTO,
                                                  HttpServletRequest request);

    /**
     * Handles the CRDB Payment Post Request.
     * <ol>
     *   <li>Validates JWT token and SHA1 checksum.</li>
     *   <li>Guards against duplicate {@code transactionRef}.</li>
     *   <li>Locates the matching pending payment record created during verification.</li>
     *   <li>Marks the payment as SUCCESSFUL and notifies downstream services.</li>
     *   <li>Returns a receipt to CRDB Bank.</li>
     * </ol>
     *
     * @param requestDTO inbound payment post payload from CRDB Bank
     * @param request    raw HTTP request (used for audit logging)
     * @return payment post response containing the institution receipt number
     */
    CRDBPaymentPostResponseDTO postPaymentNotification(CRDBPaymentPostRequestDTO requestDTO,
                                                       HttpServletRequest request);

    CreditDocumentData addInCreditDoc(com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment payment);
}
