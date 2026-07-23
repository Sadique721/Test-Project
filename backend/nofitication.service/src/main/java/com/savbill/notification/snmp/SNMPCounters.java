package com.savbill.notification.snmp;

public class SNMPCounters{

	//EmailConfig
	private static int getEmailConfigListSuccess = 0;
	private static int getEmailConfigListFailure = 0;
	private static int totalGetEmailConfigList=0;
	private static int updateEmailConfigSuccess = 0;
	private static int updateEmailConfigFailure = 0;
	private static int totalUpdateEmailConfig=0;
	private static int createEmailConfigSuccess = 0;
	private static int createEmailConfigFailure = 0;
	private static int totalCreateEmailConfig=0;
	private static int updateEmailConfigPasswordSuccess=0;
	private static int updateEmailConfigPasswordFailure=0;
	private static int totalUpdateEmailConfigPassword=0;

	
	//SMSConfig
	private static int getSmsConfigListSuccess = 0;
	private static int getSmsConfigListFailure = 0;
	private static int totalGetSmsConfig=0;
	private static int updateSmsConfigSuccess = 0;
	private static int updateSmsConfigFailure = 0;
	private static int totalUpdateSmsConfig=0;
	private static int createSmsConfigSuccess = 0;
	private static int createSmsConfigFailure = 0;
	private static int totalCreateSmsConfig=0;

	//EMail
	private static int sendEmailSuccess = 0;
	private static int sendEmailFailure = 0;
	private static int totalSendEmail=0;
	private static int getEmailListSuccess = 0;
	private static int getEmailListFailure = 0;
	private static int totalGetEmailList=0;
	private static int getEmailByIdSuccess = 0;
	private static int getEmailByIdFailure = 0;
	private	static int totalGetEmailById=0;
	private static int getEmailBySourceNameSuccess = 0;
	private static int getEmailBySourceNameFailure = 0;
	private static int totalGetEmailBySourceName=0;
	private static int createEmailSuccess= 0;
	private static int createEmailFailure= 0;
	private static int totalCreateEmail=0;
	private static int updateEmailSuccess= 0;
	private static int updateEmailFailure= 0;
	private static int totalUpdateEmail=0;
	private static int deleteEmailSuccess= 0;
	private static int deleteEmailFailure= 0;
	private static int totalDeleteEmail=0;
	
