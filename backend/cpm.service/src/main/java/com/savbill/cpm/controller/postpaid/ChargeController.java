package com.savbill.cpm.controller.postpaid;

import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.savbill.cpm.controller.base.BaseController;
import com.savbill.cpm.model.postpaid.Charge;
import com.savbill.cpm.model.postpaid.Tax;
import com.savbill.cpm.service.postpaid.ChargeService;
import com.savbill.cpm.service.postpaid.TaxService;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;

@Controller
public class ChargeController extends BaseController<Charge>{


    private static final String MODEL_DISP_NAME="Charge";
    private static final String MODEL_URI_NAME="charge";
    private static final String RETURN_URI_INDEX="redirect:/charge/1";
    private static final String RETURN_URI_LIST="postpaid/charge/chargelist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/charge/chargeform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private TaxService taxService;

    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
        return CommonUtils.getYesNoStatusMap();
    }
    
    @ModelAttribute("chargeTypeMap")
    TreeMap<String, String> getChargeTypeMap(){
        return CommonUtils.getChargeTypeMap();
    }
        
    @ModelAttribute("taxMap")
    List<Tax> getTaxList(){
        return taxService.getAllActiveEntities();
    }
    
    @RequestMapping(value = {"/charge/{pageNumber}","/charge"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

        if(pageNumber==null) {
            pageNumber=1;
        }

        Page<Charge> page =null;
        if(search!=null && !"".equalsIgnoreCase(search)){
            page = chargeService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
        }else{
            page = chargeService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
        }
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
        setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/charge/add")
    public String add(Model model) {
        model.addAttribute("entity", chargeService.getChargeForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/charge/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", chargeService.getChargeForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/charge/save", method = RequestMethod.POST)
    public String save(Charge bean,final RedirectAttributes ra) {

        String operation="edit";
        String flashMsg="";
        String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;

        try{
            if(bean !=null && bean.getId()==null){
                operation="add";
//	    		bean.setCreatedById(getLoggedInUserId());
            }else {
//	    		bean.setLastModifiedById(getLoggedInUserId());
            }
            Charge save = chargeService.saveCharge(bean);
            if(save !=null){
                flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
                if(operation.equalsIgnoreCase("add")){
                    flashMsg="Charge Added Successfully";
                }else{
                    flashMsg="Charge Updated Successfully";
                }
            }else{
                flashMsg="Error Performing operation, Please try after sometime !!!";
            }
        }catch(Exception e){
            flashMsg="error";
        }

        ra.addFlashAttribute("flashMsg", flashMsg);
        ra.addFlashAttribute("flashMsgType", flashMsgType);
        return RETURN_URI_INDEX;
    }

    @RequestMapping("/charge/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
        chargeService.deleteCharge(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
}
