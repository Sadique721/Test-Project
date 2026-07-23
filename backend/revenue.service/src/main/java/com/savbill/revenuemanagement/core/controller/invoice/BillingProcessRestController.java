package com.savbill.revenuemanagement.core.controller.invoice;


import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.MenuConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.ResponseObject;
import com.savbill.revenuemanagement.core.dto.invoice.BulkInvoiceDownloadRequest;
import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.PartnerDebitDocument;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.repository.partner.PartnerDebitDocRepository;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceCharges;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PlanGroupMappingChargeRelRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupMappingRepository;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

@Api(value = "SavbillBillingEngine Prepaid Management", description = "REST APIs related to PrepaidBilling Entity!!!!", tags = "PrepaidBilling")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
@CrossOrigin
public class BillingProcessRestController {

    private static final Logger logger = LoggerFactory.getLogger(BillingProcessRestController.class);
	@Autowired
	private DebitDocRepository debitDocRepository;
	@Autowired
	private PdfUtil pdfUtil;
	@Autowired
	PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;
	@Autowired
	PlanGroupMappingRepository planGroupMappingRepository;
	@Autowired
	PostpaidPlanRepo postpaidPlanRepo;
	@Autowired
	PostpaidPlanChargeRepo postpaidPlanChargeRepo;
	@Autowired
	ChargeRepository chargeRepository;
//	@Autowired
//	MessageSender messageSender;
	@Autowired
	TaxService taxService;
	@Autowired
	TrialDebitDocRepository trialDebitDocRepository;
	@Autowired
	CreditDocRepository creditDocRepository;
	@Autowired
	private PartnerDebitDocRepository partnerDebitDocRepository;
	@Autowired
	KafkaMessageSender kafkaMessageSender;

