package com.savbill.cpm.controller.postpaid;

import com.savbill.cpm.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.savbill.cpm.controller.base.BaseController;
import com.savbill.cpm.model.postpaid.CustomerAddress;
import com.savbill.cpm.model.postpaid.DebitDocument;
import com.savbill.cpm.pojo.SearchDebitDocs;
import com.savbill.cpm.service.common.FileSystemService;
import com.savbill.cpm.service.postpaid.DebitDocService;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

@Controller
public class DebitDocController extends BaseController<CustomerAddress> {


    private static final String MODEL_DISP_NAME = "Invoice";
    private static final String MODEL_URI_NAME = "invoice";
    private static final String RETURN_URI_INDEX = "redirect:/invoice/search";
    private static final String RETURN_URI_LIST = "postpaid/invoice/invoicelist";
    private static final String RETURN_URI_ADD_EDIT = "radius/customers/addrform";
    private static final String SORT_BY_COLUMN = "id";

    @Autowired
    private DebitDocService entityService;


    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap() {
        return CommonUtils.getYesNoStatusMap();
    }

    //@PreAuthorize("hasPermission('com.savbill.cpm.controller.common.CustomerAddress', '1')")
    @RequestMapping(value = {"/invoice/search/{pageNumber}", "/invoice/search"})
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name = "bid", defaultValue = "") String billRunId, @ModelAttribute("entity") SearchDebitDocs entity, @ModelAttribute("flashMsgType") String flashMsgType, @ModelAttribute("flashMsg") String flashMsg, Model model) {
        if (pageNumber == null) {
            pageNumber = 1;
        }

        if (entity != null) {
            List<DebitDocument> debitDocList = null;
            if (billRunId != null && !"".equals(billRunId)) {
                entity.setBillrunid(Integer.valueOf(billRunId));
                debitDocList = entityService.searchByBillRunId(billRunId);
            } else {
                debitDocList = entityService.getAllEntities(pageNumber, CommonConstants.DB_PAGE_SIZE);
            }
            if (debitDocList == null || debitDocList.size() == 0) {
                model.addAttribute("errorFlash", "No results found");
            } else {
                entity.setDebitdoclist(debitDocList);
            }
        } else {
            entity = new SearchDebitDocs();
        }
//		entity = new SearchDebitDocs();

        model.addAttribute("entity", entity);
        return RETURN_URI_LIST;
    }


    @RequestMapping(value = {"/invoice"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        SearchDebitDocs entity = new SearchDebitDocs();
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity", entity);
        return RETURN_URI_LIST;
    }

    @RequestMapping(value = {"/invoice/download/{invoiceid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer invoiceid, Model model) {
        DebitDocument doc = entityService.get(invoiceid);
        Resource resource = null;
        FileSystemService service = SpringContext.getBean(FileSystemService.class);
        resource = service.getInvoice(doc.getBillrunid() + File.separator + doc.getDocnumber() + ".pdf");
        //resource=service.getInvoice("12123");
        String contentType = "application/octet-stream";
        if (resource != null && resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = {"/invoice/revert/{invoiceid}"}, method = RequestMethod.GET)
    public String revertInvoice(@PathVariable Integer invoiceid, Model model) {
        boolean bStatus = false;
        try {
            bStatus = entityService.revertInvoice(String.valueOf(invoiceid));
        } catch (Exception e) {
            e.printStackTrace();
            bStatus = false;
        }

        if (bStatus) {
            model.addAttribute("successFlash", "Invoice Reverted sucessfully");
        } else {
            model.addAttribute("errorFlash", "Error Processing Request, Please try after sometime...");
        }
        SearchDebitDocs entity = new SearchDebitDocs();
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity", entity);
        return RETURN_URI_LIST;
    }
}
