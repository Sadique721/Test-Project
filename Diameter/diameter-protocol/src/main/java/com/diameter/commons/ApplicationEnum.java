package com.diameter.commons;

public interface ApplicationEnum {
  long getApplicationId();
  
  long getVendorId();
  
  Application getApplication();
  
  ServiceTypes getApplicationType();
}
