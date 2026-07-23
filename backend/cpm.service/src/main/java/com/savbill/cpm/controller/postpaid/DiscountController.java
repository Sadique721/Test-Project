package com.savbill.cpm.controller.postpaid;

import java.util.List;
import java.util.TreeMap;

import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.controller.base.BaseController;
import com.savbill.cpm.model.postpaid.Discount;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.service.postpaid.DiscountService;
import com.savbill.cpm.service.postpaid.PostpaidPlanService;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;

@Controller
public class DiscountController extends BaseController<Discount>{


	
	private static final String MODEL_DISP_NAME="Discount";
	private static final String MODEL_URI_NAME="disc";    
    private static final String RETURN_URI_INDEX="redirect:/disc/1";
    private static final String RETURN_URI_LIST="postpaid/disc/disclist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/disc/discform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private DiscountService entityService;
	@Autowired
	CreateDataSharedService createDataSharedService;

    @Autowired
    private PostpaidPlanService planService;
    
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return CommonUtils.getYesNoStatusMap();
    }
    
    @ModelAttribute("discTypeMap")
    TreeMap<String, String> getDiscountTypeMap(){
    	return CommonUtils.getDiscTypeMap();
    }
    
    @ModelAttribute("planList")
    List<PostpaidPlan> getPlanList(){
    	return planService.getAllActiveEntities(Constants.ALL,Constants.ALL);
    }
    
    @RequestMapping(value = {"/disc/{pageNumber}","/disc"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
  
    	Page<Discount> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/disc/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getDiscountForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/disc/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) throws Exception{
        model.addAttribute("entity", entityService.getDiscountForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/disc/save", method = RequestMethod.POST)
    public String save(Discount bean,final RedirectAttributes ra) {
    	
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
    	
    		Discount save = entityService.saveDiscount(bean);
			createDataSharedService.sendEntitySaveDataForAllMicroService(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Discount Added Successfully";
	        	}else{
	        		flashMsg="Discount Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	
    	if(operation.equals("add")) {
    		//Automatically open edit when adding new entity
    		return "redirect:/disc/edit/"+bean.getId();
    	}else {
	        ra.addFlashAttribute("flashMsg", flashMsg);
	        ra.addFlashAttribute("flashMsgType", flashMsgType);
	        return RETURN_URI_INDEX;
	        
    	}
    }

    @RequestMapping("/disc/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
    	entityService.deleteDiscount(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }

    @RequestMapping(path = {"/disc/addmapping"})
    public 	String addSlab(Discount entity, Model model) {
    	if(entity.getDiscMappingList()==null) {
    		entity.setDiscMappingList(entityService.getDiscountMappingList());
    	}    	
    	entity.getDiscMappingList().add(entityService.getDiscountMapping());
    	model.addAttribute("entity", entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
    	return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping(path = {"/disc/removemapping"},params = "removeindex")
    public 	String deleteSlab(Discount entity, @RequestParam("removeindex") int index,Model model) {
    	model.addAttribute("entity", entityService.deleteSlab(entity, index));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    } 

    @RequestMapping(path = {"/disc/addplan"})
    public 	String addTier(Discount entity, Model model) {
    	if(entity.getPlanMappingList()==null) {
    		entity.setPlanMappingList(entityService.getDiscountPlanMappingList());
    	}    	
    	entity.getPlanMappingList().add(entityService.getDiscountPlanMapping());
    	model.addAttribute("entity", entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);

    	return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping(path = {"/disc/removeplan"},params = "removeindex")
    public 	String deleteTier(Discount entity, @RequestParam("removeindex") int index,Model model) {
    	model.addAttribute("entity", entityService.deleteTier(entity, index));
        model.addAttribute("pageuri", MODEL_URI_NAME);

    	return RETURN_URI_ADD_EDIT;
    } 
}
