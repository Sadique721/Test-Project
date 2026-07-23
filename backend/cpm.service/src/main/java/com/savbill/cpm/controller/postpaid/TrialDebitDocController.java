package com.savbill.cpm.controller.postpaid;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import com.savbill.cpm.constants.MenuConstants;
import com.savbill.cpm.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.savbill.cpm.controller.base.BaseController;
import com.savbill.cpm.model.postpaid.CustomerAddress;
import com.savbill.cpm.model.postpaid.TrialDebitDocument;
import com.savbill.cpm.pojo.SearchTrialDebitDocs;
import com.savbill.cpm.service.common.FileSystemService;
import com.savbill.cpm.service.postpaid.TrialDebitDocService;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;

@Controller
public class TrialDebitDocController extends BaseController<CustomerAddress>{


	
	private static final String MODEL_DISP_NAME="Trial Invoice";
	private static final String MODEL_URI_NAME="trialinvoice";    
    private static final String RETURN_URI_INDEX="redirect:/trialinvoice/search";
    private static final String RETURN_URI_LIST="postpaid/invoice/trialinvoicelist";
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private TrialDebitDocService entityService;

        
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return CommonUtils.getYesNoStatusMap();
    }
        
    //@PreAuthorize("hasPermission('com.savbill.cpm.controller.common.CustomerAddress', '1')")
    @PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_trial_bill_run_invoice +  "\")")
    @RequestMapping(value = {"/trialinvoice/search/{pageNumber}","/trialinvoice/search"})
    public String list(@PathVariable(required = false) Integer pageNumber,@RequestParam(name="bid",defaultValue="")  String billRunId,@ModelAttribute("entity") SearchTrialDebitDocs entity,  @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	
    	if(entity!=null) {
    		List<TrialDebitDocument> debitDocList=null;
    		if(billRunId!=null && !"".equals(billRunId)) {
    			entity.setBillrunid(Integer.valueOf(billRunId));
    			debitDocList=entityService.searchByBillRunId(billRunId);
    		}else {
    			debitDocList=entityService.getAllEntities(pageNumber, CommonConstants.DB_PAGE_SIZE);
    		}
    		if(debitDocList==null || debitDocList.size() == 0) {
    			model.addAttribute("errorFlash","No results found");
    		}else {
    			entity.setDebitdoclist(debitDocList);
    		}
    	}else {
    		entity = new SearchTrialDebitDocs();
    	} 
//		entity = new SearchDebitDocs();

        model.addAttribute("entity",entity);
        return RETURN_URI_LIST;
    }

    
    
    
    @RequestMapping(value = {"/trialinvoice"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getSearchTrialDebitDocsForInvoice());
        return RETURN_URI_LIST;
    }
    
    @RequestMapping(value = {"/trialinvoice/download/{invoiceid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource>  downloadInvoice(@PathVariable Integer invoiceid,Model model)
    { 	 	   
    	TrialDebitDocument doc = entityService.getById(invoiceid);
    	
    	
 	   	Resource resource = null;              
        FileSystemService service= SpringContext.getBean(FileSystemService.class);
        resource=service.getTrialInvoice(doc.getBillrunid() + File.separator+ doc.getDocnumber() + ".pdf");
        //resource=service.getInvoice("12123");
        String contentType = "application/octet-stream";
        if(resource!=null && resource.exists()) {
        	 return ResponseEntity.ok()
        	 .contentType(MediaType.parseMediaType(contentType))
        	 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        	 .body(resource);        	 
        }else {
        	return ResponseEntity.notFound().build();
        }
    }

}
