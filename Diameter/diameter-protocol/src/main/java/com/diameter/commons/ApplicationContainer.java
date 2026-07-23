package com.diameter.commons;

import java.util.Set;

public interface ApplicationContainer {
  Set<ApplicationEnum> getApplications();
  
  Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> paramSet);
}
