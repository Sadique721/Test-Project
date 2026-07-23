package utility;

import java.util.Properties;

public class ModuleControlConstant {
	
	static Properties prop = Utility.loadProperties("migration_module.properties");
		
	//######Master-Data
	public static final boolean MASTERDATA_MIGRATION = Boolean.valueOf(prop.getProperty("MASTERDATA_MIGRATION"));
	public static final boolean COUNTRY = Boolean.valueOf(prop.getProperty("COUNTRY"));
	public static final boolean PROVINCE = Boolean.valueOf(prop.getProperty("PROVINCE"));
	public static final boolean DISTRICT = Boolean.valueOf(prop.getProperty("DISTRICT"));
	public static final boolean MUNCIPILITY = Boolean.valueOf(prop.getProperty("MUNCIPILITY"));
	public static final boolean SERVICEAREA = Boolean.valueOf(prop.getProperty("SERVICEAREA"));
    public static final boolean SERVICEAREACLASS = Boolean.valueOf(prop.getProperty("SERVICEAREACLASS"));
	public static final boolean WARD = Boolean.valueOf(prop.getProperty("WARD"));
    public static final boolean WARDCLASS = Boolean.valueOf(prop.getProperty("WARDCLASS"));
	public static final boolean INVESTMENTCODE = Boolean.valueOf(prop.getProperty("INVESTMENTCODE"));
	public static final boolean BRANCH = Boolean.valueOf(prop.getProperty("BRANCH"));
	public static final boolean BUSINESSUNIT = Boolean.valueOf(prop.getProperty("BUSINESSUNIT"));
	public static final boolean SUBBUSINESSUNIT = Boolean.valueOf(prop.getProperty("SUBBUSINESSUNIT"));
	public static final boolean REGION = Boolean.valueOf(prop.getProperty("REGION"));
	public static final boolean BUSINESSVERTICAL = Boolean.valueOf(prop.getProperty("BUSINESSVERTICAL"));
	public static final boolean SUBBUSINESSVERTICAL = Boolean.valueOf(prop.getProperty("SUBBUSINESSVERTICAL"));

    public static final boolean DEPARTMENT_MANAGEMENT = Boolean.valueOf(prop.getProperty("DEPARTMENT_MANAGEMENT"));

    public static final boolean OLT = Boolean.valueOf(prop.getProperty("OLT"));

    // new development   SUBAREA
	public static final boolean SUBAREA = Boolean.valueOf(prop.getProperty("SUBAREA"));

    public static final boolean SUBAREACLASS = Boolean.valueOf(prop.getProperty("SUBAREACLASS"));
	// BUILDING
	public static final boolean BUILDING = Boolean.valueOf(prop.getProperty("BUILDING"));

    public static final boolean BUILDINGCLASS = Boolean.valueOf(prop.getProperty("BUILDINGCLASS"));
    public static final boolean BUILDINGHOMEPASSCLASS = Boolean.valueOf(prop.getProperty("BUILDINGHOMEPASSCLASS"));

	//#loction
	public static final boolean LOCATION_MIGRATION = Boolean.valueOf(prop.getProperty("LOCATION_MIGRATION"));
	public static final boolean LOCATION = Boolean.valueOf(prop.getProperty("LOCATION"));
	
	//#######Staff-Data
	public static final boolean STAFFDATA_MIGRATION = Boolean.valueOf(prop.getProperty("STAFFDATA_MIGRATION"));
	public static final boolean TEAM = Boolean.valueOf(prop.getProperty("TEAM"));
	public static final boolean STAFF = Boolean.valueOf(prop.getProperty("STAFF"));
		
	
	//######Plan-Data
	public static final boolean PLANDATA_MIGRATION = Boolean.valueOf(prop.getProperty("PLANDATA_MIGRATION"));
	public static final boolean PLANSERVICE = Boolean.valueOf(prop.getProperty("PLANSERVICE"));
	public static final boolean PLANTAX = Boolean.valueOf(prop.getProperty("PLANTAX"));
	public static final boolean PLANCHARGE = Boolean.valueOf(prop.getProperty("PLANCHARGE"));
	public static final boolean PLANQOS = Boolean.valueOf(prop.getProperty("PLANQOS"));
	public static final boolean PREPAIDPLAN = Boolean.valueOf(prop.getProperty("PREPAIDPLAN"));
	public static final boolean PLANBUNDLE = Boolean.valueOf(prop.getProperty("PLANBUNDLE"));
	
	
	//Act Base Plan 
	public static final boolean PREPAIDBASEPLAN = Boolean.valueOf(prop.getProperty("PREPAIDBASEPLAN"));
	public static final boolean PREPAIDVODPLAN = Boolean.valueOf(prop.getProperty("PREPAIDVODPLAN"));
	public static final boolean PREPAIDBODPLAN = Boolean.valueOf(prop.getProperty("PREPAIDBODPLAN"));
	
