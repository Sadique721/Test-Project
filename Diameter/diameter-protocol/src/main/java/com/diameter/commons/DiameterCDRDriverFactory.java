package com.diameter.commons;

public interface DiameterCDRDriverFactory {
  CDRDriver<DiameterPacket> getDriverById(String paramString) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
  
  CDRDriver<DiameterPacket> getDriver(String paramString) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
}
