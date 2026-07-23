package temp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import commons.CommonGetAPI;
import commons.CommonList;
import customer.CustomerPaymentDetails;
import customer.PrepaidCustomer;
import staff.Login;
import utility.Utility;


public class ReadJsonFile {

	public static void main(String args[]) throws IOException {
		
	
	System.out.println("Execution start...!");	
	
	try {
		validate(13);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//testCommonGetAPI();
	//testCommonListAPI();
	
	
	System.out.println("Execution Completed...!");	
				
	}
	
	private static void testCommonGetAPI() throws Exception {
		
		Login login = new Login();
		login.setAuthBearer();
		
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		
		//int ans = commonGetAPI.getChargeId("mahinDRA");
		//String ans = commonGetAPI.getProductAndProductCategoryDetails("nokia1");
		
		//List<Integer> ans = commonGetAPI.getInvestmentCodeIdList("ICP,IPTV,DTV,FTTH,IC");
		//,IPTV,DTV,FTTH,IC
		//List<Integer> ans = commonGetAPI.getTeamIdList("Child_Team,savbillt_QA_team");
		
		//List<Integer> ans = commonGetAPI.getRoleId("ADMIN,Product/partner");
		//List<Integer> ans = commonGetAPI.getRoleId("all");
		//List<Integer> ans = commonGetAPI.getReasonCategoryIdList("Test_Sale,Channel missing");
		//List<Integer> ans = commonGetAPI.getReasonCategoryIdList("alL");
		//List<Integer> ans = commonGetAPI.getProductCategoryIdListForCustomerBind("DTV_CARD,DTV_stb");
		//List<Integer> ans = commonGetAPI.getProductCategoryIdListForCustomerBind("alL");
		//List<Integer> ans = commonGetAPI.getTeamIdListBasedOnAttchedStaff("Child_team,T3,t1");
		//List<Integer> ans = commonGetAPI.getTeamIdListBasedOnAttchedStaff("alL");
		//List<Integer> ans = commonGetAPI.getBranchIdList("alL");
		//List<Integer> ans = commonGetAPI.getBranchIdList("branch11,Branch12");
		//List<Integer> ans = commonGetAPI.getRegionIdList("alL");
		//List<Integer> ans = commonGetAPI.getRegionIdList("master25,Region11f");
		//List<Integer> ans = commonGetAPI.getBusinessUnitIdList("alL");
		//List<Integer> ans = commonGetAPI.getBusinessUnitIdList("Buisness_66,buisness_22");
		//List<Integer> ans = commonGetAPI.getBusinessUnitIdList("alL");
		//List<Integer> ans = commonGetAPI.getBusinessUnitIdList("Buisness_66,buisness_22");
		
		//List<Integer> ans = commonGetAPI.getServiceIdList("alL");
		//List<Integer> ans = commonGetAPI.getServiceIdList("sERVice11,serviceMang_INdia1");
		
		//List<Integer> ans = commonGetAPI.getServiceAreaIdList("");
		//List<Integer> ans = commonGetAPI.getServiceAreaIdList("zulfinserviceAREA,AMT300");
		
		//map = {465=1:5, 466=, 216=, 9=, 78=}
		
		//int taxId = commonGetAPI.getTaxId("");
		
		int ans = commonGetAPI.getCustomerId("Rekha5","Prepaid");
		System.out.println(ans);	
		
	}
	
	
	private static void testCommonListAPI() throws Exception {
		
		Login login = new Login();
		login.setAuthBearer();
		
		CommonList commonList = new CommonList();
		String ans = commonList.getCommonChargeType("One time1");
		//String ans = commonList.getCommonChargeCategory("ServiceCHARGE");
		//String ans = commonList.getCommonPlanGroup("renEW");
		//String ans = commonList.getCommonPlanAccessibility("boTh");
		//String ans = commonList.getCommonPaymentMode("CASH");
		
		//String ans = commonList.getCommonPlanCategory("busiNess promotion");
		System.out.println(ans);	
		
	}
	
	public static void validate(int age) throws Exception  {  
        if(age<18) {  
            //throw Arithmetic exception if not eligible to vote  
            throw new Exception("Person is not eligible to vote");    
        }  
        else {  
            System.out.println("Person is eligible to vote!!");  
        }  
    }  


}