	public static final boolean BASEQOS = Boolean.valueOf(prop.getProperty("BASEQOS"));
	public static final boolean BANDWIDTHQOS = Boolean.valueOf(prop.getProperty("BANDWIDTHQOS"));
	
	//######PartnerPlanBundle
	public static final boolean PARTNERDATA_MIGRATION = Boolean.valueOf(prop.getProperty("PARTNERDATA_MIGRATION"));
	public static final boolean PARTNERPLANBUNDLE = Boolean.valueOf(prop.getProperty("PARTNERPLANBUNDLE"));
	public static final boolean PARTNER = Boolean.valueOf(prop.getProperty("PARTNER"));
	
	
	//#######Customers-Data
	public static final boolean CUSTOMERDATA_MIGRATION = Boolean.valueOf(prop.getProperty("CUSTOMERDATA_MIGRATION"));
	public static final boolean CUSTOMER = Boolean.valueOf(prop.getProperty("CUSTOMER"));
	public static final boolean RECORDPAYMENT = Boolean.valueOf(prop.getProperty("RECORDPAYMENT"));
	public static final boolean CREDITNOTE = Boolean.valueOf(prop.getProperty("CREDITNOTE"));
	public static final boolean ASSIGNINVENTORY_CUSTOMER = Boolean.valueOf(prop.getProperty("ASSIGNINVENTORY_CUSTOMER"));
	public static final boolean UPLOADDOCUMENT = Boolean.valueOf(prop.getProperty("UPLOADDOCUMENT"));
	public static final boolean RENEWPLAN = Boolean.valueOf(prop.getProperty("RENEWPLAN"));
	
	
	
	
	//#######Inventory-Data
	public static final boolean INVENTORYDATA_MIGRATION = Boolean.valueOf(prop.getProperty("INVENTORYDATA_MIGRATION"));
	public static final boolean MANUFACTURER = Boolean.valueOf(prop.getProperty("MANUFACTURER"));
	public static final boolean PRODUCTCATEGORY = Boolean.valueOf(prop.getProperty("PRODUCTCATEGORY"));
	public static final boolean PRODUCT = Boolean.valueOf(prop.getProperty("PRODUCT"));
	public static final boolean POP = Boolean.valueOf(prop.getProperty("POP"));
	public static final boolean WAREHOUSE = Boolean.valueOf(prop.getProperty("WAREHOUSE"));
	public static final boolean INWARD = Boolean.valueOf(prop.getProperty("INWARD"));
	public static final boolean OUTWARD = Boolean.valueOf(prop.getProperty("OUTWARD"));
	public static final boolean ASSIGN_INVENTORY_POP = Boolean.valueOf(prop.getProperty("ASSIGN_INVENTORY_POP"));
	public static final boolean ASSIGN_INVENTORY_SERVICEAREA = Boolean.valueOf(prop.getProperty("ASSIGN_INVENTORY_SERVICEAREA"));
	
	
	//#######Inventory-Data
	public static final boolean TICKETDATA_MIGRATION = Boolean.valueOf(prop.getProperty("TICKETDATA_MIGRATION"));
	public static final boolean TAT = Boolean.valueOf(prop.getProperty("TAT"));
	public static final boolean PROBLEMDOMAIN = Boolean.valueOf(prop.getProperty("PROBLEMDOMAIN"));
	public static final boolean SUBPROBLEMDOMAIN = Boolean.valueOf(prop.getProperty("SUBPROBLEMDOMAIN"));
	public static final boolean ROOTCAUSE = Boolean.valueOf(prop.getProperty("ROOTCAUSE"));
	public static final boolean TICKET = Boolean.valueOf(prop.getProperty("TICKET"));
	public static final boolean TICKETPOSTPUT = Boolean.valueOf(prop.getProperty("TICKETPOSTPUT"));

	//########LeadSource-Data
	public static final boolean SALESCRMDATA_MIGRATION = Boolean.valueOf(prop.getProperty("SALESCRMDATA_MIGRATION"));
	public static final boolean LEADSOURCEMASTER = Boolean.valueOf(prop.getProperty("LEADSOURCEMASTER"));
	public static final boolean REJECTEDREASONMASTER = Boolean.valueOf(prop.getProperty("REJECTEDREASONMASTER"));
	//LEADCREATE
	public static final boolean LEADCREATE = Boolean.valueOf(prop.getProperty("LEADCREATE"));
	// Network
	public static final boolean NETWORK_MIGRATION = Boolean.valueOf(prop.getProperty("NETWORK_MIGRATION"));
	public static final boolean NETWORK  = Boolean.valueOf(prop.getProperty("NETWORK"));

