package com.savbill.salescrmsbss.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.savbill.salescrmsbss.entity.Charge;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.LeadServiceMapping;
import com.savbill.salescrmsbss.entity.PostpaidPlan;
import com.savbill.salescrmsbss.entity.PostpaidPlanCharge;
import com.savbill.salescrmsbss.entity.Product;
import com.savbill.salescrmsbss.entity.ProductPlanMapping;
import com.savbill.salescrmsbss.entity.QuotationCircuitMapping;
import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.LeadServiceMappingRepository;
import com.savbill.salescrmsbss.repository.PostpaidPlanChargeRepository;
import com.savbill.salescrmsbss.repository.PostpaidPlanRepository;
import com.savbill.salescrmsbss.repository.ProductPlanMappingRepository;
import com.savbill.salescrmsbss.repository.ProductRepository;
import com.savbill.salescrmsbss.repository.QuotationDetailsRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

@Component("pdfGenerator")
public class PDFGenerator {

	@Autowired
	private QuotationDetailsRepository quotationDetailsRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private LeadServiceMappingRepository leadServiceMappingRepository;

//	@Autowired
//	private ChargeRepository chargeRepository;

	@Autowired
	private PostpaidPlanChargeRepository postpaidPlanChargeRepository;

	@Autowired
	private ProductPlanMappingRepository productPlanMappingRepository;

	@Autowired
	private PostpaidPlanRepository postpaidPlanRepository;

	@Autowired
	private PostpaidPlanChargeRepository postPaidPlanChargeRepository;

	@Autowired
	private ProductRepository productRepository;

