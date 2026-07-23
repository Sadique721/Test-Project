package com.diameter.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ParserUtility {
  private static final int S = 1;
  
  private static final int R = 2;
  
  private static final int A = 3;
  
  private static final int E1 = 4;
  
  private static final int E2 = 5;
  
  private static final int E3 = 6;
  
  private static final String E1_Message = "missing right parenthesis";
  
  private static final String E2_Message = "missing operator";
  
  private static final String E3_Message = "unbalanced right parenthesis";
  
  private static final int AND = 0;
  
  private static final int OR = 1;
  
  private static final int EITHER_OR = 2;
  
  private static final int LEFT_PARENTHESIS = 3;
  
  private static final int RIGHT_PARENTHESIS = 4;
  
  private static final int $ = 5;
  
  private static final int[][] operatorTable = new int[][] { { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 1, 1, 1, 1, 2, 4 }, { 2, 2, 2, 5, 2, 2 }, { 1, 1, 1, 1, 6, 3 } };
  
  public static List<String> convertToPostFixNotation(String inStr) throws ParserException {
    List<String> tokens = getOperatorParsingTokens(inStr);
    List<String> postFixNotation = parseTokens(tokens);
    return postFixNotation;
  }
  
  public static List<String> getOperatorParsingTokens(String inStr) {
    int pos = 0;
    ArrayList<String> arrList = new ArrayList<>();
    arrList.add("$");
    StringBuilder strBuilder = new StringBuilder();
    char[] charString = inStr.toCharArray();
    int len = charString.length;
    while (pos < len) {
      char ch = charString[pos];
      if (ch == '"') {
        pos++;
        while (pos < len) {
          ch = charString[pos];
          if (ch == '\\') {
            strBuilder.append(charString[pos + 1]);
            pos += 2;
            continue;
          } 
          if (ch == '"')
            break; 
          strBuilder.append(ch);
          pos++;
        } 
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        strBuilder = new StringBuilder();
      } else if (ch == '&') {
        if (charString[pos + 1] == '&') {
          if (strBuilder.toString().length() > 0)
            arrList.add(strBuilder.toString()); 
          arrList.add("&&");
          pos++;
          strBuilder = new StringBuilder();
        } else {
          strBuilder.append(ch);
        } 
      } else if (ch == '|') {
        if (charString[pos + 1] == '|') {
          if (strBuilder.toString().length() > 0)
            arrList.add(strBuilder.toString()); 
          arrList.add("||");
          pos++;
          strBuilder = new StringBuilder();
        } else {
          strBuilder.append(ch);
        } 
      } else if (ch == '^') {
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        arrList.add("^");
        strBuilder = new StringBuilder();
      } else if (ch == '(') {
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        arrList.add("(");
        strBuilder = new StringBuilder();
      } else if (ch == ')') {
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        arrList.add(")");
        strBuilder = new StringBuilder();
      } else if (ch != ' ' && ch != '\r' && ch != '\n') {
        strBuilder.append(ch);
      } 
      pos++;
    } 
    if (strBuilder.toString().length() > 0)
      arrList.add(strBuilder.toString()); 
    arrList.add("$");
    return arrList;
  }
  
  public static List<String> parseTokens(List<String> tokens) throws ParserException {
    Stack<String> oprStack = new Stack<>();
    oprStack.push("$");
    ArrayList<String> postFixExp = new ArrayList<>();
    int listSize = tokens.size();
    for (int i = 0; i < listSize; i++) {
      String token = tokens.get(i);
      int inTokenCode = getOperatorCode(token.charAt(0));
      if (inTokenCode == -1) {
        postFixExp.add(token);
      } else {
        int stackTopOperatorCode = getOperatorCode(((String)oprStack.peek()).charAt(0));
        int operationCode = getOperation(stackTopOperatorCode, inTokenCode);
        if (operationCode == 1) {
          oprStack.push(token);
        } else if (operationCode == 2) {
          if (inTokenCode == 4) {
            String operator = oprStack.pop();
            int code = getOperatorCode(operator.charAt(0));
            while (code != 3) {
              postFixExp.add(operator);
              operator = oprStack.pop();
              code = getOperatorCode(operator.charAt(0));
            } 
          } else {
            while (operationCode == 2) {
              String op = oprStack.pop();
              postFixExp.add(op);
              stackTopOperatorCode = getOperatorCode(((String)oprStack.peek()).charAt(0));
              operationCode = getOperation(stackTopOperatorCode, inTokenCode);
            } 
            oprStack.push(token);
          } 
        } else if (operationCode != 3) {
          if (operationCode == 4)
            throw new ParserException("missing right parenthesis"); 
          if (operationCode == 5)
            throw new ParserException("missing operator"); 
          if (operationCode == 6)
            throw new ParserException("unbalanced right parenthesis"); 
        } 
      } 
    } 
    return postFixExp;
  }
  
  public static int getOperation(int operator1, int Operator2) {
    return operatorTable[operator1][Operator2];
  }
  
  public static int getOperatorCode(char operator) {
    if (operator == '&')
      return 0; 
    if (operator == '|')
      return 1; 
    if (operator == '^')
      return 2; 
    if (operator == '(')
      return 3; 
    if (operator == ')')
      return 4; 
    if (operator == '$')
      return 5; 
    return -1;
  }
  
  public static Map<String, Map<String, String>> convertToCustomerLevelPolicyMap(String inStr) {
    Map<String, Map<String, String>> policyMap = new HashMap<>();
    Map<String, String> attrMap = new HashMap<>();
    List<String> tokens = getPolicyParsingTokens(inStr);
    Iterator<String> strIterator = tokens.iterator();
    while (strIterator.hasNext()) {
      String token = strIterator.next();
      if ("$".equals(token)) {
        token = strIterator.next();
        policyMap.put(token, getAllAttributes(strIterator));
        continue;
      } 
      if (",".equals(token))
        continue; 
      if (token.contains("=")) {
        String[] attr = splitKeyAndValue(token);
        attrMap.put(attr[0], attr[2]);
        continue;
      } 
      attrMap.putAll(getAllAttributes(strIterator));
    } 
    policyMap.put("*", attrMap);
    return policyMap;
  }
  
  private static Map<String, String> getAllAttributes(Iterator<String> strIterator) {
    Map<String, String> attrMap = new HashMap<>();
    while (strIterator.hasNext()) {
      String token = strIterator.next();
      if ("$".equals(token) || ")".equals(token))
        return attrMap; 
      if (",".equals(token) || "(".equals(token))
        continue; 
      String[] attr = splitKeyAndValue(token);
      attrMap.put(attr[0], attr[2]);
    } 
    return attrMap;
  }
  
  public static Map<String, Map<String, ArrayList<String>>> convertToReplyPolicyMap(String inStr) {
    Map<String, Map<String, ArrayList<String>>> policyMap = new HashMap<>();
    Map<String, ArrayList<String>> attrMap = new HashMap<>();
    List<String> tokens = getPolicyParsingTokens(inStr);
    Iterator<String> strIterator = tokens.iterator();
    while (strIterator.hasNext()) {
      String token = strIterator.next();
      if ("$".equals(token)) {
        token = strIterator.next();
        policyMap.put(token, getAllAttributesList(strIterator));
        continue;
      } 
      if (",".equals(token))
        continue; 
      if ("=".equals(token)) {
        String[] attr = splitKeyAndValue(token);
        ArrayList<String> valueList = attrMap.get(attr[0]);
        if (valueList == null) {
          valueList = new ArrayList<>();
          valueList.add(attr[2]);
          attrMap.put(attr[0], valueList);
          continue;
        } 
        valueList.add(attr[2]);
        continue;
      } 
      if (" ".equals(token))
        continue; 
      if (token.startsWith("\n") || token.startsWith("\r"))
        continue; 
      attrMap.putAll(getAllAttributesList(strIterator));
    } 
    policyMap.put("*", attrMap);
    return policyMap;
  }
  
  public static List<String> getReplyItemTokens(String inStr) {
    StringBuilder currentToken = new StringBuilder();
    ArrayList<String> replyItemList = new ArrayList<>();
    int pos = 0;
    char[] charString = inStr.toCharArray();
    int len = charString.length;
    while (pos < len) {
      if (charString[pos] == '"') {
        pos++;
        while (pos < len) {
          char ch = charString[pos];
          if (ch == '\\') {
            currentToken.append(charString[pos + 1]);
            pos += 2;
            continue;
          } 
          if (ch == '"') {
            pos++;
            break;
          } 
          currentToken.append(ch);
          pos++;
        } 
        continue;
      } 
      if (charString[pos] == '(' || charString[pos] == ')' || charString[pos] == ',') {
        if (currentToken.length() > 0)
          replyItemList.add(currentToken.toString()); 
        replyItemList.add(String.valueOf(charString[pos]));
        currentToken = new StringBuilder();
        pos++;
        continue;
      } 
      if (charString[pos] == '\r' || charString[pos] == '\n' || charString[pos] == ' ') {
        pos++;
        continue;
      } 
      currentToken.append(charString[pos]);
      pos++;
    } 
    if (currentToken.length() > 0)
      replyItemList.add(currentToken.toString()); 
    return replyItemList;
  }
  
  public static Map<String, Map<String, Map<String, ArrayList<String>>>> parseCustomerReplyItems(String strReplyItems) throws ParserException {
    List<String> replyItemList = getReplyItemTokens(strReplyItems);
    int policyDepthLevel = 0;
    if (replyItemList == null)
      return null; 
    Map<String, Map<String, Map<String, ArrayList<String>>>> policyOverrideDataMap = new HashMap<>();
    Map<String, Map<String, ArrayList<String>>> defaultReplyItemMap = new HashMap<>();
    int listSize = replyItemList.size();
    for (int i = 0; i < listSize; i++) {
      String currentToken = replyItemList.get(i);
      if (currentToken.charAt(0) == '$' && !currentToken.startsWith("$REQ:") && !currentToken.startsWith("$RES:")) {
        Map<String, Map<String, ArrayList<String>>> policyWiseReplyItemMap = new HashMap<>();
        if (!((String)replyItemList.get(++i)).equals("("))
          throw new ParserException("Invalid Token : " + currentToken + " in reply item : " + strReplyItems); 
        policyDepthLevel = 0;
        while (++i < listSize) {
          String tmpString = replyItemList.get(i);
          if (")".equals(tmpString) && policyDepthLevel == 0)
            break; 
          if ("(".equals(tmpString)) {
            policyDepthLevel++;
            continue;
          } 
          if (")".equals(tmpString)) {
            policyDepthLevel--;
            continue;
          } 
          if (tmpString.startsWith("$REQ:") || tmpString.startsWith("$RES:")) {
            Map<String, ArrayList<String>> replyItemBasedOnResMap = new LinkedHashMap<>();
            if (!((String)replyItemList.get(++i)).equals("("))
              throw new ParserException("Invalid Token : " + (String)replyItemList.get(i) + " in reply item : " + strReplyItems); 
            while (++i < listSize) {
              String replyItems = replyItemList.get(i);
              if (")".equals(replyItems)) {
                i++;
                break;
              } 
              if (",".equals(replyItems))
                continue; 
              String[] arrayOfString = splitKeyAndValue(replyItems);
              if (arrayOfString.length != 3)
                throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems); 
              ArrayList<String> arrayList = replyItemBasedOnResMap.get(arrayOfString[0]);
              if (arrayList == null) {
                arrayList = new ArrayList<>();
                replyItemBasedOnResMap.put(arrayOfString[0], arrayList);
              } 
              arrayList.add(arrayOfString[2]);
            } 
            policyWiseReplyItemMap.put(tmpString, replyItemBasedOnResMap);
            continue;
          } 
          if (",".equals(tmpString))
            continue; 
          Map<String, ArrayList<String>> replyItem = policyWiseReplyItemMap.get("*");
          if (replyItem == null) {
            replyItem = new HashMap<>();
            policyWiseReplyItemMap.put("*", replyItem);
          } 
          String[] tmp = splitKeyAndValue(tmpString);
          ArrayList<String> valueList = replyItem.get(tmp[0]);
          if (valueList == null) {
            valueList = new ArrayList<>();
            replyItem.put(tmp[0], valueList);
          } 
          valueList.add(tmp[2]);
        } 
        policyOverrideDataMap.put(currentToken.substring(1), policyWiseReplyItemMap);
      } else if (currentToken.startsWith("$REQ:") || currentToken.startsWith("$RES:")) {
        Map<String, ArrayList<String>> replyItemBasedOnReqMap = new HashMap<>();
        if (!((String)replyItemList.get(++i)).equals("("))
          throw new ParserException("Invalid Token : " + (String)replyItemList.get(i) + " in reply item : " + strReplyItems); 
        while (++i < listSize) {
          String replyItems = replyItemList.get(i);
          if (")".equals(replyItems)) {
            i++;
            break;
          } 
          if (",".equals(replyItems))
            continue; 
          String[] tmp = splitKeyAndValue(replyItems);
          if (tmp.length != 3)
            throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems); 
          ArrayList<String> valueList = replyItemBasedOnReqMap.get(tmp[0]);
          if (valueList == null) {
            valueList = new ArrayList<>();
            replyItemBasedOnReqMap.put(tmp[0], valueList);
          } 
          valueList.add(tmp[2]);
        } 
        defaultReplyItemMap.put(currentToken, replyItemBasedOnReqMap);
      } else {
        Map<String, ArrayList<String>> replyItems = defaultReplyItemMap.get("*");
        if (replyItems == null) {
          replyItems = new HashMap<>();
          defaultReplyItemMap.put("*", replyItems);
        } 
        if (!",".equals(currentToken)) {
          String[] tmp = splitKeyAndValue(currentToken);
          if (tmp.length != 3)
            throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems); 
          ArrayList<String> valueList = replyItems.get(tmp[0]);
          if (valueList == null) {
            valueList = new ArrayList<>();
            replyItems.put(tmp[0], valueList);
          } 
          valueList.add(tmp[2]);
        } 
      } 
    } 
    policyOverrideDataMap.put("*", defaultReplyItemMap);
    return policyOverrideDataMap;
  }
  
  public static Map<String, Map<String, ArrayList<String>>> parseReplyItems(String strReplyItems) throws ParserException {
    List<String> replyItemList = getReplyItemTokens(strReplyItems);
    if (replyItemList == null)
      return null; 
    Map<String, Map<String, ArrayList<String>>> replyItemMap = new HashMap<>();
    int listSize = replyItemList.size();
    for (int i = 0; i < listSize; i++) {
      String currentToken = replyItemList.get(i);
      if (currentToken.startsWith("$REQ:") || currentToken.startsWith("$RES:")) {
        Map<String, ArrayList<String>> replyItemBasedOnReqMap = new LinkedHashMap<>();
        if (!((String)replyItemList.get(++i)).equals("("))
          throw new ParserException("Invalid Token : " + (String)replyItemList.get(i) + " in reply item : " + strReplyItems); 
        while (++i < listSize) {
          String replyItems = replyItemList.get(i);
          if (")".equals(replyItems)) {
            i++;
            break;
          } 
          if (",".equals(replyItems))
            continue; 
          String[] tmp = splitKeyAndValue(replyItems);
          if (tmp.length != 3)
            throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems); 
          ArrayList<String> valueList = replyItemBasedOnReqMap.get(tmp[0]);
          if (valueList == null) {
            valueList = new ArrayList<>();
            replyItemBasedOnReqMap.put(tmp[0], valueList);
          } 
          valueList.add(tmp[2]);
        } 
        replyItemMap.put(currentToken, replyItemBasedOnReqMap);
      } else {
        Map<String, ArrayList<String>> replyItems = replyItemMap.get("*");
        if (replyItems == null) {
          replyItems = new LinkedHashMap<>();
          replyItemMap.put("*", replyItems);
        } 
        if (!",".equals(currentToken)) {
          String[] tmp = splitKeyAndValue(currentToken);
          if (tmp.length != 3)
            throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems); 
          ArrayList<String> valueList = replyItems.get(tmp[0]);
          if (valueList == null) {
            valueList = new ArrayList<>();
            replyItems.put(tmp[0], valueList);
          } 
          valueList.add(tmp[2]);
        } 
      } 
    } 
    return replyItemMap;
  }
  
  public static Map<String, String> convertToPolicyWiseReplyItemMap(String inStr) {
    Map<String, String> policyMap = new HashMap<>();
    int pos = 0;
    StringBuilder strBuilder = null;
    StringBuilder policyName = null;
    char[] charString = inStr.toCharArray();
    int len = charString.length;
    while (pos < len) {
      if (charString[pos] == '$') {
        policyName = new StringBuilder();
        pos++;
        while (pos < len && charString[pos] != '(') {
          policyName.append(charString[pos]);
          pos++;
        } 
      } 
      if (charString[pos] == '(') {
        int stop = 0;
        pos++;
        strBuilder = new StringBuilder();
        while (pos < len) {
          char currentChar = charString[pos];
          if (currentChar == ')' && stop == 0)
            break; 
          strBuilder.append(currentChar);
          if (currentChar == '(') {
            stop++;
          } else if (currentChar == ')') {
            stop--;
          } 
          pos++;
        } 
        policyMap.put(policyName.toString(), strBuilder.toString());
        pos++;
      } 
      pos++;
    } 
    return policyMap;
  }
  
  private static Map<String, ArrayList<String>> getAllAttributesList(Iterator<String> strIterator) {
    Map<String, ArrayList<String>> attrMap = new LinkedHashMap<>();
    while (strIterator.hasNext()) {
      String token = strIterator.next();
      if ("$".equals(token) || ")".equals(token))
        return attrMap; 
      if (",".equals(token) || "(".equals(token))
        continue; 
      String[] attr = splitKeyAndValue(token);
      ArrayList<String> attributeList = attrMap.get(attr[0]);
      if (attributeList == null) {
        attributeList = new ArrayList<>();
        attributeList.add(attr[2]);
        attrMap.put(attr[0], attributeList);
        continue;
      } 
      attributeList.add(attr[2]);
    } 
    return attrMap;
  }
  
  public static String[] splitKeyAndValue(String attr) {
    String[] splitedAttr = new String[3];
    StringBuilder strBuilder = new StringBuilder();
    int pos = 0;
    char[] charString = attr.toCharArray();
    int len = charString.length;
    while (pos < len) {
      char ch = charString[pos];
      if (ch == '=') {
        if (strBuilder.toString().length() > 0)
          splitedAttr[0] = strBuilder.toString(); 
        splitedAttr[1] = "=";
        splitedAttr[2] = attr.substring(pos + 1);
        return splitedAttr;
      } 
      if (ch == '!') {
        if (charString[pos + 1] == '=') {
          pos++;
          if (strBuilder.toString().length() > 0)
            splitedAttr[0] = strBuilder.toString(); 
          splitedAttr[1] = "!=";
          splitedAttr[2] = attr.substring(pos + 1);
          return splitedAttr;
        } 
        continue;
      } 
      if (ch == '<') {
        if (charString[pos + 1] == '=') {
          pos++;
          if (strBuilder.toString().length() > 0)
            splitedAttr[0] = strBuilder.toString(); 
          splitedAttr[1] = "<=";
          splitedAttr[2] = attr.substring(pos + 1);
        } else {
          if (strBuilder.toString().length() > 0)
            splitedAttr[0] = strBuilder.toString(); 
          splitedAttr[1] = "<";
          splitedAttr[2] = attr.substring(pos + 1);
        } 
        return splitedAttr;
      } 
      if (ch == '>') {
        if (charString[pos + 1] == '=') {
          pos++;
          if (strBuilder.toString().length() > 0)
            splitedAttr[0] = strBuilder.toString(); 
          splitedAttr[1] = ">=";
          splitedAttr[2] = attr.substring(pos + 1);
        } else {
          if (strBuilder.toString().length() > 0)
            splitedAttr[0] = strBuilder.toString(); 
          splitedAttr[1] = ">";
          splitedAttr[2] = attr.substring(pos + 1);
        } 
        return splitedAttr;
      } 
      strBuilder.append(ch);
      pos++;
    } 
    if (strBuilder.toString().length() > 0)
      splitedAttr[0] = strBuilder.toString(); 
    return splitedAttr;
  }
  
  public static String[] splitString(String strAttribute, char... splitChar) {
    ArrayList<String> splittedExpression = new ArrayList<>();
    StringBuilder currentExpression = new StringBuilder();
    int pos = 0;
    if (strAttribute == null)
      return null; 
    char[] charString = strAttribute.toCharArray();
    int len = charString.length;
    while (pos < len) {
      char currentChar = charString[pos];
      if (currentChar == '"') {
        pos++;
        while (pos < len) {
          currentChar = charString[pos];
          if (currentChar == '\\') {
            currentExpression.append(charString[pos + 1]);
            pos += 2;
            continue;
          } 
          if (currentChar == '"') {
            pos++;
            break;
          } 
          currentExpression.append(currentChar);
          pos++;
        } 
        continue;
      } 
      if (containsChar(splitChar, currentChar)) {
        if (currentExpression.toString().length() > 0)
          splittedExpression.add(currentExpression.toString()); 
        currentExpression = new StringBuilder();
        pos++;
        continue;
      } 
      currentExpression.append(currentChar);
      pos++;
    } 
    if (currentExpression != null && currentExpression.length() != 0)
      splittedExpression.add(currentExpression.toString()); 
    return splittedExpression.<String>toArray(new String[splittedExpression.size()]);
  }
  
  public static boolean containsChar(char[] characters, char lookupChar) {
    if (characters != null)
      for (int i = 0; i < characters.length; i++) {
        if (characters[i] == lookupChar)
          return true; 
      }  
    return false;
  }
  
  public static List<String> getPolicyParsingTokens(String inStr) {
    int pos = 0;
    boolean bIgnore = false;
    ArrayList<String> arrList = new ArrayList<>();
    StringBuilder strBuilder = new StringBuilder();
    char[] charString = inStr.toCharArray();
    int len = charString.length;
    while (pos < len) {
      char ch = charString[pos];
      if (ch == '"') {
        pos++;
        while (pos < len) {
          ch = charString[pos];
          if (ch == '\\') {
            strBuilder.append(charString[pos + 1]);
            pos += 2;
            continue;
          } 
          if (ch == '"') {
            pos++;
            break;
          } 
          strBuilder.append(ch);
          pos++;
        } 
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        strBuilder = new StringBuilder();
        continue;
      } 
      if (ch == '(') {
        bIgnore = true;
        arrList.add("(");
        pos++;
        while (pos < len) {
          ch = charString[pos];
          if (ch == ',' || ch == ')' || ch == '"')
            break; 
          strBuilder.append(ch);
          pos++;
        } 
        if (strBuilder.toString().length() > 0 && pos < len && charString[pos] != '"') {
          arrList.add(strBuilder.toString());
          strBuilder = new StringBuilder();
        } 
        continue;
      } 
      if (ch == ')') {
        bIgnore = false;
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        arrList.add(")");
        strBuilder = new StringBuilder();
        pos++;
        continue;
      } 
      if (ch == '$' && !bIgnore) {
        arrList.add("$");
        pos++;
        while (pos < len) {
          ch = charString[pos];
          if (ch == '(' || ch == '"')
            break; 
          strBuilder.append(ch);
          pos++;
        } 
        if (strBuilder.toString().length() > 0 && pos < len && charString[pos] != '"') {
          arrList.add(strBuilder.toString());
          strBuilder = new StringBuilder();
        } 
        continue;
      } 
      if (ch == ',') {
        if (strBuilder.toString().length() > 0)
          arrList.add(strBuilder.toString()); 
        strBuilder = new StringBuilder();
        arrList.add(",");
        pos++;
        while (pos < len) {
          ch = charString[pos];
          if (ch == ',' || ch == ')' || ch == '$' || ch == '"')
            break; 
          strBuilder.append(ch);
          pos++;
        } 
        if (strBuilder.toString().length() > 0 && pos < len && charString[pos] != '"') {
          arrList.add(strBuilder.toString());
          strBuilder = new StringBuilder();
        } 
        continue;
      } 
      if (ch == ' ') {
        pos++;
        continue;
      } 
      strBuilder.append(ch);
      pos++;
    } 
    if (strBuilder.toString().length() > 0)
      arrList.add(strBuilder.toString()); 
    return arrList;
  }
  
  public static String getRegxPattern(String strPattern) {
    char[] patternCharacters = strPattern.toCharArray();
    StringBuilder newPattern = new StringBuilder();
    String strRegx = null;
    for (int i = 0; i < patternCharacters.length; i++) {
      if (patternCharacters[i] == '\\') {
        i++;
        if (patternCharacters[i] == '*' || patternCharacters[i] == '?' || patternCharacters[i] == '\\') {
          newPattern.append("\\" + patternCharacters[i]);
        } else {
          newPattern.append("\\\\" + patternCharacters[i]);
        } 
      } else if (patternCharacters[i] == '*') {
        newPattern.append("[\\p{ASCII}]*");
      } else if (patternCharacters[i] == '?') {
        newPattern.append("[\\p{ASCII}]");
      } else {
        newPattern.append(patternCharacters[i]);
      } 
    } 
    strRegx = newPattern.toString();
    return strRegx;
  }
  
  public static void main(String[] args) {}
}
