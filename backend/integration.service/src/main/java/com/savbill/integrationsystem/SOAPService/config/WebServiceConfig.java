package com.savbill.integrationsystem.SOAPService.config;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
//	@Bean
//	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
//		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
//		servlet.setApplicationContext(applicationContext);
//		servlet.setTransformWsdlLocations(true);
//		return new ServletRegistrationBean(servlet, "/ws/*");
//	}

//	@Bean(name = "countries")
//	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
//		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//		wsdl11Definition.setPortTypeName("CountriesPort");
//		wsdl11Definition.setLocationUri("/ws");
//		wsdl11Definition.setTargetNamespace("http://api.act.com/");
//		wsdl11Definition.setSchema(countriesSchema);
//		return wsdl11Definition;
//	}
//
//	@Bean
//	public XsdSchema countriesSchema() {
//		return new SimpleXsdSchema(new ClassPathResource("xsd/countries.xsd"));
//	}

    @Bean(name = "wsAddAccount")
    public DefaultWsdl11Definition addAccountWsdl(XsdSchema addAccountSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AddAccountPort");
        wsdl11Definition.setLocationUri(SoapConstants.WS);
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(addAccountSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema addAccountSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/AddAccount/wsAddAccount.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "wsGetBalance")
    public DefaultWsdl11Definition WsGetBalanceWsdl(XsdSchema WsGetBalanceSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsGetBalancePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(WsGetBalanceSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema WsGetBalanceSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WsGetBalance/wsGetBalance.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "logoffUserSessions")
    public DefaultWsdl11Definition getLogoffUserSessionsWsdl(XsdSchema logoffUserSessionsXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsLogoffUserSessionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(logoffUserSessionsXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema logoffUserSessionsXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/LogOffUserSessions/logoffUserSessions.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "changeService")
    public DefaultWsdl11Definition getChangeServiceWsdl(XsdSchema changeServiceXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WschangeServicePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(changeServiceXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema changeServiceXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ChangeService/changeService.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "loginSession")
    public DefaultWsdl11Definition getValidateLogingSessionWsdl(XsdSchema logingSessionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsLogingSessionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(logingSessionXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema logingSessionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/logingSession/wsLogingsession.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "GetSessionsByIp")
    public DefaultWsdl11Definition GetSessionsByIpWsdl(XsdSchema GetSessionsByIpSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("GetSessionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(GetSessionsByIpSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema GetSessionsByIpSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/wsGetUserSessions/wsGetSessionsByIP.xsd"));
    }

    @Bean(name = "sessionLoginStatus")
    public DefaultWsdl11Definition sessionLoginStatusWsdl(XsdSchema SessionLoginStatusXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("SessionLoginStatusPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(SessionLoginStatusXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema SessionLoginStatusXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/SessionLoginStatus/wsSessionLoginStatus.xsd")); // Ensure this points to the correct path
    }

    @Bean(name = "updateAccount")
    public DefaultWsdl11Definition updateAccount(XsdSchema updateAccountSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UpdateAccount");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(updateAccountSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema updateAccountSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/UpdateAccount/wsUpdateAccount.xsd"));
    }

    @Bean(name = "authenticateUser")
    public DefaultWsdl11Definition getAuthenticateUserWsdl(XsdSchema authenticateUserXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsAuthenticateUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(authenticateUserXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema authenticateUserXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/authenticateUser/wsAuthenticateUser.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "GetUserUssageSummary")
    public DefaultWsdl11Definition GetUserUssageSummarylSchemaWsdl(XsdSchema GetUserUssageSummarylSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("GetUserUssageSummaryPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(GetUserUssageSummarylSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema GetUserUssageSummarylSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/GetUserUsageSummary/GetUserUsageSummary.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "removeAccount")
    public DefaultWsdl11Definition getAemoveAccountWsdl(XsdSchema removeAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsRemoveAccountUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(removeAccountXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema removeAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/removeAccount/wsRemoveAccount.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "resetUsageForAccount")
    public DefaultWsdl11Definition getWsResetUsageForAccountWsdl(XsdSchema resetUsageForAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsResetUsageForAccountPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(resetUsageForAccountXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema resetUsageForAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/resetUsageForAccount/resetUsageForAccount.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "updateUserUsage")
    public DefaultWsdl11Definition UpdateUserUsageWsdl(XsdSchema updateUserUsageXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UpdateUserUsageUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(updateUserUsageXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema updateUserUsageXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/UpdateUserUsage/wsUpdateUserUsage.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "userLoginStatus")
    public DefaultWsdl11Definition UserLoginStatusWsdl(XsdSchema userLoginStatusXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UserLoginStatusPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(userLoginStatusXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema userLoginStatusXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/UserLoginStatus/WsUserLoginStatus.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "getAccountDetails")
    public DefaultWsdl11Definition getAccountDetailsSchemaWsdl(XsdSchema getAccountDetailsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("getAccountDetailsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(getAccountDetailsSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema getAccountDetailsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/GetAccocuntDetails/GetAccountDetails.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "getAccountName")
    public DefaultWsdl11Definition getAccountNameWsdl(XsdSchema getAccountNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("GetAccountNameUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(getAccountNameXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema getAccountNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/GetAccountName/wsGetAccountName.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "getAddServiceToAccount")
    public DefaultWsdl11Definition getAddServiceToAccountWsdl(XsdSchema getAddServiceToAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("GetAddServiceToAccountPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(getAddServiceToAccountXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema getAddServiceToAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/AddServiceToAccount/AddServiceToAccount.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "subAcctNameIsLogged")
    public DefaultWsdl11Definition subAcctNameIsLoggedOnWsdl(XsdSchema subAcctNameIsLoggedOnXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("subAcctUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(subAcctNameIsLoggedOnXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema subAcctNameIsLoggedOnXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/SubAcctNameIsLoggedOn/subAcctNameIsLoggedOn.xsd"));
    }

    @Bean(name = "subSessionIsLoggedOnRequest")
    public DefaultWsdl11Definition subSessionIsLoggedOnWsdl(XsdSchema subSessionIsLoggedOnXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("subSessionIsLoggedOnUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(subSessionIsLoggedOnXmlSchema());
//		wsdl11Definition.setSchema(subSessionIsLoggedOnXmlSchema());
        return wsdl11Definition;
    }
    @Bean
    public XsdSchema subSessionIsLoggedOnXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/subSessionIsLoggedOn/subSessionIsLoggedOn.xsd"));
    }


    @Bean(name = "getSubAcctName")
    public DefaultWsdl11Definition getSubAcctNameXmlSchemaWsdl(XsdSchema getSubAcctNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("subAcctUserPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(getSubAcctNameXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema getSubAcctNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/GetSubAcctName/getSubAcctName.xsd"));
    }

    @Bean(name = "logoffSubSessions")
    public DefaultWsdl11Definition logoffSubSessionsWsdl(XsdSchema subAcctNameIsLoggedOnXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("logoffSubSessionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(logoffSubSessionsXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema logoffSubSessionsXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/logoffSubSessions/logoffSubSessions.xsd"));
    }

    @Bean(name = "getSubscriberSession")
    public DefaultWsdl11Definition getSubscriberSessionXmlSchemaXmlSchemaWsdl(XsdSchema getSubscriberSessionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("subscriberSessionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(getSubscriberSessionXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema getSubscriberSessionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/GetSubscriberSession/GetSubscriberSession.xsd"));
    }


    @Bean(name = "addSubscriberAccountXML")
    public DefaultWsdl11Definition addSubscriberAccountXMLSchemaXmlSchemaWsdl(XsdSchema addSubscriberAccountXMLSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("subscriberSessionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(addSubscriberAccountXMLSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema addSubscriberAccountXMLSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/AddSubscriberAccountXML/addSubscriberAccountXML.xsd"));
    }


    @Bean(name = "authenticateSubscriber")
    public DefaultWsdl11Definition authenticateSubscriberWsdl(XsdSchema authenticateSubscriberXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("authenticateSubscriberPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(authenticateSubscriberXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema authenticateSubscriberXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/authenticateSubscriber/WsAuthenticateSubscriber.xsd"));
    }

    @Bean(name = "removeSubscriberAccount")
    public DefaultWsdl11Definition removeSubscriberAccountWsdl(XsdSchema removeSubscriberAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("removeSubscriberAccountPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(removeSubscriberAccountXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema removeSubscriberAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/removeSubscriberAccount/WsRemoveSubscriberAccount.xsd"));
    }

    @Bean(name = "AddServiceToSubAcctName")
    public DefaultWsdl11Definition AddServiceToSubAcctNameWsdl(XsdSchema AddServiceToSubAcctNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("authenticateSubscriberPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(AddServiceToSubAcctNameXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema AddServiceToSubAcctNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/AddServiceToSubAcctName/AddServiceToSubAcctName.xsd"));
    }

    @Bean(name = "ResetMeteredUsageForSubAcctName")
    public DefaultWsdl11Definition ResetMeteredUsageForSubAcctNameWsdl(XsdSchema ResetMeteredUsageForSubAcctNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("ResetMeteredUsageForSubAcctNamePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(ResetMeteredUsageForSubAcctNameXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema ResetMeteredUsageForSubAcctNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/resetMeteredUsageForSubAcctName/ResetMeteredUsageForSubAcctName.xsd"));
    }

    @Bean(name = "logoffSubSession")
    public DefaultWsdl11Definition logoffSubSessionWsdl(XsdSchema logoffSubSessionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("LogoffSubSessionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(logoffSubSessionXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema logoffSubSessionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/LogOffSubSession/WsLogOffSubSession.xsd"));
    }

    @Bean(name = "RemoveService")
    public DefaultWsdl11Definition RemoveServiceWsdl(XsdSchema RemoveServiceXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RemoveServicePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(RemoveServiceXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema RemoveServiceXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/RemoveService/RemoveService.xsd"));
    }

    @Bean(name = "logoffUserSession")
    public DefaultWsdl11Definition getLogoffUserSessionWsdl(XsdSchema logoffUserSessionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsLogoffUserSessionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(logoffUserSessionXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema logoffUserSessionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/logOffUserSession/logOffUserSession.xsd")); // Ensure this points to the correct path

    }

    @Bean(name = "LogonSubSession")
    public DefaultWsdl11Definition LogonSubSessionWsdl(XsdSchema LogonSubSessionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("LogonSubSessionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(LogonSubSessionXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema LogonSubSessionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/LogOnSubSession/LogOnSubSession.xsd"));
    }

    @Bean(name = "wsSubscribeAddOn")
    public DefaultWsdl11Definition wsSubscribeAddOnWsdl(XsdSchema wsSubscribeAddOnXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("wsSubscribeAddOnPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(wsSubscribeAddOnXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema wsSubscribeAddOnXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WsSubscribeAddOn/WsSubscribeAddOn.xsd"));
    }

    @Bean(name = "wsMeteredVolumeUsageForSubAcctName")
    public DefaultWsdl11Definition wsMeteredVolumeUsageForSubAcctNameWsdl(XsdSchema wsMeteredVolumeUsageForSubAcctNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsLogoffUserSessionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(wsMeteredVolumeUsageForSubAcctNameXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema wsMeteredVolumeUsageForSubAcctNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/meteredVolumeUsageForSubAcctName/WsMeteredVolumeUsageForSubAcctName.xsd")); // Ensure this points to the correct path
    }

    @Bean(name = "getBalance")
    public DefaultWsdl11Definition getBalanceWsdl(XsdSchema getBalanceXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("getBalancePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(getBalanceXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema getBalanceXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/getBlanace/getBalance.xsd")); // Ensure this points to the correct path
    }

    @Bean(name = "wsSubscribeTopUp")
    public DefaultWsdl11Definition gwsSubscribeTopUpWsdl(XsdSchema wsSubscribeTopUpXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("wsSubscribeTopUpPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(wsSubscribeTopUpXmlSchema());
        return wsdl11Definition;

    }

    @Bean
    public XsdSchema wsSubscribeTopUpXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WsSubscribeTopUp/WsSubscribeTopUp.xsd")); // Ensure this points to the correct path
    }

    @Bean(name = "getSubscriberAccount")
    public DefaultWsdl11Definition getSubscriberAccountWsdl(XsdSchema getSubscriberAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("getSubscriberAccountPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(getSubscriberAccountXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema getSubscriberAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/getSubscriberAccountXML/getSubscriberAccountXML.xsd"));
    }

    @Bean(name = "updateSubscriberAccountXML")
    public DefaultWsdl11Definition updateSubscriberAccounttWsdl(XsdSchema updateSubscriberAccountXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("updateSubscriberAccountPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI);
        wsdl11Definition.setSchema(updateSubscriberAccountXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema updateSubscriberAccountXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/updateSubscriberAccountXML/updateSubscriberAccountXML.xsd"));
    }

    @Bean(name = "reauthSessions")
    public DefaultWsdl11Definition ReauthSessionsWsdl(XsdSchema ReauthSessionsXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("ReauthSessions");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW_SES);
        wsdl11Definition.setSchema(ReauthSessionsXmlSchema());

        // Define SOAP 1.2 binding
        wsdl11Definition.setCreateSoap12Binding(true);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema ReauthSessionsXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ReauthSession/WsReauthSession.xsd"));
    }

    @Bean(name = "WsChangeAddOnSubscription")
    public DefaultWsdl11Definition WsAddChangeAddOnSubscriptionWsdl(XsdSchema getWsAddChangeAddOnSubscriptionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsAddChangeAddOnSubscriptiontPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(getWsAddChangeAddOnSubscriptionXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema getWsAddChangeAddOnSubscriptionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WsChangeAddOnSubscription/WsChangeAddOnSubscription.xsd"));
    }

    @Bean(name = "WsChangeTopUpSubscription")
    public DefaultWsdl11Definition WsChangeTopUpSubscriptionWsdl(XsdSchema WsChangeTopUpSubscriptionXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WsChangeTopUpSubscriptionPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(WsChangeTopUpSubscriptionXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema WsChangeTopUpSubscriptionXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WsChangeTopUpSubscription/WsChangeTopUpSubscription.xsd"));
    }

    @Bean(name = "changeAndApplyServicesToSubAcctName")
    public DefaultWsdl11Definition WschangeAndApplyServicesToSubAcctNameWsdl(XsdSchema WschangeAndApplyServicesToSubAcctNameXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("WschangeAndApplyServicesToSubAcctNamePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_URI_NEW);
        wsdl11Definition.setSchema(WschangeAndApplyServicesToSubAcctNameXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema WschangeAndApplyServicesToSubAcctNameXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/changeAndApplyServices/changeAndApplyServicesToSubAcctNameXML.xsd"));
    }



    @Bean(name = "wsListTopUpSubscriptions")
    public DefaultWsdl11Definition WsListTopUpSubscriptionsWsdl(XsdSchema wsListTopUpSubscriptionsXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("wsListTopUpSubscriptionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(wsListTopUpSubscriptionsXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema wsListTopUpSubscriptionsXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/wsListTopUpSubscriptions/wsListTopUpSubscriptions.xsd"));
    }



    @Bean(name = "wsListAddOnSubscriptions")
    public DefaultWsdl11Definition wsListAddOnSubscriptionsWsdl(XsdSchema wsListAddOnSubscriptionsXmlSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("wsListAddOnSubscriptionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl11Definition.setSchema(wsListAddOnSubscriptionsXmlSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema wsListAddOnSubscriptionsXmlSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/wsListAddOnSubscriptions/wsListAddOnSubscriptions.xsd"));
    }



    @Bean(name = "QodServices")
    public WsdlDefinition authenticateUserWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/mergedBinding.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }

    @Bean(name = "finalMergedWsdl")
    public WsdlDefinition finalMergedWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/mergedWsdl.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }

    @Bean(name = "npmServices")
    public WsdlDefinition oldApiWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/npmWsdl.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }
    @Bean(name = "wsListTopUpSubscriptions")
    public DefaultWsdl11Definition listTopUpSubscriptionsWsdl() {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ListTopUpSubscriptionsPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl.setSchema(listTopUpSubscriptionsSchema());
        return wsdl;
    }

    @Bean
    public XsdSchema listTopUpSubscriptionsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/wsListTopUpSubscriptions/wsListTopUpSubscriptions.xsd"));
    }

    @Bean(name = "wsListAddOnSubscriptions")
    public DefaultWsdl11Definition listAddOnSubscriptionsWsdl() {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ListAddOnSubscriptionsPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace(SoapConstants.NAMESPACE_ELITECORE);
        wsdl.setSchema(listAddOnSubscriptionsSchema());
        return wsdl;
    }

    @Bean
    public XsdSchema listAddOnSubscriptionsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/wsListAddOnSubscriptions/wsListAddOnSubscriptions.xsd"));
    }

    @Bean(name = "appServices")
    public WsdlDefinition appUserWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/appWsld.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }

    @Bean(name = "finalAppWsdl")
    public WsdlDefinition finalAppWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/finalAppWsdl.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }



    @Bean(name = "finalVasWsdl")
    public WsdlDefinition finalVasWsdl() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/finalVasWsdl.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }

    @Bean(name = "vasServices")
    public WsdlDefinition vas() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/vas.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }


    @Bean(name = "mergedServices")
    public WsdlDefinition mergedServices() {
        return new WsdlDefinition() {
            @Override
            public Source getSource() {
                // Load the WSDL file from classpath and return it as a StreamSource
                Resource wsdlResource = new ClassPathResource("wsdl/mergedBinding.wsdl");
                try {
                    // Return the WSDL as a StreamSource (which is a subclass of Source)
                    return new StreamSource(wsdlResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load WSDL file", e);
                }
            }
        };
    }


}