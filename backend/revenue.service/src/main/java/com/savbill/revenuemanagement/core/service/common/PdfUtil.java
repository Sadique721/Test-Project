package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.BulkInvoiceDownloadProjection;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.PartnerDebitDocument;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.XsltManagementRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerDebitDocRepository;
import com.savbill.revenuemanagement.core.security.jwt.JwtUtil;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class PdfUtil<D> {

    private static final Logger logger = LoggerFactory.getLogger(PdfUtil.class);
    private static final int MAX_BULK_INVOICE_COUNT = 1000;
    private static final int MAX_BULK_INVOICE_TOTAL_SIZE_MB = 250;
    private static final long MAX_BULK_INVOICE_TOTAL_SIZE_BYTES = MAX_BULK_INVOICE_TOTAL_SIZE_MB * 1024L * 1024L;
    private static final int ZIP_COPY_BUFFER_SIZE = 16 * 1024;
    @Autowired
    DebitDocRepository debitDocRepository;
    @Autowired
    TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    CustomersRepository customersRepository;
    private Path barterDocDir;
    @Autowired
    private XsltManagementRepository templateRepository;
    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;
    @Autowired
    private PartnerDebitDocRepository partnerDebitDocRepository;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    public boolean generatePDF(DebitDocument debitDocument, Boolean isRePrint) throws Exception {

        logger.debug("[InvoiceUtil]: generatePDF() called for (DB) " + debitDocument.getDocnumber());
        Date startDate = new Date();
        try {
            Integer printCounter = debitDocument.getPrintCounter();
            String reprintBy = "";
            Integer reprintById = 1;
            LocalDateTime reprintDate = debitDocument.getLastReprintDate();
            Integer lco_id = debitDocument.getLcoId();
            Long buId = debitDocument.getBuId();
//            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

            if (!isRePrint) {
                //TODO: Update reprintby and reprintById
                if (printCounter == null)
                    printCounter = 0;
                debitDocument.setPrintCounter(printCounter + 1);
            }

            String jrsmlfile = null;
            String templateType = "Billing";
            if (isRePrint)
                templateType = "ReBilling";
            reprintDate = LocalDateTime.now();
            if (buId != null) {
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndBuidAndMvnoId(templateType, buId, debitDocument.getCustomer().getMvnoId());
            } else {
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoId(templateType, debitDocument.getCustomer().getMvnoId());
            }
            if (lco_id != null) {
                if (buId != null)
                    jrsmlfile = templateRepository.findByTemplatetypeRebillingAndBuidAndMvnoIdAndLcoid(templateType, buId, debitDocument.getCustomer().getMvnoId(), lco_id);
                else
                    jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoIdAndLcoid(templateType, debitDocument.getCustomer().getMvnoId(), lco_id);
            }


            if (jrsmlfile == null)
                throw new Exception("Template Not Found to Generate PDF");

            InputStream targetStream = new ByteArrayInputStream(jrsmlfile.getBytes());
            String path = clientServiceRepository.findValueByNameAndMvnoId("pdfpath", debitDocument.getCustomer().getMvnoId());

            logger.debug("[" + this.getClass().getName() + "] generatePDF(): Exported path will be " + path);
            JasperReport jasperReport = JasperCompileManager.compileReport(targetStream);
            Map<String, Object> parameters = new HashMap<String, Object>();

            String xml = debitDocument.getDocument();
            if (xml == null || (xml != null && xml.isEmpty())) {
                xml = prepaidInvoiceService.setInvoiceXml(debitDocument);
                debitDocument.setDocument(xml);
            }
            if (isRePrint) {
                if (xml != null) {
                    xml = prepaidInvoiceService.setInvoiceXml(debitDocument);
                    debitDocument.setDocument(xml);
                }
            }

            if (debitDocument.getDocument() == null || debitDocument.getDocument().equalsIgnoreCase(""))
                throw new Exception("XML Data Not Found to Generate PDF"); //dira-141

            Document doc = convertStringToDocument(xml);
            if (isRePrint) {
                if (printCounter == null)
                    printCounter = 0;
                doc.getElementsByTagName("printCounter").item(0).setTextContent(printCounter.toString());
//                if (doc.getElementsByTagName("reprintById").item(0).getTextContent() != null) {
//                    doc.getElementsByTagName("reprintBy").item(0).setTextContent(reprintBy);
//                    doc.getElementsByTagName("reprintById").item(0).setTextContent(Integer.toString(reprintById));
//                    doc.getElementsByTagName("lastReprintDate").item(0).setTextContent(reprintDate.toString());
//                }
            }

            JRXmlDataSource jrxmlds = new JRXmlDataSource(doc, "/invoice");

            //Need to add billRunId
            Integer billrunId = debitDocument.getId();
            if (debitDocument.getBillrunid() != null) {
                billrunId = debitDocument.getBillrunid();
            } else {
                BillRun billRun = postpaidInvoiceService.addBillRunData(1, debitDocument.getTotalamount(), 1, 0);
                if (billRun != null) {
                    billrunId = billRun.getId();
                    debitDocument.setBillrunid(billrunId);
                }
            }
            File directory = new File(path + File.separator + debitDocument.getCustomer().getId());
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("[" + this.getClass().getName() + "] " + path + File.separator + debitDocument.getId() + " created...");
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrxmlds);
            JasperExportManager.exportReportToPdfFile(jasperPrint, new ClassPathResource(path + File.separator + debitDocument.getCustomer().getId() + File.separator + debitDocument.getDocnumber() + ".pdf").getPath());
            Date endDate = new Date();

            if (isRePrint) {
                debitDocument.setPrintCounter(printCounter + 1);
                debitDocument.setBuId(Long.valueOf(billrunId));
                debitDocRepository.save(debitDocument);
            } else
                debitDocRepository.save(debitDocument);

            logger.info("[" + this.getClass().getName() + "] " + debitDocument.getDocnumber() + ".PDF Generated Successfully Elapsed Time : " + new DateTimeUtil().getElapsedTime(startDate, endDate) + " sec");
            return true;

        } catch (JRException e) {
            e.printStackTrace();
            logger.error("generatePDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("generatePDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0"))
                throw new Exception("Template not Found");
            else
                throw new Exception(e.getMessage());
        }
    }


    public boolean generatePartnerPDF(PartnerDebitDocument debitDocument) throws Exception {

        logger.debug("[InvoiceUtil]: generatePartnerPDF() called for (DB) " + debitDocument.getDocnumber());
        Date startDate = new Date();
        try {
            Long buId = debitDocument.getBuId();
            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();
            String jrsmlfile = null;
            String templateType = "PartnerBilling";

            if (mvnoId != null && mvnoId == 1)
                jrsmlfile = templateRepository.findByTemplateTypePartnerBillingAndMvnoId(templateType, 1);
            else
                jrsmlfile = templateRepository.findByTemplateTypePartnerBillingAndMvnoId(templateType, mvnoId);


            if (jrsmlfile == null)
                throw new Exception("Template Not Found to Generate Partner PDF");

            if (debitDocument.getDocument() == null || debitDocument.getDocument().equalsIgnoreCase(""))
                throw new Exception("XML Data Not Found to Generate Partner PDF");

            InputStream targetStream = new ByteArrayInputStream(jrsmlfile.getBytes());
            String path = clientServiceRepository.findValueByNameAndMvnoId("pdfpath", mvnoId);

            logger.debug("[" + this.getClass().getName() + "] generatePDF(): Exported path will be " + path);
            JasperReport jasperReport = JasperCompileManager.compileReport(targetStream);
            Map<String, Object> parameters = new HashMap<String, Object>();

            Document doc = convertStringToDocument(debitDocument.getDocument());

            JRXmlDataSource jrxmlds = new JRXmlDataSource(doc, "/invoice");

            //Need to add billRunId
            Integer billrunId = debitDocument.getId();
            if (debitDocument.getBillrunid() != null) {
                billrunId = debitDocument.getBillrunid();
            } else {
                BillRun billRun = postpaidInvoiceService.addBillRunData(1, debitDocument.getTotalamount(), 1, 0);
                if (billRun != null) {
                    billrunId = billRun.getId();
                    debitDocument.setBillrunid(billrunId);
                    partnerDebitDocRepository.save(debitDocument);
                }
            }
            File directory = new File(path + File.separator + debitDocument.getDocnumber());
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("[" + this.getClass().getName() + "] " + path + File.separator + debitDocument.getId() + " created...");
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrxmlds);
            JasperExportManager.exportReportToPdfFile(jasperPrint, new ClassPathResource(path + File.separator + debitDocument.getDocnumber() + File.separator + debitDocument.getDocnumber() + ".pdf").getPath());
            Date endDate = new Date();

            logger.info("[" + this.getClass().getName() + "] " + debitDocument.getDocnumber() + ".PDF Generated Successfully Elapsed Time : " + new DateTimeUtil().getElapsedTime(startDate, endDate) + " sec");
            return true;

        } catch (JRException e) {
            e.printStackTrace();
            logger.error("generatePartnerPDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("generatePartnerPDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0"))
                throw new Exception("Template not Found");
            else
                throw new Exception(e.getMessage());
        }
    }


    public boolean generateTrialPDF(TrialDebitDocument debitDocument, Boolean isRePrint) throws Exception {

        logger.debug("[InvoiceUtil]: generatePDF() called for (DB) " + debitDocument.getDocnumber());
        Date startDate = new Date();
        Customers customers = debitDocument.getCustomer();
        try {
            //Integer printCounter = debitDocument.getPrintCounter();
            String reprintBy = "";
            Integer reprintById = 1;
            //LocalDateTime reprintDate = debitDocument.getLastReprintDate();
            Integer lco_id = customers.getLcoId();
            Long buId = customers.getBuId();
            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

            if (!isRePrint) {
                //TODO: Update reprintby and reprintById
                //if (printCounter == null)
                //printCounter = 0;
                //debitDocument.setPrintCounter(printCounter + 1);
            }

            String jrsmlfile = null;
            String templateType = "Billing";
            if (isRePrint)
                templateType = "ReBilling";
            //reprintDate = LocalDateTime.now();
            if (mvnoId != null && mvnoId == 1) {
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoId(templateType, debitDocument.getCustomer().getMvnoId());
            } else if (buId != null && mvnoId != null) {
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndBuidAndMvnoId(templateType, buId, mvnoId);
            } else {
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoId(templateType, mvnoId);
            }
            if (lco_id != null) {
                if (buId != null && mvnoId != null)
                    jrsmlfile = templateRepository.findByTemplatetypeRebillingAndBuidAndMvnoIdAndLcoid(templateType, buId, mvnoId, lco_id);
                else
                    jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoIdAndLcoid(templateType, mvnoId, lco_id);
            }


            if (jrsmlfile == null)
                throw new Exception("Template Not Found to Generate PDF");

            if (debitDocument.getDocument() == null || debitDocument.getDocument().equalsIgnoreCase(""))
                throw new Exception("XML Data Not Found to Generate PDF");

            InputStream targetStream = new ByteArrayInputStream(jrsmlfile.getBytes());
            String path = clientServiceRepository.findValueByNameAndMvnoId("pdfpath", mvnoId);

            logger.debug("[" + this.getClass().getName() + "] generatePDF(): Exported path will be " + path);
            JasperReport jasperReport = JasperCompileManager.compileReport(targetStream);
            Map<String, Object> parameters = new HashMap<String, Object>();

            Document doc = convertStringToDocument(debitDocument.getDocument());
            if (isRePrint) {
                //if(printCounter==null)
                //printCounter=0;
                //doc.getElementsByTagName("printCounter").item(0).setTextContent(printCounter.toString());
//                if (doc.getElementsByTagName("reprintById").item(0).getTextContent() != null) {
//                    doc.getElementsByTagName("reprintBy").item(0).setTextContent(reprintBy);
//                    doc.getElementsByTagName("reprintById").item(0).setTextContent(Integer.toString(reprintById));
//                    doc.getElementsByTagName("lastReprintDate").item(0).setTextContent(reprintDate.toString());
//                }
            }

            JRXmlDataSource jrxmlds = new JRXmlDataSource(doc, "/invoice");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrxmlds);
            //Need to add billRunId
            Integer billrunId = debitDocument.getId();
            if (debitDocument.getBillrunid() != null) {
                billrunId = debitDocument.getBillrunid();
            } else {
                BillRun billRun = postpaidInvoiceService.addBillRunData(1, debitDocument.getTotalamount(), 1, 0);
                if (billRun != null) {
                    billrunId = billRun.getId();
                    debitDocument.setBillrunid(billrunId);
                }
            }

            File directory = new File(path + File.separator + billrunId);
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("[" + this.getClass().getName() + "] " + path + File.separator + debitDocument.getId() + " created...");
            }

            JasperExportManager.exportReportToPdfFile(jasperPrint, new ClassPathResource(path + File.separator + billrunId + File.separator + debitDocument.getDocnumber() + ".pdf").getPath());
            Date endDate = new Date();

            if (isRePrint) {
                //debitDocument.setPrintCounter(printCounter+1);
                //debitDocument.setBuId(Long.valueOf(billrunId));
                trialDebitDocRepository.save(debitDocument);
            } else
                trialDebitDocRepository.save(debitDocument);

            logger.info("[" + this.getClass().getName() + "] " + debitDocument.getDocnumber() + ".PDF Generated Successfully Elapsed Time : " + new DateTimeUtil().getElapsedTime(startDate, endDate) + " sec");
            return true;

        } catch (JRException e) {
            e.printStackTrace();
            logger.error("generatePDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("generatePDF [" + this.getClass().getName() + "] " + e, e.getMessage());
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0"))
                throw new Exception("Template not Found");
            else
                throw new Exception(e.getMessage());
        }
    }

    private Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity downloadInvoicePdf(DebitDocument debitDocument, HttpServletResponse servletResponse) {
        Resource resource = null;
        String billDir;
        Path billPath;

        try {
            String InvoiceNo = debitDocument.getCustomer().getId() + File.separator + debitDocument.getDocnumber() + ".pdf";
            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

            billDir = clientServiceRepository.findValueByNameAndMvnoId(Constants.PATHS.PDF_READ_PATH, mvnoId);
            List<String> paths = Arrays.asList(billDir.split(","));
            Path filePath = null;
            for (String path : paths) {
                logger.info("Bill Dir is :" + path);
                billPath = Paths.get(path).toAbsolutePath().normalize();
                filePath = billPath.resolve(InvoiceNo).normalize();
                logger.info("Bill Path is :" + filePath.toString());
                resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    break;
                }
            }

            if (!resource.exists()) {
                logger.info("File not found " + InvoiceNo);
                return null;
            }

            PrintWriter out = servletResponse.getWriter();
            servletResponse.setContentType("APPLICATION/OCTET-STREAM");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");

            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());

            int i;
            while ((i = fileInputStream.read()) != -1) {
                out.write(i);
            }
            fileInputStream.close();
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }


    public ResponseEntity downloadBulkInvoicePdfs(List<Integer> requestedDebitDocIds,
                                                   HttpServletResponse servletResponse) {
        List<Integer> debitDocIds = validateBulkInvoiceIds(requestedDebitDocIds);
        if (jwtUtil.getLoggedInUser() == null || jwtUtil.getLoggedInUser().getMvnoId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Logged-in user is not available");
        }

        Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();
        List<BulkInvoiceDownloadProjection> debitDocuments =
                debitDocRepository.findBulkInvoiceDownloadData(
                        debitDocIds,
                        mvnoId,
                        Constants.BILL_RUN_STATUS.GENERATED.status());
        Map<Integer, BulkInvoiceDownloadProjection> documentsById = new HashMap<>();
        for (BulkInvoiceDownloadProjection debitDocument : debitDocuments) {
            documentsById.put(debitDocument.getDebitDocId(), debitDocument);
        }

        List<Integer> unavailableInvoiceIds = new ArrayList<>();
        for (Integer debitDocId : debitDocIds) {
            if (!documentsById.containsKey(debitDocId)) {
                unavailableInvoiceIds.add(debitDocId);
            }
        }
        if (!unavailableInvoiceIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Invoices not found, not accessible, or not in Generated status: "
                            + unavailableInvoiceIds);
        }

        List<Path> billPaths = getBulkInvoiceReadPaths(mvnoId);
        List<BulkInvoicePdf> invoicePdfs = new ArrayList<>();
        List<Integer> missingPdfIds = new ArrayList<>();
        for (Integer debitDocId : debitDocIds) {
            BulkInvoiceDownloadProjection debitDocument = documentsById.get(debitDocId);
            Path pdfPath = findBulkInvoicePdf(debitDocument, billPaths);
            if (pdfPath == null) {
                missingPdfIds.add(debitDocId);
            } else {
                invoicePdfs.add(new BulkInvoicePdf(
                        pdfPath,
                        buildBulkInvoiceZipEntryName(debitDocument)));
            }
        }
        if (!missingPdfIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Invoice PDF files not found: " + missingPdfIds);
        }

        validateBulkInvoiceTotalSize(invoicePdfs);

        servletResponse.setContentType("application/zip");
        servletResponse.setHeader("Content-Disposition", "attachment; filename=\"invoices.zip\"");
        servletResponse.setHeader("Cache-Control", "no-store");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
                new BufferedOutputStream(servletResponse.getOutputStream()))) {
            // PDF files are already compressed; ZIP is used as the download container.
            zipOutputStream.setLevel(Deflater.NO_COMPRESSION);
            byte[] buffer = new byte[ZIP_COPY_BUFFER_SIZE];
            for (BulkInvoicePdf invoicePdf : invoicePdfs) {
                zipOutputStream.putNextEntry(new ZipEntry(invoicePdf.entryName));
                try (InputStream inputStream = new BufferedInputStream(
                        Files.newInputStream(invoicePdf.path))) {
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, bytesRead);
                    }
                }
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException exception) {
            logger.error("Unable to stream bulk invoice ZIP", exception);
            throw new RuntimeException("Unable to download bulk invoice PDFs", exception);
        }

        logger.info("Bulk invoice PDF download completed. mvnoId: {}, invoiceCount: {}",
                mvnoId, invoicePdfs.size());
        return ResponseEntity.ok().build();
    }

    private List<Integer> validateBulkInvoiceIds(List<Integer> requestedDebitDocIds) {
        if (requestedDebitDocIds == null || requestedDebitDocIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "debitDocIds must not be empty");
        }
        if (requestedDebitDocIds.size() > MAX_BULK_INVOICE_COUNT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maximum of " + MAX_BULK_INVOICE_COUNT + " invoices can be downloaded at once");
        }

        Set<Integer> uniqueDebitDocIds = new LinkedHashSet<>();
        for (Integer debitDocId : requestedDebitDocIds) {
            if (debitDocId == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "debitDocIds must not contain null values");
            }
            uniqueDebitDocIds.add(debitDocId);
        }
        return new ArrayList<>(uniqueDebitDocIds);
    }

    private void validateBulkInvoiceTotalSize(List<BulkInvoicePdf> invoicePdfs) {
        long totalSize = 0L;
        for (BulkInvoicePdf invoicePdf : invoicePdfs) {
            long fileSize;
            try {
                fileSize = Files.size(invoicePdf.path);
            } catch (IOException exception) {
                logger.error("Unable to determine size for invoice PDF {}",
                        invoicePdf.entryName, exception);
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to determine invoice PDF size");
            }
            if (fileSize > MAX_BULK_INVOICE_TOTAL_SIZE_BYTES - totalSize) {
                throw new ResponseStatusException(
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        "Combined invoice PDF size exceeds the maximum of "
                                + MAX_BULK_INVOICE_TOTAL_SIZE_MB + " MB");
            }
            totalSize += fileSize;
        }
    }

    private List<Path> getBulkInvoiceReadPaths(Integer mvnoId) {
        String billDir = clientServiceRepository.findValueByNameAndMvnoId(
                Constants.PATHS.PDF_READ_PATH,
                mvnoId);
        if (billDir == null || billDir.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invoice PDF read path is not configured");
        }

        List<Path> billPaths = new ArrayList<>();
        for (String path : billDir.split(",")) {
            if (!path.trim().isEmpty()) {
                try {
                    billPaths.add(Paths.get(path.trim()).toAbsolutePath().normalize());
                } catch (Exception exception) {
                    logger.warn("Ignoring invalid invoice PDF read path: {}", path);
                }
            }
        }
        if (billPaths.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invoice PDF read path is invalid");
        }
        return billPaths;
    }

    private Path findBulkInvoicePdf(BulkInvoiceDownloadProjection debitDocument, List<Path> billPaths) {
        if (debitDocument.getDocNumber() == null || debitDocument.getDocNumber().trim().isEmpty()) {
            return null;
        }

        for (Path billPath : billPaths) {
            Path customerPath = billPath
                    .resolve(String.valueOf(debitDocument.getCustomerId()))
                    .normalize();
            Path filePath = customerPath
                    .resolve(debitDocument.getDocNumber() + ".pdf")
                    .normalize();
            if (!Objects.equals(filePath.getParent(), customerPath)) {
                logger.warn("Unsafe invoice PDF path rejected for debitDocId {}", debitDocument.getDebitDocId());
                continue;
            }
            if (Files.isRegularFile(filePath) && Files.isReadable(filePath)) {
                return filePath;
            }
        }
        return null;
    }

    private String buildBulkInvoiceZipEntryName(BulkInvoiceDownloadProjection debitDocument) {
        String invoiceNumber = debitDocument.getDocNumber().trim();
        if (invoiceNumber.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            invoiceNumber = invoiceNumber.substring(0, invoiceNumber.length() - 4);
        }
        String safeInvoiceNumber = invoiceNumber
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .replace("..", "_");
        if (safeInvoiceNumber.isEmpty()) {
            safeInvoiceNumber = "invoice";
        }
        return debitDocument.getDebitDocId() + "_" + safeInvoiceNumber + ".pdf";
    }

    private static class BulkInvoicePdf {
        private final Path path;
        private final String entryName;

        private BulkInvoicePdf(Path path, String entryName) {
            this.path = path;
            this.entryName = entryName;
        }
    }

    public ResponseEntity downloadPartnerInvoicePdf(PartnerDebitDocument debitDocument, HttpServletResponse servletResponse) {
        Resource resource = null;
        String billDir;
        Path billPath;

        try {
            String InvoiceNo = debitDocument.getDocnumber() + File.separator + debitDocument.getDocnumber() + ".pdf";
            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

            billDir = clientServiceRepository.findValueByNameAndMvnoId(Constants.PATHS.PDF_READ_PATH, mvnoId);
            List<String> paths = Arrays.asList(billDir.split(","));
            Path filePath = null;
            for (String path : paths) {
                logger.info("Bill Dir is :" + path);
                billPath = Paths.get(path).toAbsolutePath().normalize();
                filePath = billPath.resolve(InvoiceNo).normalize();
                logger.info("Bill Path is :" + filePath.toString());
                resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    break;
                }
            }

            if (!resource.exists()) {
                logger.info("File not found " + InvoiceNo);
                return null;
            }

            PrintWriter out = servletResponse.getWriter();
            servletResponse.setContentType("APPLICATION/OCTET-STREAM");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");

            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());

            int i;
            while ((i = fileInputStream.read()) != -1) {
                out.write(i);
            }
            fileInputStream.close();
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }


    public ResponseEntity downloadTrailInvoicePdf(TrialDebitDocument debitDocument, HttpServletResponse servletResponse) {
        Resource resource = null;
        String billDir;
        Path billPath;
        Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

        try {
            billDir = clientServiceRepository.findValueByNameAndMvnoId("pdfpath", mvnoId);

            String InvoiceNo = debitDocument.getBillrunid() + File.separator + debitDocument.getDocnumber() + ".pdf";
            logger.info("Bill Dir is :" + billDir);
            billPath = Paths.get(billDir).toAbsolutePath().normalize();
            Path filePath = billPath.resolve(InvoiceNo).normalize();
            logger.info("Bill Path is :" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                logger.info("File not found " + InvoiceNo);
                return null;
            }

            PrintWriter out = servletResponse.getWriter();
            servletResponse.setContentType("APPLICATION/OCTET-STREAM");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");

            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());

            int i;
            while ((i = fileInputStream.read()) != -1) {
                out.write(i);
            }
            fileInputStream.close();
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }


    public void generatePdf(com.itextpdf.text.Document doc, Class clazz, List<D> pojoList, Field[] fields) throws Exception {
        try {

            doc.setPageSize(PageSize.A4.rotate());
            doc.open();

            if (fields == null)
                fields = clazz.getDeclaredFields();

            PdfPTable table = new PdfPTable(fields.length);
            table.setWidthPercentage(100);

            String[] columnNames = new ExcelUtil<>().fields(clazz, fields);
            for (int i = 0; i < columnNames.length; i++) {
                generatePdfCell(table, columnNames[i].toUpperCase(), BaseColor.GRAY);
            }

            for (D dto : pojoList) {
                for (int i = 0; i < fields.length; i++) {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, fields[i].getName());
                    Method getter = pd.getReadMethod();
                    //     generatePdfCell(table, getter.invoke(dto), BaseColor.WHITE);
                }
            }
            doc.add(table);
            doc.close();

        } catch (Exception ex) {
            // ApplicationLogger.logger.error("PDF " + ex.getMessage() + ex);
            throw ex;
        }
    }

    public void generatePdfCell(PdfPTable table, Object text, BaseColor backgroundColor) {
        PdfPCell cell = null;

        if (text instanceof Integer || text instanceof Boolean
                || text instanceof LocalDate
                || text instanceof LocalTime
                || text instanceof LocalDateTime
                || text instanceof String) {
            cell = new PdfPCell(new Phrase(text.toString()));
        } else if (text instanceof Double) {
            cell = new PdfPCell(new Phrase(String.valueOf(text)));
        } else if (text instanceof Float) {
            cell = new PdfPCell(new Phrase((Float) text));
        } else if (text instanceof List) {
            cell = new PdfPCell(new Phrase(text.toString()));
        } else {
            cell = new PdfPCell(new Phrase());
        }

        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        table.addCell(cell);
    }


    public boolean generateReceipt(CreditDocument creditDocument) throws RuntimeException {
        logger.debug("[InvoiceUtil]: generateReceipt() called for (DB) " + creditDocument.getId());
        Date startDate = new Date();
        String queryxml = "";
        try {
            String xmlFile;
            String jrsmlfile = null;
            String invoiceNumber;

            String creditDocType = creditDocument.getType().trim();
            String customerId = creditDocument.getCustomer().getId().toString();
            Long buId = creditDocument.getCustomer().getBuId();
            Integer mvnoId = creditDocument.getCustomer().getMvnoId();

            if (buId != null && mvnoId != null)
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndBuidAndMvnoId(creditDocType, buId, mvnoId);
            else
                jrsmlfile = templateRepository.findByTemplatetypeRebillingAndMvnoId(creditDocType, mvnoId);

            if (jrsmlfile == null)
                throw new RuntimeException("Template Not Found to Generate PDF");

            InputStream targetStream = new ByteArrayInputStream(jrsmlfile.getBytes());
            String path = clientServiceRepository.findValueByNameAndMvnoId("paymentpdfpath", mvnoId);

            xmlFile = creditDocument.getXmldocument();

            if (xmlFile == null || xmlFile.equalsIgnoreCase(""))
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Template not Found! add right template", null);
            else {
                try {
                    logger.debug("[" + this.getClass().getName() + "] generateReceipt(): Exported path will be " + path);
                    JasperReport jasperReport = JasperCompileManager.compileReport(targetStream);
                    // Parameters for report
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    Document doc = convertStringToDocument(xmlFile);
                    JRXmlDataSource jrxmlds = new JRXmlDataSource(doc, "/receipt");

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrxmlds);
                    File directory = new File(path + File.separator + creditDocument.getId());
                    if (!directory.exists()) {
                        directory.mkdirs();
                        logger.info("[" + this.getClass().getName() + "] " + path + File.separator + creditDocument.getId() + " created...");
                    }

                    JasperExportManager.exportReportToPdfFile(jasperPrint, new ClassPathResource(path + File.separator + creditDocument.getId() + File.separator + creditDocument.getId() + ".pdf").getPath());
                    Date endDate = new Date();
                    logger.info("[" + this.getClass().getName() + "] " + creditDocument.getId() + ".PDF Generated Successfully Elapsed Time : " + new DateTimeUtil().getElapsedTime(startDate, endDate) + " sec");
                    return true;

                } catch (JRException e) {
                    logger.error("[" + this.getClass().getName() + "] " + e, e);
                    throw new RuntimeException("Payment Receipt generation Fail");

                }
            }
        } catch (Exception e) {
            logger.error("generateReceipt [" + this.getClass().getName() + "] " + e, e.getMessage());
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0"))
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Template not Found! add right template", null);
            else
                throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("generateReceipt method completed.");
        }
    }


    public void downloadReceiptPdf(String paymentid, HttpServletResponse servletResponse) {
        logger.info("[" + this.getClass().getName() + "]: ****** REST download() method called. paymentid :" + paymentid + " ****** ");
        logger.info("In getPaymentReceipt");
        Resource resource = null;
        Path paymentPath;
        String paymentDir;
        Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();

        try {
            paymentDir = clientServiceRepository.findValueByNameAndMvnoId(Constants.PATHS.PAYMENT_PDF_READ_PATH, mvnoId);
            List<String> paths = Arrays.asList(paymentDir.split(","));
            String paymentNo = paymentid + File.separator + paymentid + ".pdf";
            Path filePath = null;
            logger.info("BILL_PATH:" + paymentDir);
            for (String path : paths) {
                paymentPath = Paths.get(path).toAbsolutePath().normalize();
                filePath = paymentPath.resolve(paymentNo).normalize();
                logger.info("payment PATH:" + filePath.toString());
                resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    break;
                }
            }
            if (!resource.exists()) {
                logger.info("File not found " + paymentNo);
                throw new Exception("File not Found");
            }

            PrintWriter out = servletResponse.getWriter();
            servletResponse.setContentType("APPLICATION/OCTET-STREAM");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");
            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
            int i;
            while ((i = fileInputStream.read()) != -1) {
                out.write(i);
            }
            fileInputStream.close();
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            resource = null;
        }
    }


    public Resource getBarterDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();
        String path = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).getValue();
        Resource resource = null;
        try {
            String subFolderName = path + "/" + userName.trim() + "/";
            this.barterDocDir = Paths.get(subFolderName);

            Path filePath = this.barterDocDir.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            String fileUrl = filePath.toUri().toString();
            resource = new UrlResource(new URI(fileUrl));
            if (resource.exists()) {
                return resource;
            }
            if (resource == null) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return resource;
    }
}
