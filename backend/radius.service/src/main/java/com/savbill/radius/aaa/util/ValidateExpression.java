package com.savbill.radius.aaa.util;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.expressions.ExpressionEvaluator;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.server.RadiusUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidateExpression {
    private static final Logger log = LoggerFactory.getLogger(ValidateExpression.class);

    public boolean checkExpression(String strExpression, RadiusPacket request, CustomerData custReturnData) {
        if (custReturnData != null && custReturnData.getEventName() != null) {
            return checkExpression(strExpression, request, custReturnData, custReturnData.getEventName());
        }
        return checkExpression(strExpression, request, custReturnData, null);
    }

    public boolean checkExpression(String strExpression, RadiusPacket request, CustomerData custReturnData, String eventName) {
        boolean blnReply = false;
        //check for strExpression null and Event is null
        if ((strExpression == null || strExpression.isEmpty())) {//&& (eventName == null || eventName.isEmpty())){
            return true;
        }
//		if(eventName != null && !eventName.equalsIgnoreCase("")) {
//			blnReply = false;
//		}

        //$volumebasedunusedquota<=0
        Pattern pattern = Pattern.compile("[,=]");
        try {
            log.debug("Pattern" + pattern + ":Expression:" + strExpression);

            if (strExpression.contains("&&") || strExpression.contains("||")) {
                return validateMultiplExpression(strExpression, request, custReturnData, eventName);
            }
            HashMap regMap = new HashMap();
            if (strExpression.contains("CONTAINS") || strExpression.contains("ISEXISTS") || strExpression.contains("ISNOTEXISTS")) {
                regMap.put(strExpression, "");
            } else {
                String[] words = pattern.split(strExpression);
                for (int j = 0; j < words.length; j++) {
                    if (j % 2 == 0 && j <= (words.length - 2)) {
                        regMap.put(words[j], words[j + 1]);
                    }
                }
            }
            Iterator it = regMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                log.debug(pair.getKey() + " = " + pair.getValue());
                if (pair.getKey().toString() != null) {

                    log.debug(":Key:" + pair.getKey().toString() + ":Value:" + pair.getValue());
                    if (pair.getKey().toString().startsWith("{") && pair.getKey().toString().endsWith("}")) {
                        String value = pair.getValue().toString();
                        if (value.startsWith("{") && value.endsWith("}")) {
                            value = ExpressionEvaluator.getValueFromGivenExpression(value, custReturnData, request);
                        }
                        String key = pair.getKey().toString();
                        key = ExpressionEvaluator.getValueFromGivenExpression(key, custReturnData, request);
                        if (key != null && !key.isEmpty() && (key.contains(value) || value.equals("*"))) {
                            log.debug(pair.getKey() + " is matched with Value: " + value);
                            return true;
                        } else {
                            log.debug(pair.getKey() + " is NOT matched give dynaReplyValue :" + value);
                            return false;
                        }

                    } else if (pair.getKey().toString().startsWith("{") && pair.getKey().toString().endsWith("!")) {
                        String value = pair.getValue().toString();
                        if (value.startsWith("{") && value.endsWith("}")) {
                            value = ExpressionEvaluator.getValueFromGivenExpression(value, custReturnData, request);
                        }
                        String key = pair.getKey().toString();
                        key = key.substring(0, key.length() - 1);
                        key = ExpressionEvaluator.getValueFromGivenExpression(key, custReturnData, request);
                        if (key != null && !key.isEmpty() && !key.equals(value)) {
                            log.debug(pair.getKey() + " is matched with Value: " + value);
                            return true;
                        } else {
                            log.debug(pair.getKey() + " is NOT matched give dynaReplyValue :" + value);
                            return false;
                        }

                    } else if (pair.getKey().toString().startsWith("$")) {
                        if (custReturnData != null) {
                            boolean checkWithGreater = false;
                            boolean checkWithLess = false;
                            RadiusUtility radUtil = new RadiusUtility();
                            String dynamicKey = pair.getKey().toString().substring(1);
                            if (pair.getKey().toString().contains("<")) {
                                checkWithLess = true;
                                dynamicKey = dynamicKey.replace("<", "");
                            } else if (pair.getKey().toString().contains(">")) {
                                checkWithGreater = true;
                                dynamicKey = dynamicKey.replace(">", "");
                            }

                            String dynaReplyValue = radUtil.getDynamicReplyValue(dynamicKey, custReturnData, request);
                            if (dynaReplyValue != null) {
                                if (checkWithGreater) {
                                    if (Double.parseDouble(dynaReplyValue) > Double.parseDouble(pair.getValue().toString())) {
                                        log.debug(pair.getKey() + " is matched. Customer Profile");
                                        return true;
                                    } else {
                                        log.debug(pair.getKey() + " is NOT matched give dynaReplyValue Null");
                                        return false;
                                    }
                                } else if (checkWithLess) {
                                    if (Double.parseDouble(dynaReplyValue) < Double.parseDouble(pair.getValue().toString())) {
                                        log.debug(pair.getKey() + " is matched. Customer Profile");
                                        return true;
                                    } else {
                                        log.debug(pair.getKey() + " is NOT matched give dynaReplyValue Null");
                                        return false;
                                    }
                                } else if (dynaReplyValue.contains(pair.getValue().toString())) {
                                    log.debug(pair.getKey() + " is matched. Customer Profile");
                                    return true;
                                } else {
                                    log.debug(pair.getKey() + " is NOT matched give dynaReplyValue Null");
                                    return false;
                                }
                            } else {
                                log.debug(pair.getKey() + " is NOT matched. Customer Profile");
                                return false;
                            }
                        } else {
                            log.debug(pair.getKey() + " is NOT matched give $ and Customer Profile Null");
                            return false;
                        }
                    } else if (pair.getKey().toString().startsWith("#") && eventName != null) { //Check for eventName
                        String event = pair.getValue().toString();
                        if (event != null) {
                            event = event.trim(); //SUP-1663: after space event is not matched.
                            if (event.equalsIgnoreCase(eventName)) {
                                log.debug(pair.getKey() + " is matched. Event");
                                return true;
                            } else {
                                log.debug(pair.getKey() + " is NOT matched with Event");
                                return false;
                            }
                        } else {
                            log.debug(pair.getKey() + " is NOT matched. Event");
                            return false;
                        }
                    } else if (pair.getKey().toString().startsWith("CONTAINS") && request != null) {
                        //EX: CONTAINS{REQ{User-Name},REGEX{\b(?:[0-9]{1,3}\.){3}[0-9]{1,3}\b}}
                        String expression = pair.getKey().toString();
                        log.debug("DATA Key:" + expression);
                        expression = ExpressionEvaluator.parseExpression(expression, "CONTAINS\\{(.*)\\}");
                        if (expression.contains("REGEX")) {
                            String[] expPair = expression.split(",REGEX");
                            String expKey = expPair[0];
                            String expValue = expPair[1];
                            return checkExpressionValidOrNot(expKey, expValue, request, custReturnData, true);
                        } else {
                            String[] expPair = expression.split(",");
                            String expKey = expPair[0];
                            String expValue = expPair[1];
                            return checkExpressionValidOrNot(expKey, expValue, request, custReturnData, false);
                        }
                    } else if (pair.getKey().toString().startsWith("ISEXISTS") && request != null) {    // check key exists
                        String expression = ExpressionEvaluator.parseExpression(pair.getKey().toString(), "ISEXISTS\\{(.*)\\}");
                        log.info("ISEXISTS Expression: " + expression);
                        expression = ExpressionEvaluator.getValueFromGivenExpression(expression, custReturnData, request);
                        log.info("ISEXISTS Expression: " + expression + " value: " + expression);
                        return expression != null;
                    } else if (pair.getKey().toString().startsWith("ISNOTEXISTS") && request != null) {    // check key not exists
                        String expression = ExpressionEvaluator.parseExpression(pair.getKey().toString(), "ISNOTEXISTS\\{(.*)\\}");
                        log.info("ISNOTEXISTS Expression: " + expression);
                        expression = ExpressionEvaluator.getValueFromGivenExpression(expression, custReturnData, request);
                        log.info("ISNOTEXISTS Expression: " + expression + " value: " + expression);
                        return expression == null;
                    } else if (!pair.getKey().toString().startsWith("#") && request != null) {
                        String key = getValueFromRequest(pair.getKey().toString(), request);
                        String value = pair.getValue().toString();
                        return checkExpressionValidOrNot(key, value, request, custReturnData, false);
                    }

                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Error while performing operation expression: " + strExpression, e);
            return false;
        }
        return blnReply;
    }


    public boolean validateMultiplExpression(String expression, RadiusPacket request, CustomerData custReturnData, String eventName) {
        //1. (exp1 && exp2)
        //2.(exp1 || exp2)
        //3.(exp1 && exp2 || exp3)
        // Remove all spaces for easier processing
        expression = expression.replaceAll("\\s+", "");

//		if(expression.startsWith("{") && expression.endsWith("}")){
//			expression = ExpressionEvaluator.removeFirstandLast(expression);
//		}

        // Split the expression based on operators
        String[] tokens = expression.split("(?<=&&)|(?=&&)|(?<=\\|\\|)|(?=\\|\\|)");

        // Stack to evaluate the expression
        Stack<Boolean> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.equals("&&") || token.equals("||")) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    boolean val2 = values.pop();
                    boolean val1 = values.pop();
                    String op = operators.pop();
                    values.push(applyOperator(val1, val2, op));
                }
                operators.push(token);
            } else {
                // It's an expression, so check it
                boolean result = checkExpression(token, request, custReturnData, eventName);
                values.push(result);
            }
        }

        while (!operators.isEmpty()) {
            boolean val2 = values.pop();
            boolean val1 = values.pop();
            String op = operators.pop();
            values.push(applyOperator(val1, val2, op));
        }

        return values.pop();
    }

    // Check operator precedence
    public boolean hasPrecedence(String op1, String op2) {
        if ((op1.equals("&&") && op2.equals("||"))) {
            return false;
        } else {
            return true;
        }
    }

    // Apply the operator to the values
    public boolean applyOperator(boolean val1, boolean val2, String op) {
        switch (op) {
            case "&&":
                return val1 && val2;
            case "||":
                return val1 || val2;
        }
        return false;
    }

    public boolean checkExpressionValidOrNot(String key, String value, RadiusPacket request, CustomerData customerData, boolean isRegexValue) throws Exception {
        boolean isValueFromReq = false;
        if (key.startsWith("{") || key.startsWith("REQ") || key.startsWith("REGEX")) {
            key = getValueFromRequest(key, request);
        }
        if (value.startsWith("{") || value.startsWith("REQ") || value.startsWith("REGEX")) {
            if (!isRegexValue)
                isValueFromReq = true;
            value = getValueFromRequest(value, request);

        }
        if (key.contains("PROFILE")) {
            if (!key.startsWith("{") && !key.endsWith("}")) {
                key = "{" + key + "}";
            }
            String resultFromKey = ExpressionEvaluator.getValueFromGivenExpression(key, customerData, request);
            log.debug("PROFILE DATA Key:" + key + " DATA Key Result: " + resultFromKey);
            log.debug("PROFILE DATA Value:" + value);
            if (value.equals("*"))
                return true;
            if (isRegexValue) {
                Pattern pattern = Pattern.compile(value);
                Matcher matcher = pattern.matcher(resultFromKey);
                return matcher.matches();
            } else if (resultFromKey != null && resultFromKey.contains(value)) {
                log.debug(key + " is matched");
                return true;
            } else if (value.equals("null") && (resultFromKey == null || resultFromKey.isEmpty())) { // if required any value with null
                log.debug(key + " is matched");
                return true;
            } else {
                log.debug(key + " is NOT matched");
                return false;
            }
        } else if (request.getAttribute(key) != null) {
            String resultFromKey = request.getAttribute(key).getAttributeValue();
            if (isValueFromReq)
                value = request.getAttributeValue(value);
            log.debug("DATA Key:" + key + " DATA Key Result: " + resultFromKey);
            log.debug("DATA Value:" + value);
            if (value.equals("*"))
                return true;
            if (isRegexValue) {
                Pattern pattern = Pattern.compile(value);
                Matcher matcher = pattern.matcher(resultFromKey);
                return matcher.matches();
            } else if (resultFromKey != null && resultFromKey.contains(value)) {
                log.debug(key + " is matched");
                return true;
            } else if (value.equals("null") && (resultFromKey == null || resultFromKey.isEmpty())) { // if required any value with null
                log.debug(key + " is matched");
                return true;
            } else {
                log.debug(key + " is NOT matched");
                return false;
            }
        } else {
            log.debug(key + " is NOT matched");
            return false;
        }
    }

    public String getValueFromRequest(String key, RadiusPacket request) throws Exception {
        if (key.startsWith("{") && key.endsWith("}")) {
            key = key.substring(1, key.length() - 1);
        } else if (key.startsWith("REQ")) {
            key = ExpressionEvaluator.parseExpression(key, "REQ\\{(.*)\\}");
        } else if (key.contains("REGEX")) {
            key = ExpressionEvaluator.parseExpression(key, "REGEX\\{(.*)\\}");
        }
        return key;
    }

}