    //WORKFLOW
    public static final boolean WORKFLOW_MIGRATION = Boolean.valueOf(prop.getProperty("WORKFLOW_MIGRATION"));


	//netconf customer
		public static final boolean NETCONF_MIGRATION = Boolean.valueOf(prop.getProperty("NETCONF_MIGRATION"));
		public static final boolean CUSTOMERNETCONF = Boolean.valueOf(prop.getProperty("CUSTOMERNETCONF"));
		
		//Act customer
		public static final boolean ACT_MIGRATION = Boolean.valueOf(prop.getProperty("ACT_MIGRATION"));
		public static final boolean CUSTOMERACT = Boolean.valueOf(prop.getProperty("CUSTOMERACT"));
		
		// Act customer addon plan-->
		public static final boolean ADDONCUSTOMER = Boolean.valueOf(prop.getProperty("ADDONCUSTOMER"));
		public static final boolean ADDON = Boolean.valueOf(prop.getProperty("ADDON"));

		
		//savana control
		public static final boolean SAVANNA_MIGRATION = Boolean.valueOf(prop.getProperty("SAVANNA_MIGRATION"));
		public static final boolean CUSTOMERSAVANA = Boolean.valueOf(prop.getProperty("CUSTOMERSAVANA"));
		public static final boolean PREPAIDCUSTOMERSAVANA = Boolean.valueOf(prop.getProperty("PREPAIDCUSTOMERSAVANA"));
		public static final boolean PREPAIDPARENTCUSTOMERSAVANA = Boolean.valueOf(prop.getProperty("PREPAIDPARENTCUSTOMERSAVANA"));
		public static final boolean ASSIGNINVENTORY_SAVANNA_CUSTOMER = Boolean.valueOf(prop.getProperty("ASSIGNINVENTORY_SAVANNA_CUSTOMER"));
		public static final boolean RECORD_PAYMENT_CUSTOMER = Boolean.valueOf(prop.getProperty("RECORD_PAYMENT_CUSTOMER"));
		public static final boolean RECORD_PAYMENT_CAF_CUSTOMER = Boolean.valueOf(prop.getProperty("RECORD_PAYMENT_CAF_CUSTOMER"));
        public static final boolean RECORD_PAYMENT_CUSTOMER_BACKDATE = Boolean.valueOf(prop.getProperty("RECORD_PAYMENT_CUSTOMER_BACKDATE"));
        public static final boolean CHECK_PAYMENT_RECORD_BY_REFERENCE_NUMBER = Boolean.valueOf(prop.getProperty("CHECK_PAYMENT_RECORD_BY_REFERENCE_NUMBER"));

		public static final boolean CAFCUSTOMERSAVANA = Boolean.valueOf(prop.getProperty("CAFCUSTOMERSAVANA"));
		public static final boolean CAFCUSTOMERCLASSSAVANNA = Boolean.valueOf(prop.getProperty("CAFCUSTOMERCLASSSAVANNA"));
		public static final boolean CUSTOMERDIRECTCHARGE = Boolean.valueOf(prop.getProperty("CUSTOMERDIRECTCHARGE"));
		public static final boolean CAF_CUSTOMERDIRECTCHARGE = Boolean.valueOf(prop.getProperty("CAF_CUSTOMERDIRECTCHARGE"));
		// SAVANNA_PAYMENT
		public static final boolean SAVANNA_PAYMENT = Boolean.valueOf(prop.getProperty("SAVANNA_PAYMENT"));
		
		// savana database --> data bse updation 
		public static final boolean CUSTOMERDATABASE = Boolean.valueOf(prop.getProperty("CUSTOMERDATABASE"));
		
		
		// Tumil migration TUMIL_MIGRATION
		public static final boolean TUMIL_MIGRATION = Boolean.valueOf(prop.getProperty("TUMIL_MIGRATION"));
		public static final boolean CUSTOMERTUMIL = Boolean.valueOf(prop.getProperty("CUSTOMERTUMIL"));
		// CAFCUSTOMERTUMIL
		public static final boolean CAFCUSTOMERTUMIL = Boolean.valueOf(prop.getProperty("CAFCUSTOMERTUMIL"));
		public static final boolean ASSIGNINVENTORY_TUMIL_CUSTOMER = Boolean.valueOf(prop.getProperty("ASSIGNINVENTORY_TUMIL_CUSTOMER"));
		public static final boolean DIRECTCHARGE = Boolean.valueOf(prop.getProperty("DIRECTCHARGE"));
		public static final boolean RECORD_PAYMENT_CUSTOMER_TUMIL = Boolean.valueOf(prop.getProperty("RECORD_PAYMENT_CUSTOMER_TUMIL"));

    //######Department Management

}
