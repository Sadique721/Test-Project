package utility;

import java.util.Properties;

public class Constant {
	
	static Properties prop = Utility.loadProperties("migration.properties");
	
	public static final String BASE_PATH = System.getProperty("user.dir");
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	
	public static final String MASTERDATA_FILE = prop.getProperty("MASTERDATA_FILE");
	public static final String TICKETDATA_FILE = prop.getProperty("TICKETDATA_FILE");
	public static final String SALES_CRM_DATA_FILE = prop.getProperty("SALESCRMDATA_FILE");
	public static final String PLANDATA_FILE = prop.getProperty("PLANDATA_FILE");
	public static final String PARTNERDATA_FILE = prop.getProperty("PARTNERDATA_FILE");
	public static final String DEMOGRAPHIC_DATA_FILE = prop.getProperty("DEMOGRAPHIC_DATA_FILE");
	public static final String INVENTORY_DATA_FILE = prop.getProperty("INVENTORY_DATA_FILE");
	public static final String CUSTOMER_DATA_FILE = prop.getProperty("CUSTOMER_DATA_FILE");
	public static final String LOCATION_DATA_FILE = prop.getProperty("LOCATION_DATA_FILE");

    //Department Management
    public static final String DEPARTMENT_MANAGEMENT_DATA_FILE = prop.getProperty("DEPARTMENT_MANAGEMENT_DATA_FILE");

    public static final String OLT = prop.getProperty("OLT");

	// savana file read
	public static final String	SAVANACUSTOMER_FILE=prop.getProperty("SAVANACUSTOMER_FILE");

	public static final String	SAVANAPARENTCUSTOMER_FILE=prop.getProperty("SAVANAPARENTCUSTOMER_FILE");

	public static final String	DAILY_PAYMENTS_CHECK=prop.getProperty("DAILY_PAYMENTS_CHECK");

	// payment 
	public static final String PAYMENTFILE = prop.getProperty("PAYMENTFILE");
	//MNetwork file read
	public static final String NETWORK_DATA_FILE = prop.getProperty("NETWORK_DATA_FILE");
	
	
	
	//net conf
	public static final String NETCONF_DATA_FILE = prop.getProperty("NETCONF_DATA_FILE");
	
	
	//ACT FILE
		public static final String ACTCUSTOMER_DATA_FILE = prop.getProperty("ACTCUSTOMER_DATA_FILE");
		
		public static final String ACTCUSTOMERCSV = prop.getProperty("ACTCUSTOMERCSV");
		
		public static final String ACTCUSTOMER_ADDON_DATA_FILE = prop.getProperty("ACTCUSTOMER_ADDON_DATA_FILE");
		public static final String CSVADDON = prop.getProperty("CSVADDON");
		
		public static final String ACTPLAN_DATA_FILE = prop.getProperty("ACTPLAN_DATA_FILE");
	
		
		// TUMIL SHEET TUMIL_FILE
		public static final String TUMIL_FILE = prop.getProperty("TUMIL_FILE");
		
		
		
	//public static String AUTHENTICATION ="";
	
	public static final String API_URL = prop.getProperty("API_URL");
	public static final String STAFF_USERNAME = prop.getProperty("STAFF_USERNAME");
	public static final String STAFF_PASSWORD = prop.getProperty("STAFF_PASSWORD");
	
	
	//public static final int TOTAL_THREADS = Integer.parseInt(prop.getProperty("CUSTOMER_GENERATION_THREADS"));
	public static final int TOTAL_THREADS = 8; //here thread is 1
	
	
	//Act Thread implement Call from property file.
	public static final int THREAD_POOL_SIZE =Integer.parseInt( prop.getProperty("THREAD_POOL_SIZE")); //here thread is 1
	public static final int BATCH_SIZE =Integer.parseInt( prop.getProperty("BATCH_SIZE")); //here thread is 1
	public static final int RETRY_LIMIT =Integer.parseInt( prop.getProperty("RETRY_LIMIT")); //here thread is 1
	public static final int RETRY_DELAY_MS =Integer.parseInt( prop.getProperty("RETRY_DELAY_MS")); //here thread is 1
	
	public static final String PREPAID_CUSTOMER_SCHEDULER = "CustomerSchedulerExecutionStats.txt";
	public static final String RECORD_PAYMENT_SCHEDULER = "RecordPaymentSchedulerExecutionStats.txt";
	
	// convert csv to xlsx 
	
	public static final String CSV = prop.getProperty("CSV");
	public static final String XLSX = prop.getProperty("XLSX");
	
	// database --------------------------------------------------------------------->
	public static final String URLCOMMON = prop.getProperty("URLCOMMON");
	public static final String URLCONVERGE = prop.getProperty("URLCONVERGE");
	public static final String URLREVENUE = prop.getProperty("URLREVENUE");

    public static final String URLTICKETMANAGEMENT = prop.getProperty("URLTICKETMANAGEMENT");

    public static final String URLPAYMENTCPM = prop.getProperty("URLPAYMENTCPM");
    public static final String URLPAYMENTREV = prop.getProperty("URLPAYMENTREV");

    public static final String URLCUSTDCPM = prop.getProperty("URLCUSTDCPM");
    public static final String URLCUSTDREV = prop.getProperty("URLCUSTDREV");


	public static final String USERNAME = prop.getProperty("USERNAME");
	public static final String PASSWORD = prop.getProperty("PASSWORD");
	
	public static final int LIMIT =Integer.parseInt( prop.getProperty("LIMIT"));
	public static final int BATCH =Integer.parseInt( prop.getProperty("BATCH"));
	//------------------------------------------------------------------------------------>>	

    public static final String WORKFLOWFILE = prop.getProperty("WORKFLOW_FILE");

}