	@PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_invoice_master +  "\" ,\"" + MenuConstants.pre_cust_invoices  + "\",\"" + MenuConstants.post_cust_invoices_generate+ "\",\"" + MenuConstants.pre_cust_invoices_generate+ "\")")
	@RequestMapping(value = "/generatePdfByInvoiceId/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject generatePdfByInvoiceNumber(@PathVariable("debitDocId") Integer debitDocId, HttpServletRequest request) {
		logger.info("[" + this.getClass().getName() + "] generatePdfByInvoiceNumber() method started. invoiceId :" + debitDocId);
		ResponseObject response = new ResponseObject();
		try {
//			LoggedInUser user=authorizationService.getLoggedInUser(request);
			Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitDocId);
			if(debitDocument.isPresent()) {
				if(debitDocument.get().getDocument() == null || debitDocument.get().getDocument().isEmpty()) {
					PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
					prepaidInvoiceService.setInvoiceXml(debitDocument.get());
					debitDocRepository.save(debitDocument.get());
				}
				boolean pdfGenerationFlag=pdfUtil.generatePDF(debitDocument.get(),false);
				if (pdfGenerationFlag) {
					debitDocument.get().setBillrunstatus(Constants.INVOICE_STATUS.EXPORTED.status());
					debitDocRepository.save(debitDocument.get());
					PrepaidInvoiceCharges prepaidInvoiceCharges=new PrepaidInvoiceCharges(debitDocument.get().getCustomer().getId(),debitDocument.get().getCustomer().getUsername(),null,debitDocument.get().getTotalamount(),debitDocument.get().getId().longValue(),null,false,debitDocument.get().getTotalamount(),null,null,null,"null","false",null,0L,debitDocument.get(),debitDocument.get().getCustomer().getWalletbalance(),debitDocument.get().getPaymentStatus(),debitDocument.get().getBillrunid(),null,null,debitDocument.get().getAdjustedAmount(),debitDocument.get().getBillrunstatus(),true,  debitDocument.get().getIsDirectChargeInvoice(),null,null,null,null);
//					messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
					kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges,PrepaidInvoiceCharges.class.getSimpleName()));

				}
			} else {
				throw new RuntimeException("Invalid debit doc id: "+debitDocId);
			}

			logger.info("[" + this.getClass().getName() + "] generatePdfByInvoiceNumber() method completed.");
			response.setResponseCode(String.valueOf(HttpStatus.OK.value()));
			response.setResponseMessage("Invoice PDF generation done. Please Check Current Status of BillRun.");
			response.setResponseObject(null);
		} catch (Exception e) {
			logger.error("Error while export invoice pdf: "+e.getMessage());
			e.printStackTrace();
			response.setResponseCode(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()));
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}


	//@PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_invoice_master +  "\" ,\"" + MenuConstants.pre_cust_invoices  + "\",\"" + MenuConstants.post_cust_invoices_generate+ "\",\"" + MenuConstants.pre_cust_invoices_generate+ "\")")
	@RequestMapping(value = "/generatePdfByPartnerInvoiceId/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject generatePdfByPartnerInvoiceNumber(@PathVariable("debitDocId") Integer debitDocId, HttpServletRequest request) {
		logger.info("[" + this.getClass().getName() + "] generatePdfByInvoiceNumber() method started. invoiceId :" + debitDocId);
		ResponseObject response = new ResponseObject();
		try {
			Optional<PartnerDebitDocument> debitDocument = partnerDebitDocRepository.findById(debitDocId);
			if(debitDocument.isPresent()) {
				boolean pdfGenerationFlag=pdfUtil.generatePartnerPDF(debitDocument.get());
				if (pdfGenerationFlag) {
					debitDocument.get().setBillrunstatus(Constants.INVOICE_STATUS.EXPORTED.status());
					partnerDebitDocRepository.save(debitDocument.get());
				}
			} else {
				throw new RuntimeException("Invalid debit doc id: "+debitDocId);
			}

			logger.info("[" + this.getClass().getName() + "] generatePdfByInvoiceNumber() method completed.");
			response.setResponseCode(HttpStatus.OK.toString());
			response.setResponseMessage("Invoice PDF generation done. Please Check Current Status of BillRun.");
			response.setResponseObject(null);
		} catch (Exception e) {
			logger.error("Error while export invoice pdf: "+e.getMessage());
			e.printStackTrace();
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}

	@PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_invoice_master +  "\" ,\"" + MenuConstants.pre_cust_invoices  + "\",\"" + MenuConstants.post_cust_invoices_generate+ "\",\"" + MenuConstants.pre_cust_invoices_generate+ "\")")
	@RequestMapping(value = "/generateTrialPdfByInvoiceId/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject generateTrialPdfByInvoiceNumber(@PathVariable("debitDocId") Integer debitDocId, HttpServletRequest request) {
		logger.info("[" + this.getClass().getName() + "] generatePdfByInvoiceNumber() method started. invoiceId :" + debitDocId);
		ResponseObject response = new ResponseObject();
		try {
//			LoggedInUser user=authorizationService.getLoggedInUser(request);
			Optional<TrialDebitDocument> trialDebitDocument = trialDebitDocRepository.findById(debitDocId);
			if(trialDebitDocument.isPresent()) {
				if(trialDebitDocument.get().getDocument() == null || trialDebitDocument.get().getDocnumber().isEmpty() || StringUtils.isBlank(trialDebitDocument.get().getDocument())) {
					PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
					String xml = prepaidInvoiceService.setInvoiceXml(trialDebitDocument.get(),trialDebitDocument.get().getTrialDebitDocumentDetails());
					trialDebitDocument.get().setDocument(xml);
					trialDebitDocRepository.save(trialDebitDocument.get());
				}
				boolean pdfGenerationFlag=pdfUtil.generateTrialPDF(trialDebitDocument.get(),false);
				if (pdfGenerationFlag) {
					trialDebitDocument.get().setBillrunstatus(Constants.INVOICE_STATUS.EXPORTED.status());
					trialDebitDocRepository.save(trialDebitDocument.get());
//					PrepaidInvoiceCharges prepaidInvoiceCharges=new PrepaidInvoiceCharges(trialDebitDocument.get().getCustomer().getId(),trialDebitDocument.get().getCustomer().getUsername(),null,trialDebitDocument.get().getTotalamount(),trialDebitDocument.get().getId().longValue(),null,false,trialDebitDocument.get().getTotalamount(),null,null,null,"null","false",null,0L,trialDebitDocument.get(),trialDebitDocument.get().getCustomer().getWalletbalance(),trialDebitDocument.get().getPaymentStatus(),trialDebitDocument.get().getBillrunid(),null,null,trialDebitDocument.get().getAdjustedAmount(),trialDebitDocument.get().getBillrunstatus(),true,  null,null,null,null);
//					messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);

				}
			} else {
				throw new RuntimeException("Invalid Invoice id: "+debitDocId);
			}

			logger.info("[" + this.getClass().getName() + "] generateTrialPdfByInvoiceNumber() method completed.");
			response.setResponseCode(HttpStatus.OK.toString());
			response.setResponseMessage("Invoice PDF generation done. Please Check Current Status of BillRun.");
			response.setResponseObject(null);
		} catch (Exception e) {
			logger.error("Error while export invoice pdf: "+e.getMessage());
			e.printStackTrace();
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}

	@RequestMapping(value = "/invoicePdf/download/{debitDocId}", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity download(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse) {
		Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			throw new RuntimeException("Invoice not found for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "]: ****** REST BillingProcessRestController() method called. invoicenumber :" + debitDocument.get().getDocnumber()+" ****** ");
		return pdfUtil.downloadInvoicePdf(debitDocument.get(), servletResponse);
	}


	@PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_view
			+ "\",\"" + MenuConstants.post_cust_invoices_view
			+ "\",\"" + MenuConstants.pre_cust_invoices_list
			+ "\",\"" + MenuConstants.post_cust_invoices_list + "\")")
	@PostMapping(value = "/invoicePdf/download/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<?> downloadBulkInvoicePdfs(@RequestBody BulkInvoiceDownloadRequest request,
											   HttpServletResponse servletResponse) {
		logger.info("Bulk invoice PDF download requested.");
		try {
			return pdfUtil.downloadBulkInvoicePdfs(
					request == null ? null : request.getDebitDocIds(),
					servletResponse);
		} catch (ResponseStatusException exception) {
			HttpStatus status = exception.getStatus();
			logger.warn("Bulk invoice PDF download rejected: {}", exception.getReason());
			return buildBulkInvoiceDownloadError(status, exception.getReason());
		} catch (Exception exception) {
			logger.error("Unexpected error while downloading bulk invoice PDFs", exception);
			if (servletResponse.isCommitted()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			return buildBulkInvoiceDownloadError(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to download invoice PDFs");
		}
	}

	private ResponseEntity<ResponseObject> buildBulkInvoiceDownloadError(HttpStatus status, String message) {
		ResponseObject response = new ResponseObject();
		response.setResponseCode(String.valueOf(status.value()));
		response.setResponseMessage(message == null ? status.getReasonPhrase() : message);
		response.setResponseObject(null);
		return ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(response);
	}


	@RequestMapping(value = "/partnerInvoicePdf/download/{debitDocId}", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity partnerInvoiceDownload(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse) {
		Optional<PartnerDebitDocument> debitDocument = partnerDebitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			throw new RuntimeException("Invoice not found for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "]: ****** REST BillingProcessRestController() method called. invoicenumber :" + debitDocument.get().getDocnumber()+" ****** ");
		return pdfUtil.downloadPartnerInvoicePdf(debitDocument.get(), servletResponse);
	}


	@RequestMapping(value = "/trialinvoicePdf/download/{debitDocId}", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity trialdownload(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse) {
		Optional<TrialDebitDocument> debitDocument = trialDebitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			throw new RuntimeException("Invoice not found for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "]: ****** REST BillingProcessRestController() method called. invoicenumber :" + debitDocument.get().getDocnumber()+" ****** ");
		return pdfUtil.downloadTrailInvoicePdf(debitDocument.get(), servletResponse);
	}

	@PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_reprint +  "\" ,\"" + MenuConstants.post_cust_invoices_view   + MenuConstants.pre_cust_invoices_view   + MenuConstants.post_cust_invoices_reprint  + "\")")
	@RequestMapping(value = "/regeneratepdfsub/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject regeneratepdfsub(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse, HttpServletRequest req){
		ResponseObject response = new ResponseObject();
		Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage("Invoice not available!");
			response.setResponseObject(null);
			logger.error("Invoice not available for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "] regeneratepdfsub() method started. invoiceNumber :" + debitDocument.get().getDocument());
		try {
			pdfUtil.generatePDF(debitDocument.get(),true);
			pdfUtil.downloadInvoicePdf(debitDocument.get(), servletResponse);
			logger.info("[" + this.getClass().getName() + "] regeneratepdfsub() method completed.");
			response.setResponseCode(HttpStatus.OK.toString());
			response.setResponseMessage("Invoice PDF regeneration is in-progress. Please check bill run status to know the current status.");
			response.setResponseObject(null);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}


	@PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_reprint +  "\" ,\"" + MenuConstants.post_cust_invoices_view   + MenuConstants.pre_cust_invoices_view   + MenuConstants.post_cust_invoices_reprint  + "\")")
	@RequestMapping(value = "/regeneratepartnerpdfsub/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject regeneratepartnerpdfsub(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse, HttpServletRequest req){
		ResponseObject response = new ResponseObject();
		Optional<PartnerDebitDocument> debitDocument = partnerDebitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage("Invoice not available!");
			response.setResponseObject(null);
			logger.error("Invoice not available for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "] RegeneratePartnerPdfSub() method started. invoiceNumber :" + debitDocument.get().getDocument());
		try {
			pdfUtil.generatePartnerPDF(debitDocument.get());
			pdfUtil.downloadPartnerInvoicePdf(debitDocument.get(), servletResponse);
			logger.info("[" + this.getClass().getName() + "] regeneratepdfsub() method completed.");
			response.setResponseCode(HttpStatus.OK.toString());
			response.setResponseMessage("Partner Invoice PDF regeneration is in-progress. Please check bill run status to know the current status.");
			response.setResponseObject(null);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}


	@RequestMapping(value = "/regeneratePdfForTrail/{debitDocId}", method = RequestMethod.GET)
	public ResponseObject regeneratePdfForTrail(@PathVariable("debitDocId") Integer debitDocId, HttpServletResponse servletResponse, HttpServletRequest req){
		ResponseObject response = new ResponseObject();
		Optional<TrialDebitDocument> debitDocument = trialDebitDocRepository.findById(debitDocId);
		if(!debitDocument.isPresent()) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage("Invoice not available!");
			response.setResponseObject(null);
			logger.error("Invoice not available for given id: "+debitDocId);
		}
		logger.info("[" + this.getClass().getName() + "] regeneratepdfsub() method started. invoiceNumber :" + debitDocument.get().getDocnumber());
		try {
			pdfUtil.generateTrialPDF(debitDocument.get(),true);
			pdfUtil.downloadTrailInvoicePdf(debitDocument.get(), servletResponse);
			logger.info("[" + this.getClass().getName() + "] regeneratepdfsub() method completed.");
			response.setResponseCode(HttpStatus.OK.toString());
			response.setResponseMessage("Invoice PDF regeneration is in-progress. Please check bill run status to know the current status.");
			response.setResponseObject(null);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}


	@RequestMapping(value = "/getOfferPriceWithTax/plan", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getOfferPriceWithTax(@RequestParam("planIds") List<Integer> planIds, @RequestParam("discount") double discount, @RequestParam(name ="planGroupId",required=false) Integer planGroupId)
	{
		HashMap<String, Object> resource = new HashMap<String, Object>();
		DecimalFormat df = new DecimalFormat("0.00");

		try
		{
			Double totalOfferAmount = 0.0;
			Double amount = 0.0;
			Double totalamoutWithouttax=0.00;
			Map<String, Object> response = new HashMap<String, Object>();

			List<CustomerChargeHistory> list=new ArrayList<>();
			for(Integer id: planIds)
			{
				Integer planGroupMappingId=null;
				if ( planGroupId!=null)
					planGroupMappingId=planGroupMappingRepository.findPlanGroupMappingByPlanGroupIdAndPlanId(planGroupId,id);

				PostpaidPlan plan=postpaidPlanRepo.getLightPostpaidDTO(id);
				if(plan.getCategory()!=null && plan.getCategory().equalsIgnoreCase("Business Promotion"))
				{
					if(plan.getNewOfferPrice()!=null)
					{
						response.put("finalAmount",plan.getNewOfferPrice());
						resource.put("result", response);
						return ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE)).body(resource);
					}
				}

				List<Integer>  planChargeIds=postpaidPlanChargeRepo.getChargeListByPlanId(id);
				List<Charge> charges=chargeRepository.findByChargeIds(planChargeIds);

				for(Charge charge: charges)
				{
					amount=0.0;
					CustomerChargeHistory chargeHistory = new CustomerChargeHistory();
					chargeHistory.setPlanId(plan.getId());
					chargeHistory.setPlanName(plan.getName());
					chargeHistory.setChargeAmount(charge.getActualprice());
					chargeHistory.setTaxId(charge.getTax().getId());
					chargeHistory.setChargeId(charge.getId());
					List<Double>  price=new ArrayList<>();

					if (planGroupMappingId!=null)
						price=planGroupMappingChargeRelRepo.findByPlanIdAndChargeIdAndPlanGroupMappingId(id,charge.getId(),planGroupMappingId);

					if(price.size() <= 0.0)
						price=postpaidPlanChargeRepo.getChargeListByPlanIdAndChargeId(id,charge.getId());

					if(price!=null && !price.isEmpty())
						chargeHistory.setChargeAmount(price.get(0));
					chargeHistory.setDiscount(discount);
					if(charge.getTax().getTaxtype().equalsIgnoreCase("TIER") || charge.getTax().getTaxtype().equalsIgnoreCase("Compound")) {
//						chargeHistory.setTaxAmount(0.0);	     //Will not work as set 0 will set 0 in all tax
						taxService.calculateTierTax(chargeHistory, chargeHistory.getTaxId());
					}
					list.add(chargeHistory);
					amount = chargeHistory.getChargeAmount() + chargeHistory.getTaxAmount()-chargeHistory.getDiscount();
					totalOfferAmount = totalOfferAmount + amount;
				}

			}
			response.put("finalAmount", Double.valueOf(df.format(totalOfferAmount)));
			resource.put("result", response);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE)).body(resource);
		} catch(Exception ex) {
			throw new RuntimeException("Exception: "+ex.getMessage());
		}
	}


	@RequestMapping(value = "/payment/generatereceipt/{paymentid}", method = RequestMethod.GET)
	public ResponseObject generatereceipt(@PathVariable("paymentid") String paymentid,HttpServletResponse servletResponse, HttpServletRequest req) {
		logger.info("[" + this.getClass().getName() + "]: ****** REST generatereceipt() method called. paymentid :" + paymentid+" ****** ");
		ResponseObject response = new ResponseObject();
		Boolean flag=false;
		try {
			if(paymentid!=null)
			{
				CreditDocument creditDocument=creditDocRepository.findById(Integer.parseInt(paymentid)).orElse(null);
				if(creditDocument!=null)
				{
					flag=pdfUtil.generateReceipt(creditDocument);
					if(flag) {
						pdfUtil.downloadReceiptPdf(paymentid, servletResponse);
					}
					response.setResponseCode(HttpStatus.OK.toString());
					response.setResponseMessage("Receipt Generated Successfully");
					response.setResponseObject(new Object());
				}
			}
		} catch (RuntimeException e) {
			response.setResponseCode(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()));
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
			response.setResponseMessage(e.getMessage());
			response.setResponseObject(null);
			logger.error(e.toString(),e);
		}
		return response;
	}
}
