package com.diameter.commons;

public class CounterTupleFactory {
  CounterTuple getCounterTuple(DiameterPacket packet) {
    int commandCode = packet.getCommandCode();
    if (commandCode == CommandCode.CREDIT_CONTROL.code)
      return new CCCounterTuple(); 
    return new CounterTuple();
  }
}