	Font FONT = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));

	public void generatePdfReport(Long quotationId, HttpServletResponse response) throws IOException {

		Optional<QuotationDetails> quotationDetailsOp = quotationDetailsRepository.findById(quotationId);
		if (quotationDetailsOp.isPresent()) {
			QuotationDetails quotationDetail = quotationDetailsOp.get();
			Document document = new Document();
			Paragraph p1 = new Paragraph();
			try {
				List<Charge> chargeList = getPlanIdListByQuotationDetails(quotationDetail);
				System.out.println("chargeList: " + chargeList);
				PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
				writer.setPageEvent(new MyPdfPageEventHelper()); // register the
				document.open();
				addLogo(document, quotationDetail);
				addDocHeader(document, quotationDetail);

				// For inventory table 1
				createTable(document, Integer.parseInt(ReportConstants.TABLE1_NOOFCOLUMNS), "table1", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				// For installation charge
				createTable(document, Integer.parseInt(ReportConstants.TABLE2_NOOFCOLUMNS), "table2", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				// For service charge
				createTable(document, Integer.parseInt(ReportConstants.TABLE3_NOOFCOLUMNS), "table3", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				addFooter(document, quotationDetail);
				document.close();
				System.out.println("------------------Your PDF Report is ready!-------------------------");
				/// writer.to
			} catch (FileNotFoundException | DocumentException e) {
				e.printStackTrace();
			}
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"QuotationDetails is not found for ID :" + quotationId, null);
		}
	}

	private void addLogo(Document document, QuotationDetails quotationDetail) {
		try {
			Image img = Image.getInstance(ReportConstants.LOGO_IMGPATH);
			String[] logoImgScaleArray = ReportConstants.LOGO_IMS_SCALE.split(",");
			System.out.println(logoImgScaleArray);
			img.scalePercent(Float.parseFloat(logoImgScaleArray[0]), Float.parseFloat(logoImgScaleArray[1]));
			img.setAlignment(Element.ALIGN_LEFT);
			document.add(img);
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addDocHeader(Document document, QuotationDetails quotationDetail) throws DocumentException {
//		String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
		LeadServiceMapping leadServiceMapping = null;
		String location = null;
		String org_fullname = null;

		Optional<LeadMaster> leadMasterOp = leadMasterRepository.findById(quotationDetail.getLeadId());
		if (leadMasterOp.isPresent()) {
			LeadMaster leadMaster = leadMasterOp.get();
			List<QuotationCircuitMapping> quotationCircuitMappingList = quotationDetail
					.getQuotationCircuitMappingList();
			List<String> services = new ArrayList<>();
			for (QuotationCircuitMapping circuit : quotationCircuitMappingList) {
				leadServiceMapping = leadServiceMappingRepository.findById(circuit.getLeadServiceMappingId()).get();
				services.add(leadServiceMapping.getServiceName());
			}
			Paragraph p1 = new Paragraph();
			leaveEmptyLine(p1, 1);
//			String dateStr = String.valueOf(quotationDetail.getCreatedAt()).replace("T", " ");
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formatDateTime = quotationDetail.getCreatedAt().format(format);

			if (formatDateTime != null && !formatDateTime.equalsIgnoreCase(""))
				p1.add(new Paragraph(formatDateTime));

			leaveEmptyLine(p1, 2);
			String to = ReportConstants.ADDRESS_TO;

			if (leadMaster.getFirstname() != null)
				if (leadMaster.getLastname() != null)
					org_fullname = leadMaster.getFirstname() + " " + leadMaster.getLastname();
				else
					org_fullname = leadMaster.getFirstname();
			else if (leadMaster.getLastname() != null)
				org_fullname = leadMaster.getLastname();
			else
				org_fullname = "";

			p1.add(new Paragraph(to));
			leaveEmptyLine(p1, 1);
			p1.add(new Paragraph(org_fullname));
			if (leadServiceMapping != null && leadServiceMapping.getLocation() != null
					&& !String.valueOf(leadServiceMapping.getLocation()).equalsIgnoreCase("")) {
				location = String.valueOf(leadServiceMapping.getLocation());
			}

			if (location != null && !location.equalsIgnoreCase(""))
				p1.add(new Paragraph(location));

			leaveEmptyLine(p1, 1);
			if (services != null && services.size() > 0)
				p1.add(new Paragraph(ReportConstants.QUOTATION_REASON + String.join(",", services)));

			leaveEmptyLine(p1, 1);
			p1.add(new Paragraph(ReportConstants.GREETING));
			leaveEmptyLine(p1, 1);

			p1.add(new Paragraph(ReportConstants.WE_WOULD_LIKE_TO_THANK + org_fullname + " " + ReportConstants.MSG1));

			leaveEmptyLine(p1, 1);
			p1.add(new Paragraph(ReportConstants.MSG2 + String.join(",", services) + ReportConstants.MSG3));
			leaveEmptyLine(p1, 1);
			document.add(p1);
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"LeadMaster is not found for ID :" + quotationDetail.getLeadId(), null);
		}
	}

	private void createTable(Document document, Integer noOfColumns, String tableLable,
			QuotationDetails quotationDetail, List<Charge> chargeList) throws DocumentException {

		List<QuotationCircuitMapping> quotationCircuitMappingList = quotationDetail.getQuotationCircuitMappingList();
		List<ProductPlanMapping> productPlanMapList = new ArrayList<>();
//		List<String> chargeTypes = new ArrayList<>();
//		List<String> chargeCategories = new ArrayList<>();
//		List<PostpaidPlanCharge> postPlanChargeList = new ArrayList<>();
//		List<String> chargePrices = new ArrayList<>();
		// For inventory

		for (QuotationCircuitMapping circuit : quotationCircuitMappingList) {

			LeadServiceMapping leadServiceMapping = leadServiceMappingRepository
					.findById(circuit.getLeadServiceMappingId()).get();
			PostpaidPlan postPaidPlan = postpaidPlanRepository.findByApiGatewayPlanId(leadServiceMapping.getPlanId());

			if (postPaidPlan != null && postPaidPlan.getId() != null) {
				productPlanMapList = productPlanMappingRepository.findByPostPaidPlan_id(postPaidPlan.getId());
//				postPlanChargeList = postPaidPlanChargeRepository.findByPlan_Id(postPaidPlan.getId());
//				if (postPlanChargeList != null && postPlanChargeList.size() > 0) {
//					for (PostpaidPlanCharge planChargeObj : postPlanChargeList) {
//						chargeCategories.add(planChargeObj.getCharge().getChargecategory());
//						chargeTypes.add(planChargeObj.getCharge().getChargetype());
//					}
//				}
			}
		}

		Paragraph paragraph = new Paragraph();

		leaveEmptyLine(paragraph, 1);

		PdfPTable table = new PdfPTable(noOfColumns);
		table.setHeaderRows(1);
		Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
		Font tableColumnPhrase = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		if (tableLable.equalsIgnoreCase("table1") && (productPlanMapList != null && productPlanMapList.size() > 0)) {

			String header = ReportConstants.TABLE1_COLUMNNAMES;
			System.out.println(header);
			Phrase table1_HeadLine = new Phrase(ReportConstants.TABLE1_LABEL, boldFont);

			paragraph.add(new Paragraph(table1_HeadLine));
			leaveEmptyLine(paragraph, 1);
			document.add(paragraph);
			for (int i = 0; i < noOfColumns; i++) {
				String[] table1_columnNames = header.split(", ");
				PdfPCell cell = new PdfPCell(new Phrase(table1_columnNames[i], tableColumnPhrase));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				cell.setBackgroundColor(BaseColor.WHITE);
				table.addCell(cell);
			}
		}
		if (chargeList != null && chargeList.size() > 0) {
			List<String> chargeCatList = chargeList.stream().map(Charge::getChargecategory).collect(Collectors.toList());
			List<String> chargeTypeList = chargeList.stream().map(Charge::getChargetype).collect(Collectors.toList());
			if (tableLable.equalsIgnoreCase("table2") && (chargeCatList!= null && chargeCatList.size()>0 && chargeCatList.contains("INSTALLATION")) && (chargeTypeList!= null && chargeTypeList.size()>0 && chargeTypeList.contains("NON_RECURRING"))) {
					String header = ReportConstants.TABLE2_COLUMNNAMES;
					System.out.println(header);
					Phrase table2_HeadLine = new Phrase(ReportConstants.TABLE2_LABEL, boldFont);
					paragraph.add(new Paragraph(table2_HeadLine));
					leaveEmptyLine(paragraph, 1);
					paragraph.setAlignment(Element.ALIGN_CENTER);
					try {
						document.add(paragraph);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int i = 0; i < noOfColumns; i++) {
						String[] table2_columnNames = header.split(", ");
						PdfPCell cell = new PdfPCell(new Phrase(table2_columnNames[i], tableColumnPhrase));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderColor(BaseColor.BLACK);
						cell.setBackgroundColor(BaseColor.WHITE);
						table.addCell(cell);
					}
			}
		}
		if (chargeList != null && chargeList.size() > 0) {

				if (tableLable.equalsIgnoreCase("table3")) {

					String header = ReportConstants.TABLE3_COLUMNNAMES;
					System.out.println(header);
					Phrase table3_HeadLine = new Phrase(ReportConstants.TABLE3_LABEL, boldFont);
					paragraph.add(new Paragraph(table3_HeadLine));
					leaveEmptyLine(paragraph, 1);
					paragraph.setAlignment(Element.ALIGN_CENTER);
					try {
						document.add(paragraph);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int i = 0; i < noOfColumns; i++) {
						String[] table2_columnNames = header.split(", ");
						PdfPCell cell = new PdfPCell(new Phrase(table2_columnNames[i], tableColumnPhrase));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderColor(BaseColor.BLACK);
						cell.setBackgroundColor(BaseColor.WHITE);
						table.addCell(cell);
					}
				}

		}

		getDbData(table, noOfColumns, quotationDetail, tableLable, chargeList);
		document.add(table);
	}

	private void getDbData(PdfPTable table, int noOfColumns, QuotationDetails quotationDetail, String tableLabel,
			List<Charge> chargeList) {

		List<QuotationCircuitMapping> quotationCircuitMappingList = quotationDetail.getQuotationCircuitMappingList();

		// For inventory
		for (QuotationCircuitMapping circuit : quotationCircuitMappingList) {
			List<ProductPlanMapping> productPlanMapList = new ArrayList<>();
			LeadServiceMapping leadServiceMapping = leadServiceMappingRepository
					.findById(circuit.getLeadServiceMappingId()).get();
			PostpaidPlan postPaidPlan = postpaidPlanRepository.findByApiGatewayPlanId(leadServiceMapping.getPlanId());

			if (postPaidPlan != null && postPaidPlan.getId() != null) {
				productPlanMapList = productPlanMappingRepository.findByPostPaidPlan_id(postPaidPlan.getId());
			}

			if (noOfColumns == 4 && tableLabel.equalsIgnoreCase("table1")
					&& (productPlanMapList != null && productPlanMapList.size() > 0)) {
				for (ProductPlanMapping mapping : productPlanMapList) {
					table.setWidthPercentage(100);
					table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
					table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

					table.addCell(leadServiceMapping.getLocation() != null
							&& !String.valueOf(leadServiceMapping.getLocation()).equalsIgnoreCase("")
									? String.valueOf(leadServiceMapping.getLocation())
									: "N/A");
					Product product = new Product();
					if (mapping.getProduct() != null && mapping.getProduct().getId() != null)
						product = productRepository.findById(mapping.getProduct().getId()).get();

					table.addCell(product.getName() != null && !String.valueOf(product.getName()).equalsIgnoreCase("")
							? String.valueOf(product.getName())
							: "N/A");
					table.addCell(
							mapping.getQuantity() != null && !String.valueOf(mapping.getQuantity()).equalsIgnoreCase("")
									? String.valueOf(mapping.getQuantity())
									: "N/A");
					table.addCell(mapping.getRevisedCharge() != null && mapping.getQuantity()!= null
							&& !String.valueOf(mapping.getRevisedCharge()).equalsIgnoreCase("")
									? String.valueOf(mapping.getRevisedCharge()*mapping.getQuantity())
									: "N/A");
				}
			}

			if (chargeList != null && chargeList.size() > 0) {
				// For Installation Charge
				chargeList.forEach(obj -> {
					if (noOfColumns == 2 && tableLabel.equalsIgnoreCase("table2")
							&& obj.getChargecategory().equalsIgnoreCase("installation") && obj.getChargetype().equalsIgnoreCase("NON_RECURRING")) {

						table.setWidthPercentage(100);
						table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
						table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
						table.addCell(leadServiceMapping.getLocation() != null
								&& !String.valueOf(leadServiceMapping.getLocation()).equalsIgnoreCase("")
										? String.valueOf(leadServiceMapping.getLocation())
										: "N/A");
						table.addCell(obj.getActualprice()!= 0L?String.valueOf(obj.getActualprice()):"N/A");
					}
				});
			}

			// for Service Charges
			if (noOfColumns == 4 && tableLabel.equalsIgnoreCase("table3")) {
				table.setWidthPercentage(100);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

				table.addCell(leadServiceMapping.getServiceName() != null
						&& !String.valueOf(leadServiceMapping.getServiceName()).equalsIgnoreCase("")
						? String.valueOf(leadServiceMapping.getServiceName())
						: "N/A");
				table.addCell(leadServiceMapping.getLocation() != null
						&& !String.valueOf(leadServiceMapping.getLocation()).equalsIgnoreCase("")
						? String.valueOf(leadServiceMapping.getLocation())
						: "N/A");
				table.addCell(leadServiceMapping.getBandwidth() != null && !String.valueOf(leadServiceMapping.getBandwidth()).equalsIgnoreCase("")
						? String.valueOf(leadServiceMapping.getBandwidth())
						: "N/A");
//					String offerPrice = circuit.getOfferPrice() != null ? String.valueOf(circuit.getOfferPrice())
//							: null;
				table.addCell(circuit.getOfferPrice() != 0L ? String.valueOf(circuit.getOfferPrice()) : "N/A");
			}
		}
	}

	private void addFooter(Document document, QuotationDetails quotationDetail) throws DocumentException {

		Paragraph p2 = new Paragraph();
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(ReportConstants.NOTE));
		p2.add(new Paragraph(ReportConstants.NOTE_LINE1));
		p2.add(new Paragraph(ReportConstants.NOTE_LINE2));
		String installationValidity = quotationDetail.getInstallationValidity() + " "
				+ quotationDetail.getInstallationUnit();
		String quotationValidity = quotationDetail.getValidity() + " " + quotationDetail.getValidityUnit();
		String note3 = ReportConstants.NOTE_LINE3 + installationValidity;
		p2.add(new Paragraph(note3));
		String note4 = ReportConstants.NOTE_LINE4 + quotationValidity;
		p2.add(new Paragraph(note4));
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(ReportConstants.FOOTER_LINE));
		leaveEmptyLine(p2, 1);

		p2.add(new Paragraph(ReportConstants.SENDER_ADDRESS_DETAILS));
		p2.add(new Paragraph(ReportConstants.SENDER_COINTACT_DETAILS));
		p2.add(new Paragraph(ReportConstants.SENDER_ISO_INFO));
		document.add(p2);
	}

	private static void leaveEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	public String getPdfNameWithDate() {
		String localDateString = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern(ReportConstants.REPORT_FINALNAME_FORMAT));
		return ReportConstants.PDF_DIR + ReportConstants.REPORT_FILENAME + "-" + localDateString + ".pdf";
	}

	public List<Charge> getPlanIdListByQuotationDetails(QuotationDetails quotationDetail) {
		List<Long> planIdList = new ArrayList<Long>();
		List<QuotationCircuitMapping> quotationCircuitMappingList = quotationDetail.getQuotationCircuitMappingList();
		if (quotationCircuitMappingList != null && quotationCircuitMappingList.size() > 0) {
			for (QuotationCircuitMapping quotationCircuitMapping : quotationCircuitMappingList) {
				Optional<LeadServiceMapping> leadServiceMappingOp = leadServiceMappingRepository
						.findById(quotationCircuitMapping.getLeadServiceMappingId());
				if (leadServiceMappingOp.isPresent()) {
					PostpaidPlan postPaidPlan = postpaidPlanRepository
							.findByApiGatewayPlanId(leadServiceMappingOp.get().getPlanId());
					if (postPaidPlan != null && postPaidPlan.getId() != null)
						planIdList.add(Long.parseLong(String.valueOf(postPaidPlan.getId())));
				}
			}
		}
		return getChargeListByPlanId(planIdList);
	}

	public List<Charge> getChargeListByPlanId(List<Long> planIds) {
		List<Charge> chargeList = new ArrayList<Charge>();
		if (planIds != null && planIds.size() > 0) {
			for (Long planId : planIds) {
				List<PostpaidPlanCharge> ppc = postpaidPlanChargeRepository.findByPlan_Id(planId.intValue());
				if (ppc != null && ppc.size() > 0) {
					for (PostpaidPlanCharge postpaidPlanCharge : ppc) {
						chargeList.add(postpaidPlanCharge.getCharge());
					}
				}
			}
		}
		return chargeList;
	}

	public void addWatermark(Document d1, PdfWriter writer) throws DocumentException {
		ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Phrase("SUBISU", FONT),
				297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);

	}

	class MyPdfPageEventHelper extends PdfPageEventHelper {

		@Override
		public void onEndPage(PdfWriter pdfWriter, Document document) {

			PdfContentByte pdfContentByte = pdfWriter.getDirectContentUnder();

			String waterMarkText = "SUBISU";

			Phrase phrase = new Phrase(waterMarkText, new Font(FontFamily.HELVETICA, // Select the Font name of
																						// waterMark Text
					80, // Select the Font type of waterMark Text
					Font.BOLD, // Select the Font style of waterMark Text
					BaseColor.LIGHT_GRAY)); // Select the Font colour of waterMark Text

			// 300f is x axis,
			// 550f is y axis,
			ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER, phrase, 280f, 200f, 0f);
		}
	}

	public File generatePdfReportForMail(Long quotationId, String filename) throws IOException {
		File file = null;
		Optional<QuotationDetails> quotationDetailsOp = quotationDetailsRepository.findById(quotationId);

		if (quotationDetailsOp.isPresent()) {
			QuotationDetails quotationDetail = quotationDetailsOp.get();
			List<Charge> chargeList = getPlanIdListByQuotationDetails(quotationDetail);
			Document document = new Document();
			Paragraph p1 = new Paragraph();
			try {
				file = new File(filename);
				// file.createNewFile();
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
				writer.setPageEvent(new MyPdfPageEventHelper()); // register the
				document.open();
				addLogo(document, quotationDetail);
				addDocHeader(document, quotationDetail);

				// For inventory table 1
				createTable(document, Integer.parseInt(ReportConstants.TABLE1_NOOFCOLUMNS), "table1", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				// For installation charge
				createTable(document, Integer.parseInt(ReportConstants.TABLE2_NOOFCOLUMNS), "table2", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				// For service charge
				createTable(document, Integer.parseInt(ReportConstants.TABLE3_NOOFCOLUMNS), "table3", quotationDetail,
						chargeList);
				leaveEmptyLine(p1, 1);

				addFooter(document, quotationDetail);
				document.close();
				System.out.println("------------------Your PDF Report is ready!-------------------------");
				/// writer.to
			} catch (FileNotFoundException | DocumentException e) {
				e.printStackTrace();
			}
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"QuotationDetails is not found for ID :" + quotationId, null);
		}
		return file;
	}

}