	//SMS
	private static int sendSmsSuccess = 0;
	private static int sendSmsFailure = 0;
	private	static int totalSendSms=0;
	private static int getSmsListSuccess = 0;
	private static int getSmsListFailure = 0;
	private static int totalGetSmsList=0;
	private static int getSmsByIdSuccess = 0;
	private static int getSmsByIdFailure = 0;
	private static int totalGetSmsById=0;
	private static int getSmsBySourceNameSuccess = 0;
	private static int getSmsBySourceNameFailure = 0;
	private static int totalGetSmsBySourceName=0;
	private static int createSmsSuccess= 0;
	private static int createSmsFailure= 0;
	private static int totalCreateSms=0;
	private static int updateSmsSuccess= 0;
	private static int updateSmsFailure= 0;
	private static int totalUpdateSms=0;
	private static int deleteSmsSuccess= 0;
	private static int deleteSmsFailure= 0;
	private static int totalDeleteSms=0;
	//SMS
	public void incrementSendSmsSuccess() {sendSmsSuccess++; }
	public void incrementSendSmsFailure() {sendSmsFailure++; }
	public void incrementTotalSendSms(){totalSendSms= sendSmsSuccess+sendSmsFailure; }
	public void incrementGetSmsListSuccess() { getSmsListSuccess++; }
	public void incrementGetSmsListFailure() { getSmsListFailure++; }
	public void incrementGetTotalSmsList()	{totalGetSmsList=getSmsListSuccess+getSmsListFailure; }
	public void incrementGetSmsByIdSuccess() { getSmsByIdSuccess++; }
	public void incrementGetSmsByIdFailure() { getSmsByIdFailure++; }
	public void incrementTotalGetSmsById(){totalGetSmsById=getSmsByIdSuccess+getSmsByIdFailure; }
	public void incrementGetSmsBySourceNameSuccess() { getSmsBySourceNameSuccess++; }
	public void incrementGetSmsBySourceNameFailure() { getSmsBySourceNameFailure++; }
	public void incrementTotalGetSmsBySourceName(){ totalGetSmsBySourceName=getSmsBySourceNameSuccess+getSmsBySourceNameFailure; }
	public void incrementCreateSmsSuccess() { createSmsSuccess++; }
	public void incrementCreateSmsFailure() { createSmsFailure++; }
	public void incrementTotalCreateSms(){totalCreateSms= createSmsSuccess+createSmsFailure;}
	public void incrementUpdateSmsSuccess() { updateSmsSuccess++; }
	public void incrementUpdateSmsFailure() { updateSmsFailure++; }
	public void incrementTotalUpdateSms(){totalUpdateSms=updateSmsSuccess+updateSmsFailure; }
	public void incrementDeleteSmsSuccess() { deleteSmsSuccess++; }
	public void incrementDeleteSmsFailure() { deleteSmsFailure++; }
	public void incrementTotalDeleteSms(){totalDeleteSms=deleteSmsSuccess+deleteSmsFailure; }
	//Email
	public void incrementSendEmailSuccess() {sendEmailSuccess++; }
	public void incrementSendEmailFailure() {sendEmailFailure++; }
	public void incrementTotalSendEmail(){totalSendEmail=sendEmailSuccess+sendEmailFailure;}
	public void incrementGetEmailListSuccess() { getEmailListSuccess++; }
	public void incrementGetEmailListFailure() { getEmailListFailure++; }
	public void incrementTotalGetEmailList(){totalGetEmailList=getEmailListSuccess+getEmailListFailure;}
	public void incrementGetEmailByIdSuccess() { getEmailByIdSuccess++; }
	public void incrementGetEmailByIdFailure() { getEmailByIdFailure++; }
	public void incrementTotalGetEmailById(){ totalGetEmailById=getEmailByIdSuccess+getEmailByIdFailure;}
	public void incrementGetEmailBySourceNameSuccess() { getEmailBySourceNameSuccess++; }
	public void incrementGetEmailBySourceNameFailure() { getEmailBySourceNameFailure++; }
	public void incrementTotalGetEmailBySourceName(){totalGetEmailBySourceName=getEmailBySourceNameSuccess+getEmailBySourceNameFailure;}
	public void incrementCreateEmailSuccess() { createEmailSuccess++; }
	public void incrementCreateEmailFailure() { createEmailFailure++; }
	public void incrementTotalCreateEmail(){totalCreateEmail=createEmailSuccess+createEmailFailure; }
	public void incrementUpdateEmailSuccess() { updateEmailSuccess++; }
	public void incrementUpdateEmailFailure() { updateEmailFailure++; }
	public void incrementTotalUpdateEmail(){totalUpdateEmail=updateEmailSuccess+updateEmailFailure;}
	public void incrementDeleteEmailSuccess() { deleteEmailSuccess++; }
	public void incrementDeleteEmailFailure() { deleteEmailFailure++; }
	public void incrementTotalDeleteEmail(){totalDeleteEmail=deleteEmailSuccess+deleteEmailFailure;}
	//SMSConfig
	public void incrementGetSmsConfigListSuccess() { getSmsConfigListSuccess++; }
	public void incrementGetSmsConfigListFailure() { getSmsConfigListFailure++; }
	public void incrementTotalGetSmsConfig()		{totalGetSmsConfig=getSmsConfigListSuccess+getSmsConfigListFailure; }
	public void incrementUpdateSmsConfigSuccess() { updateSmsConfigSuccess++; }
	public void incrementUpdateSmsConfigFailure() { updateSmsConfigFailure++; }
	public void incrementTotalUpdateSmsConfig()	  {totalUpdateSmsConfig= updateSmsConfigSuccess+updateSmsConfigFailure; }
	public void incrementCreateSmsConfigSuccess() { createSmsConfigSuccess++; }
	public void incrementCreateSmsConfigFailure() { createSmsConfigFailure++; }
	public void incrementTotalCreateSmsConfig()	   {totalCreateSmsConfig= createSmsConfigSuccess+createSmsConfigFailure; }
	//EmailConfig
	public void incrementGetEmailConfigListSuccess() { getEmailConfigListSuccess++; }
	public void incrementGetEmailConfigListFailure() { getEmailConfigListFailure++; }
	public void incrementTotalGetEmailConfigList(){totalGetEmailConfigList=getEmailConfigListSuccess+getEmailConfigListFailure;}
	public void incrementUpdateEmailConfigSuccess() { updateEmailConfigSuccess++; }
	public void incrementUpdateEmailConfigFailure() { updateEmailConfigFailure++; }
	public void incrementTotalUpdateEmailConfig()	{totalUpdateEmailConfig=updateEmailConfigSuccess+updateEmailConfigFailure; }
	public void incrementCreateEmailConfigSuccess() { createEmailConfigSuccess++; }
	public void incrementCreateEmailConfigFailure() { createEmailConfigFailure++; }
	public void incrementTotalCreateEmailConfig(){totalCreateEmailConfig=createEmailConfigSuccess+createEmailConfigFailure; }
	public void incrementUpdateEmailConfigPasswordSuccess(){ updateEmailConfigPasswordSuccess++; }
	public void incrementUpdateEmailConfigPasswordFailure(){ updateEmailConfigPasswordFailure++; }
	public void incrementTotalUpdateEmailConfigPassword(){ totalUpdateEmailConfigPassword=updateEmailConfigPasswordSuccess+updateEmailConfigPasswordFailure;}


