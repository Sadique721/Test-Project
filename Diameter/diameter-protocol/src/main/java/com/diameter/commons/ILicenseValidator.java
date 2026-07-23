package com.diameter.commons;

public interface ILicenseValidator {
  public static final ILicenseValidator SUPPORT_ALL_VENDORS = new ILicenseValidator() {
      public boolean isVendorSupported(String vendorId) {
        return true;
      }
    };
  
  boolean isVendorSupported(String paramString);
}
