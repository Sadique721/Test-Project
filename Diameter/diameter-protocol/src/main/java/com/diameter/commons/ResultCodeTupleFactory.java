package com.diameter.commons;

public class ResultCodeTupleFactory {
  ResultCodeTuple getResultCodeTuple(int commandCode) {
    if (commandCode == CommandCode.CREDIT_CONTROL.code)
      return new CCResultCodeTuple(); 
    return new ResultCodeTuple();
  }
}
