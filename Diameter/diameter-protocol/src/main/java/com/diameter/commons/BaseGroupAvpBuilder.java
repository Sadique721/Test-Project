package com.diameter.commons;

import java.util.ArrayList;

public abstract class BaseGroupAvpBuilder extends BaseAVPBuilder {
  protected ArrayList<AvpRule> fixedAttrList = new ArrayList<>();
  
  protected ArrayList<AvpRule> requiredAttrList = new ArrayList<>();
  
  protected ArrayList<AvpRule> optionalAttrList = new ArrayList<>();
  
  public void setFixedAttrList(ArrayList<AvpRule> fixedAttrList) {
    this.fixedAttrList = fixedAttrList;
  }
  
  public void setOptionalAttrList(ArrayList<AvpRule> optionalAttrList) {
    this.optionalAttrList = optionalAttrList;
  }
  
  public void setRequiredAttrList(ArrayList<AvpRule> requiredAttrList) {
    this.requiredAttrList = requiredAttrList;
  }
}