	public SNMPCounters()
	{
		super();
	}
	
	public static Integer getCountForValue(String value)
	{
	   switch(value)
	         {
	             case  OIDConstant.GET_EMAIL_CONFIG_LIST_SUCCESS_OID                : return getEmailConfigListSuccess;
	             case  OIDConstant.GET_EMAIL_CONFIG_LIST_FAILURE_OID                 : return getEmailConfigListFailure;
				 case OIDConstant.TOTAL_GET_EMAIL_CONFIG_LIST_OID:					return  totalGetEmailConfigList;
	             case  OIDConstant.UPDATE_EMAIL_CONFIG_SUCCESS_OID			: return updateEmailConfigSuccess;
	             case  OIDConstant.UPDATE_EMAIL_CONFIG_FAILURE_OID			: return updateEmailConfigFailure;
				 case OIDConstant.TOTAL_UPDATE_EMAIL_CONFIG_OID:				return totalUpdateEmailConfig;
	             case  OIDConstant.CREATE_EMAIL_CONFIG_SUCCESS_OID			: return createEmailConfigSuccess;
	             case  OIDConstant.CREATE_EMAIL_CONFIG_FAILURE_OID			: return createEmailConfigFailure;
				 case OIDConstant.TOTAL_CREATE_EMAIL_CONFIG_OID:				return totalCreateEmailConfig;
				 case OIDConstant.UPDATE_EMAIL_CONFIG_PASSWORD_SUCCESS_OID:		return updateEmailConfigPasswordSuccess;
				 case OIDConstant.UPDATE_EMAIL_CONFIG_PASSWORD_FAILURE_OID:		return updateEmailConfigPasswordFailure;
				 case OIDConstant.TOTAL_UPDATE_EMAIL_CONFIG_PASSWORD_OID:		return totalUpdateEmailConfigPassword;
	             case  OIDConstant.GET_SMS_CONFIG_LIST_SUCCESS_OID			: return getSmsConfigListSuccess;
	             case  OIDConstant.GET_SMS_CONFIG_LIST_FAILURE_OID			: return getSmsConfigListFailure;
				 case OIDConstant.TOTAL_GET_SMS_CONFIG_LIST_OID:			  return totalGetSmsConfig;
	             case  OIDConstant.UPDATE_SMS_CONFIG_SUCCESS_OID : return updateSmsConfigSuccess;
	             case  OIDConstant.UPDATE_SMS_CONFIG_FAILURE_OID	: return updateSmsConfigFailure;
				 case OIDConstant.TOTAL_UPDATE_SMS_CONFIG_OID:        return totalUpdateSmsConfig;
	             case  OIDConstant.CREATE_SMS_CONFIG_SUCCESS_OID : return createSmsConfigSuccess;
	             case  OIDConstant.CREATE_SMS_CONFIG_FAILURE_OID	: return createSmsConfigFailure;
				 case OIDConstant.TOTAL_CREATE_SMS_CONFIG_OID:        return totalCreateSmsConfig;
	             
	             case  OIDConstant.SEND_EMAIL_SUCCESS_OID	: return sendEmailSuccess;
	             case  OIDConstant.SEND_EMAIL_FAILURE_OID	: return sendEmailFailure;
				 case 	OIDConstant.TOTAL_SEND_EMAIL_OID:		return totalSendEmail;
	             case  OIDConstant.GET_EMAIL_LIST_SUCCESS_OID	: return getEmailListSuccess;
	             case  OIDConstant.GET_EMAIL_LIST_FAILURE_OID	: return getEmailListFailure;
				 case OIDConstant.TOTAL_GET_EMAIL_LIST_OID:			return totalGetEmailList;
	             case  OIDConstant.GET_EMAIL_BY_ID_SUCCESS_OID	: return getEmailByIdSuccess;
	             case  OIDConstant.GET_EMAIL_BY_ID_FAILURE_OID	: return getEmailByIdFailure;
				 case OIDConstant.TOTAL_GET_EMAIL_BY_ID_OID:	  return totalGetEmailById;
	             case  OIDConstant.GET_EMAIL_BY_SOURCENAME_SUCCESS_OID	: return getEmailBySourceNameSuccess;
	             case  OIDConstant.GET_EMAIL_BY_SOURCENAME_FAILURE_OID	: return getEmailBySourceNameFailure;
				 case OIDConstant.TOTAL_GET_EMAIL_LIST_BY_SOURCENAME_OID:	return totalGetEmailBySourceName;
	             case  OIDConstant.CREATE_EMAIL_SUCCESS_OID	: return createEmailSuccess;
	             case  OIDConstant.CREATE_EMAIL_FAILURE_OID	: return createEmailFailure;
				 case OIDConstant.TOTAL_CREATE_EMAIL_LIST_OID:	return totalCreateEmail;
	             case  OIDConstant.UPDATE_EMAIL_SUCCESS_OID	: return updateEmailSuccess;
	             case  OIDConstant.UPDATE_EMAIL_FAILURE_OID	: return updateEmailSuccess;
				 case OIDConstant.TOTAL_UPDATE_EMAIL_LIST_OID:	return totalUpdateEmail;
	             case  OIDConstant.DELETE_EMAIL_SUCCESS_OID	: return deleteEmailSuccess;
	             case  OIDConstant.DELETE_EMAIL_FAILURE_OID	: return deleteEmailSuccess;
				 case OIDConstant.TOTAL_DELETE_EMAIL_LIST_OID:return totalDeleteEmail;
	             
	             case  OIDConstant.SEND_SMS_SUCCESS_OID	: return sendSmsSuccess;
	             case  OIDConstant.SEND_SMS_FAILURE_OID	: return sendSmsFailure;
				 case OIDConstant.TOTAL_SEND_SMS_OID:		return totalSendSms;
	             case  OIDConstant.GET_SMS_LIST_SUCCESS_OID	: return getSmsListSuccess;
	             case  OIDConstant.GET_SMS_LIST_FAILURE_OID	: return getSmsListFailure;
				 case OIDConstant.TOTAL_GET_SMS_LIST_OID:		return totalGetSmsList;
	             case  OIDConstant.GET_SMS_BY_ID_SUCCESS_OID	: return getSmsByIdSuccess;
	             case  OIDConstant.GET_SMS_BY_ID_FAILURE_OID	: return getSmsByIdFailure;

				 case OIDConstant.TOTAL_GET_SMS_BY_ID_OID:			return totalGetSmsById;
	             case  OIDConstant.GET_SMS_BY_SOURCENAME_SUCCESS_OID	: return getSmsBySourceNameSuccess;
	             case  OIDConstant.GET_SMS_BY_SOURCENAME_FAILURE_OID	: return getSmsBySourceNameFailure;
				 case OIDConstant.TOTAL_GET_SMS_LIST_BY_SOURCENAME_OID:	return totalGetSmsBySourceName;
	             case  OIDConstant.CREATE_SMS_SUCCESS_OID	: return createSmsSuccess;
	             case  OIDConstant.CREATE_SMS_FAILURE_OID	: return createSmsFailure;
				 case OIDConstant.TOTAL_CREATE_SMS_LIST_OID:	return totalCreateSms;
	             case  OIDConstant.UPDATE_SMS_SUCCESS_OID	: return updateSmsSuccess;
	             case  OIDConstant.UPDATE_SMS_FAILURE_OID	: return updateSmsSuccess;
				 case OIDConstant.TOTAL_UPDATE_SMS_LIST_OID: return totalUpdateSms;
	             case  OIDConstant.DELETE_SMS_SUCCESS_OID	: return deleteSmsSuccess;
	             case  OIDConstant.DELETE_SMS_FAILURE_OID	: return deleteSmsSuccess;
				 case OIDConstant.TOTAL_DELETE_SMS_LIST_OID:return totalDeleteSms;
	             default                                        : return null                         ;	 
	             
	         }
	}

}
