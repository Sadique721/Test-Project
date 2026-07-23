//package com.savbill.radius.spring;
//
//import java.util.Optional;
//
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class SpringSecurityAuditorAware implements AuditorAware<Integer> {
//
//	  public Optional<Integer> getCurrentAuditor() {
//
//	    return Optional.ofNullable(SecurityContextHolder.getContext())
//				  .map(SecurityContext::getAuthentication)
//				  .filter(Authentication::isAuthenticated)
//				  .map(Authentication::getPrincipal)
//				  .map(LoggedInUser.class::cast)
//				  .map(LoggedInUser::getUserId);
//	  }
//	}
