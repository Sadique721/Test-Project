import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.savbill.radius.aaa.data.TimepolicyData;


public class test {
	public static void main(String args[]) {
		
		/*
		byte[] encryptedPass=new byte[16];
		Double usedQuota=(double) (1000000000*4d)*2d;
    	Double usedTime=(double)(1000000000*4d)*2d;
    	//		System.out.println("Upload"+usedQuota+":UsedTime:"+usedTime);
    	 DecimalFormat df = new DecimalFormat("#");
         df.setMaximumFractionDigits(8);
         //		System.out.println(df.format(usedQuota/1000000)+":"+df.format(usedTime));
    	if(!"hi".equalsIgnoreCase("sourceipaddress") && !"hi".equalsIgnoreCase("custid")) {
    		//		System.out.println("in if");
    	}
	    */
		
	    test testI=new test();
	    String attributeStr="INTERNE-01({p1}*1000,{p1}*1000)";	
	    List<String> strList = Arrays.asList(attributeStr.split("\""));
        StringBuilder resultStr = new StringBuilder();
        boolean bAttrbValFound=true;
        for (String str : strList) {
          if (str.length() != 0) {
                String dyanaAttrName = null;
                //		System.out.println("Intial "+str);
                String dynaReplyValue = null;
                while (str.contains("{") && str.contains("}")) {
                    dyanaAttrName = null;
                    dynaReplyValue = null;
                    dyanaAttrName = testI.getDynamicAttributeName(str);
                    
                    String dynamicVar = "{" + dyanaAttrName + "}";
                    //		System.out.println("Last"+dynamicVar);
                    	dynaReplyValue = "120";
                    	
                    if (dyanaAttrName != null && dynaReplyValue != null)
                        str = str.replace(dynamicVar, dynaReplyValue);
                    
                    //		System.out.println("Dyanmic String In loop:" + str + ",bAttrbValFound:"  + bAttrbValFound);
                }
          }
          
          if(bAttrbValFound) {
              try {
                  if (str.contains("(") || str.contains(")") || str.contains("/") || str.contains("*") || str.contains("+") || str.contains("-")) {
                	  //		System.out.println("Found Operaton:"+str);
                      ScriptEngineManager manager = new ScriptEngineManager();
                      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                      resultStr.append(engine.eval(str));
                	  //		System.out.println("Found Data Operaton:"+resultStr);
                  } else {
                      resultStr.append(str);
                  }
              } catch (ScriptException e) {
              	e.printStackTrace();
              	resultStr.append(str);
              }
          }else {
          	resultStr=null;
          }
        }
        //		System.out.println("resultStr:" + resultStr 	);
	}
	
                
    public String getDynamicAttributeName(String str) {
        int openIdx = str.indexOf("{");
        int closeIdx = str.indexOf("}");
        String attrStr = (String) str.subSequence(openIdx + 1, closeIdx);
        return attrStr;
    }

	private TimepolicyData verifyslab(HashMap hmPolicy,int currentone) {
	    Iterator it = hmPolicy.entrySet().iterator();

	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	    	TimepolicyData tbp=(TimepolicyData) pair.getValue();
	    	//		System.out.println("Check From:"+tbp.getFromNumber()+":To:"+tbp.getToNumber()+":with:"+currentone);
	    	if(tbp.getFromNumber()<tbp.getToNumber()) {
		    	if(tbp.getFromNumber()<=currentone && tbp.getToNumber()>=currentone) {
		    		return tbp;
		    	}
	    	}else {
	    		//Check From:31000:To:11900:with:41000

		    	if(tbp.getFromNumber()<=currentone && !(tbp.getToNumber()>=currentone)) {
		    		return tbp;
		    	}
	    	}
	    } 
	    return null;
	}
	
	private int timepolicytoformula(String fromDay,String fromTime) {
		int i=0;
		String basic;
		switch (fromDay.toLowerCase()) {
	    case "sunday":
	    	basic="1";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "monday":
	    	basic="2";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "tuesday":
	    	basic="3";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "wednesday":
	    	basic="4";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "thursday":
	    	basic="5";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "friday":
	    	basic="6";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
	    case "saturday":
	    	basic="7";
	    	fromTime=fromTime.replace(":","");
	    	basic=basic+fromTime;
	    	i=Integer.parseInt(basic);
	    	return i;
		}
		return i;
	}
}
