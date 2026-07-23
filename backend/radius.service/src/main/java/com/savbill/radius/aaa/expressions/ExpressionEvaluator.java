package com.savbill.radius.aaa.expressions;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.server.MD5Util;
import com.savbill.radius.aaa.server.RadiusUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ExpressionEvaluator.class);

    public static String getValueFromGivenExpression(String expression, CustomerData custRetrunData, RadiusPacket request) throws Exception {
        try {
            RadiusUtility radUtil = new RadiusUtility();
            String dynaValue=expression;
            if(expression.startsWith("{") && expression.endsWith("}")) {
                dynaValue=removeFirstandLast(expression);
            }
            log.debug("Dynamic Profile Attribute  is:"+dynaValue+":Dynamic Radius Attribute  is :"+expression);
            if(dynaValue.startsWith("SUBSTRING")) {
                StringBuilder sb = new StringBuilder(dynaValue);
                sb.delete(0,10);
                dynaValue=sb.toString();
                log.debug("Functional Value after Remove Output is:"+dynaValue);
                String[] sampleSpli = dynaValue.split(",");
                expression=sampleSpli[2];
                if(expression.startsWith("{") && expression.endsWith("}")) {
                    String dynaValueAtt=removeFirstandLast(expression);
                    int start=Integer.parseInt(sampleSpli[0]);
                    int end=Integer.parseInt(sampleSpli[1]);
                    dynaValue=request.getAttribute(dynaValueAtt).getAttributeValue();
                    dynaValue=dynaValue.substring(start,end);
                    log.debug("Final Dynaimc Value Searaching Value:"+dynaValue);
                } else {
                    int start=Integer.parseInt(sampleSpli[0]);
                    int end=Integer.parseInt(sampleSpli[1]);
                    dynaValue=expression;
                    dynaValue=dynaValue.substring(start,end);
                    log.debug("Final Dynaimc Value Searaching Value:"+dynaValue);
                }
                return dynaValue;
            }
            else if(dynaValue.startsWith("SEPERATE")) {
                StringBuilder sb = new StringBuilder(dynaValue);
                sb.delete(0,9);
                dynaValue=sb.toString();
                log.debug("Functional Value after Remove Output is:"+dynaValue);
                String[] sampleSpli = dynaValue.split(",");
                expression=sampleSpli[2];
                if(expression.startsWith("{") && expression.endsWith("}")) {
                    String dynaValueAtt=removeFirstandLast(expression);
                    int start=Integer.parseInt(sampleSpli[0]);
                    int end=request.getAttribute(dynaValueAtt).getAttributeValue().indexOf(sampleSpli[1]);
                    dynaValue=request.getAttribute(dynaValueAtt).getAttributeValue();
                    log.debug(":dynaValue:"+dynaValue+":Start:"+start+":end:"+end);
                    if(end>1) {
                        dynaValue=dynaValue.substring(start,end);
                    }
                    log.debug("Final Dynaimc Value Searaching Value:"+dynaValue);
                } else {
                    int start=Integer.parseInt(sampleSpli[0]);
                    int end=dynaValue.indexOf(sampleSpli[1]);
                    dynaValue=expression;
                    dynaValue=dynaValue.substring(start,end);
                    log.debug("Final Dynaimc Value Searaching Value:"+dynaValue);
                }
                return dynaValue;
            }
            else if(dynaValue.startsWith("PROFILE")) {
                StringBuilder sb = new StringBuilder(dynaValue);
                sb.delete(0,7);
                dynaValue=sb.toString();
                log.debug("Functional Value after Remove Output is:"+dynaValue);
                if(dynaValue.startsWith("{") && dynaValue.endsWith("}")) {
                    String dynaValueAtt=removeFirstandLast(dynaValue);
                    String dynaReplyValue=null;
                    if(custRetrunData!=null) {
                        if(expression.equalsIgnoreCase("HW-User-Password") && dynaValue.contains("MD5")) {
                            if(dynaValue.startsWith("{") && dynaValue.endsWith("}"))
                                dynaValue=removeFirstandLast(dynaValue);
                            String dynaAttribute=ExpressionEvaluator.parseExpression(dynaValue, "MD5\\{(.*)\\}");//dynaValue.substring(4);
                            if(dynaAttribute.charAt(dynaAttribute.length()-1) == '}' && dynaAttribute.charAt(0) != '{') {
                                dynaAttribute = dynaAttribute.substring(0, dynaAttribute.length()-1);
                            }
                            dynaReplyValue=radUtil.getDynamicReplyValue(dynaAttribute,custRetrunData,null);
                            log.debug("Decoded Password: "+dynaReplyValue);
                            dynaReplyValue = MD5Util.getMD5Hash(dynaReplyValue);
                            log.debug("Encoded Password: "+dynaReplyValue);
                        } else {
                            dynaReplyValue=radUtil.getDynamicReplyValue(dynaValueAtt,custRetrunData,null);
                        }
                        log.debug("Final Dynaimc Value Searaching Value:"+dynaReplyValue);
                    } else {
                        dynaReplyValue=null;
                    }
                    return dynaReplyValue;
                }
            }
            else if (dynaValue.startsWith("REQ")) {
                String dynaAttribute=dynaValue.substring(4);
                StringBuilder sb = new StringBuilder(dynaAttribute);
                sb.deleteCharAt(dynaAttribute.length() - 1);
                dynaAttribute=sb.toString();
                if(dynaAttribute.charAt(dynaAttribute.length()-1) == '}' && dynaAttribute.charAt(0) != '{') {
                    dynaAttribute = dynaAttribute.substring(0, dynaAttribute.length()-1);
                }
                if(request.getAttribute(dynaAttribute)!=null) {
                    dynaValue=request.getAttribute(dynaAttribute).getAttributeValue();
                }
                else {
                    dynaValue=null;
                }
                return dynaValue;
            }
            else if(dynaValue.startsWith("EXP")) {
                String dynaReplyValue = "";
                try {
                    dynaReplyValue = getValueFromExpression(dynaValue,custRetrunData, radUtil, request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(dynaReplyValue!=null) {
                    if(dynaReplyValue!=null && !dynaReplyValue.equalsIgnoreCase("NA")) {

                        if(dynaReplyValue!=null) {
                            if(dynaReplyValue.contains(".") && isDouble(dynaReplyValue))
                                dynaReplyValue = dynaReplyValue.split("\\.")[0];
                            return dynaReplyValue;
                        }
                    }
                } else {
                    log.debug("dynaReplyValue is null for EXP: "+expression);
                }
            } else if(expression.startsWith("{") && expression.endsWith("}")) {
                log.debug("Attribute:"+expression+":Value:"+request.getAttribute(dynaValue)+":dynaValue:"+dynaValue);
                if(request.getAttribute(dynaValue)!=null) {
                    return request.getAttribute(dynaValue).getAttributeValue();
                }
            }
        } catch (Exception ex) {
            log.error("Exception to get value from expression: "+expression+" exception: "+ex.getMessage());
        }
        return expression;
    }


    public static String getValueFromExpression(String expression, CustomerData customerData, RadiusUtility radUtil, RadiusPacket accessRequest) throws Exception {
        String dynaReplyValue = "";
        try {
            if (expression.contains("EXP")) {
                log.debug("In Get Value from Expression: "+expression);
                // Extract content within EXP{...}
                String expContent = ExpressionEvaluator.parseExpression(expression, "EXP\\{(.*)\\}");
//                if(expContent.charAt(expContent.length()-1) == '}' && expContent.charAt(0) != '{') {
//                    expContent = expContent.substring(0, expContent.length()-1);
//                }
                // Split content by ',' to handle multiple expressions
                String[] exps = expContent.split(",");

                for (String str : exps) { //{EXP{VAR{Calling-Station-Id},MERGE{-pppoe}}}
                    if (str.contains("MATH")) {
                        double calValue = getMathExpression(str, customerData, accessRequest, radUtil);
                        int calVal = (int) calValue;
                        dynaReplyValue = dynaReplyValue + calVal;
                    } else if(str.contains("VAR") || str.contains("PROFILE")) {
                        String dynaAttribute = str;
                        if(str.contains("VAR"))
                            dynaAttribute=ExpressionEvaluator.parseExpression(str, "VAR\\{(.*)\\}");//str.substring(4);
                        else if(str.contains("PROFILE"))
                            dynaAttribute=ExpressionEvaluator.parseExpression(str, "PROFILE\\{(.*)\\}");//str.substring(4);
//                    StringBuilder sb = new StringBuilder(dynaAttribute);
//                    sb.deleteCharAt(dynaAttribute.length() - 1);
//                    dynaAttribute=sb.toString();
                        if(dynaAttribute.startsWith("{"))
                            dynaAttribute=dynaAttribute.substring(1);
                        if(dynaAttribute.endsWith("}"))
                            dynaAttribute=dynaAttribute.substring(0,dynaAttribute.length()-1);
                        String replyValue = radUtil.getDynamicReplyValue(dynaAttribute, customerData, accessRequest);
                        if(replyValue != null)
                            dynaReplyValue = dynaReplyValue + replyValue;
                    } else if (str.contains("MERGE")) {
                        dynaReplyValue = dynaReplyValue.concat(getMergeExpression(str));
                    }else if (str.contains("REQ")) { //EXP{REQ{Calling-Station-Id},MERGE{-clips}}
                        String dynaAttribute=str.substring(4);
                        StringBuilder sb = new StringBuilder(dynaAttribute);
//                    sb.deleteCharAt(dynaAttribute.length() - 1);
                        dynaAttribute=sb.toString();
                        if(dynaAttribute.charAt(dynaAttribute.length()-1) == '}' && dynaAttribute.charAt(0) != '{') {
                            dynaAttribute = dynaAttribute.substring(0, dynaAttribute.length()-1);
                        }
                        dynaReplyValue=dynaReplyValue + accessRequest.getAttribute(dynaAttribute).getAttributeValue();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Exception to get value from EXP expression: "+expression+" exception: "+ex.getMessage());
        }
        log.debug("Expression: "+expression+" Result: "+dynaReplyValue);
        return dynaReplyValue;
    }

    public static double getMathExpression(String expression, CustomerData customerData, RadiusPacket request, RadiusUtility radUtil) throws Exception {
        expression = ExpressionEvaluator.parseExpression(expression, "MATH\\[(.*)\\]");
        return ExpressionEvaluator.evaluateExpression(expression, customerData, request, radUtil);
    }

    public static String getMergeExpression(String expression) {
        expression = expression.replaceAll("MERGE","");
        expression = removeFirstandLast(expression);
        return expression;
    }

    public static String removeFirstandLast(String str)
    {
        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(str.length() - 1);
        sb.deleteCharAt(0);
        return sb.toString();
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String replaceVariables(String component, CustomerData customerData, RadiusPacket request, RadiusUtility radUtil) throws Exception {
        Pattern varPattern = Pattern.compile("VAR\\{(\\w+)\\}");
        Matcher matcher = varPattern.matcher(component);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            Double value = Double.valueOf(radUtil.getDynamicReplyValue(variable, customerData, request));
            if (value == null) {
                throw new Exception("Variable " + variable + " not found");
            }
            matcher.appendReplacement(sb, value.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static double evaluateExpression(String expression, CustomerData customerData, RadiusPacket request, RadiusUtility radUtil) throws Exception {
        String expressionWithVariables = replaceVariables(expression, customerData, request, radUtil);
        return eval(expressionWithVariables);
    }

    public static String parseExpression(String expression, String regex) throws Exception {
        try {
            Pattern pattern = Pattern.compile(regex);//"EXP\\{MATH\\[(.*)\\]\\}" , //"EXP\\{MATH\\[(.*)\\]\\}"
            Matcher matcher = pattern.matcher(expression);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Exception("Invalid expression format for regex: "+regex+" expression: "+expression);
            }
        } catch (Exception ex) {
//            throw new Exception();
            log.error("Invalid expression format for regex: "+regex+" expression: "+expression+" ex: "+ex.getMessage());
            return null;
        }
    }

    public static double eval(String expression) throws ScriptException {
        // Create a script engine manager
        ScriptEngineManager manager = new ScriptEngineManager();
        // Get the JavaScript engine
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // Evaluate the expression
        Object result = engine.eval(expression);
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        } else {
            throw new ScriptException("Invalid result type: " + result.getClass().getName());
        }
    }
}
