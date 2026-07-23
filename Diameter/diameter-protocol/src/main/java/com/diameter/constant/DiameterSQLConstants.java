package com.diameter.constant;

public class DiameterSQLConstants {

	public static final String CHECK_USERNAME_EXISTS = "SELECT COUNT(*) FROM tblcustomers WHERE username = ? and MVNOID = ?";

    public static final String CHECK_USERNAME_EXISTS_FOR_UPDATE = "SELECT COUNT(*) FROM tblcustomers WHERE username = ? and MVNOID = ? AND custid != ?";

    public static final String INSERT_CUSTOMER = "INSERT INTO tblcustomers (custid, username, password, firstname, lastname, email, cstatus, last_login_time, failcount,last_password_change, accountnumber, accounttype, birthdate, country, cui, customertype,gender, imsi, phone, subscriberpackage, subscriberpackageid, createdate, expirydate,laststatuschangedate, NEXTBILLDATE, LASTBILLDATE, BILLDAY, outstandingbalance, partnerid,ASNNumber, BNGRouterInterface, BNGRouterName, IPPrefixes, IPV6Prefixes, LANIP, LANIPV6,LLAccountID, LLConnectionType, LLExpiryDate, LLMedium, LLServiceID, MACADDRESS, PeerIP,POOLIP, QOS, RDExport, RDValue, VLANID, VRFName, VSIID, VSIName, WANIP, WANIPV6,billentityname, purchaseorder, remarks, addparam1, addparam2, addparam3, addparam4,parentcustid, invoiceoption, oldpassword1, oldpassword2, oldpassword3, firstactivationdate,allowedipaddrs, selfcarepwd, title, custname, contactperson, cafno, pan, gst, aadhar,mactelflag, mobile, altmobile, altphone, altemail, fax, resellerid, salesrepid, deposit,voicesrvtype, didno, childdidno, intercomno, intercomgrp, onlinerenewalflag, voipenableflag,custcategory, walletbalance, networktype, oltslotid, oltportid, strconntype, stroltname,strslotname, strportname, createdbystaffid, lastmodifiedbystaffid, servicearea_id,network_device_id, onuid, is_deleted, defaultpoolid, otp, otpvalidate, createbyname,updatebyname, lastmodifieddate, latitude, longitude, url, gis_code, voiceProvision,salesremark, servicetype, previous_caf_approver, next_caf_approver, caf_approve_status,MVNOID, calendartype, invoice_type, nas_port, framed_ip, framed_ip_bind, ip_pool_name_bind,BUID, maxconcurrentsession, gatewayip, skipnetconf, rdimport, mvno_deactivation_flag,ipv4, ipv6, vlan, nas_port_id, nas_ip_address, framed_ipv6_address, delegatedprefix,framedroute, mac_provision, mac_auth_enable, framed_ip_netmask, framed_ipv6_prefix,primary_dns, primary_ipv6_dns, secondary_ipv6_dns, secondary_dns, macretentionperiod,macretentionunit, nextquotaresetdate, blockno, isinvoicestop ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_CUSTOMER = "UPDATE tblcustomers SET username=?, password=?, firstname=?, lastname=?, email=?, cstatus=?, last_login_time=?, failcount=?, last_password_change=?, accountnumber=?, accounttype=?, birthdate=?, country=?, cui=?, customertype=?, gender=?, imsi=?, phone=?, subscriberpackage=?, subscriberpackageid=?, createdate=?, expirydate=?, laststatuschangedate=?, NEXTBILLDATE=?, LASTBILLDATE=?, BILLDAY=?, outstandingbalance=?, partnerid=?, ASNNumber=?, BNGRouterInterface=?, BNGRouterName=?, IPPrefixes=?, IPV6Prefixes=?, LANIP=?, LANIPV6=?, LLAccountID=?, LLConnectionType=?, LLExpiryDate=?, LLMedium=?, LLServiceID=?, MACADDRESS=?, PeerIP=?, POOLIP=?, QOS=?, RDExport=?, RDValue=?, VLANID=?, VRFName=?, VSIID=?, VSIName=?, WANIP=?, WANIPV6=?, billentityname=?, purchaseorder=?, remarks=?, addparam1=?, addparam2=?, addparam3=?, addparam4=?, parentcustid=?, invoiceoption=?, oldpassword1=?, oldpassword2=?, oldpassword3=?, firstactivationdate=?, allowedipaddrs=?, selfcarepwd=?, title=?, custname=?, contactperson=?, cafno=?, pan=?, gst=?, aadhar=?, mactelflag=?, mobile=?, altmobile=?, altphone=?, altemail=?, fax=?, resellerid=?, salesrepid=?, deposit=?, voicesrvtype=?, didno=?, childdidno=?, intercomno=?, intercomgrp=?, onlinerenewalflag=?, voipenableflag=?, custcategory=?, walletbalance=?, networktype=?, oltslotid=?, oltportid=?, strconntype=?, stroltname=?, strslotname=?, strportname=?, createdbystaffid=?, lastmodifiedbystaffid=?, servicearea_id=?, network_device_id=?, onuid=?, is_deleted=?, defaultpoolid=?, otp=?, otpvalidate=?, createbyname=?, updatebyname=?, lastmodifieddate=?, latitude=?, longitude=?, url=?, gis_code=?, voiceProvision=?, salesremark=?, servicetype=?, previous_caf_approver=?, next_caf_approver=?, caf_approve_status=?, MVNOID=?, calendartype=?, invoice_type=?, nas_port=?, framed_ip=?, framed_ip_bind=?, ip_pool_name_bind=?, BUID=?, maxconcurrentsession=?, gatewayip=?, skipnetconf=?, rdimport=?, mvno_deactivation_flag=?, ipv4=?, ipv6=?, vlan=?, nas_port_id=?, nas_ip_address=?, framed_ipv6_address=?, delegatedprefix=?, framedroute=?, mac_provision=?, mac_auth_enable=?, framed_ip_netmask=?, framed_ipv6_prefix=?, primary_dns=?, primary_ipv6_dns=?, secondary_ipv6_dns=?, secondary_dns=?, macretentionperiod=?, macretentionunit=?, nextquotaresetdate=?, blockno=?, isinvoicestop=? WHERE custid=?";

	public static final String CHECK_QUOTA_EXISTS = "SELECT COUNT(*) FROM tblcustquotadtls WHERE custid = ? AND planid = ? AND custpackageid = ?";
	
	public static final String INSERT_QUOTA = "INSERT INTO tblcustquotadtls (quotadtlsid, custid, planid, quotatype, totalquota, usedquota, quotaunit,timetotalquota, timequotaused, timequotaunit, CREATEDBYSTAFFID, createdate,lastmodifiedbystaffid, lastmodifieddate, is_deleted, totalquotakb, usedquotakb,timeusedquotasec, timetotalquotasec, custpackageid, didtotalquota, didusedquota,intercomtotalquota, intercomusedquota, did_quota_unit, intercom_quota_unit,createbyname, updatebyname, speeddowngradeflag, isfupapllied, fupapplieddate,currentsessionusagetime, currentsessionusagevolume, parnet_quota_type,is_chunk_available, reserved_quota_in_per, total_reserved_quota,usage_quota_type, skip_quota_update, last_quota_reset, isquotaupdateskipped) VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?, ?)";
	
	public static final String GET_ALL_CUSTOMERS = "SELECT * FROM tblcustomers WHERE is_deleted = 0";
	
	public static final String GET_QUOTA_BY_CUSTOMER_IDs = "SELECT * FROM tblcustquotadtls WHERE custid IN (%s) AND is_deleted = 0";
	
	public static final String GET_CUSTOMER_BY_ID = "SELECT * FROM tblcustomers WHERE custid = ? AND is_deleted = 0";
	
	public static final String GET_CUSTOMER_BY_NAME = "SELECT * FROM tblcustomers WHERE custname = ? AND is_deleted = 0";

	public static final String GET_CUSTOMER_BY_USERNAME =
			"SELECT t.* FROM tblcustomers t " +
					"INNER JOIN tbltcustomerservicemapping t1 ON t.custid = t1.custid " +
					"WHERE t1.imsi = ? AND t1.is_deleted = 0 AND t1.status='Active' AND t.cstatus='Active' AND t.is_deleted = 0 LIMIT 1";

	public static final String GET_CUSTOMER_BY_PHONE =
			"SELECT t.* FROM tblcustomers t " +
					"INNER JOIN tbltcustomerservicemapping t1 ON t.custid = t1.custid " +
					"WHERE t1.msisdn = ? AND t1.is_deleted = 0 AND t1.status='Active' AND t.cstatus='Active' AND t.is_deleted = 0 LIMIT 1";
	
	public static final String GET_QUOTA_BY_CUSTOMER_ID = "SELECT * FROM tblcustquotadtls WHERE custid = ? AND is_deleted = 0 ORDER BY quotadtlsid DESC LIMIT 1";
	
	public static final String GET_QUOTA_BY_CUSTOMER_ID_AND_PLAN_ID = "SELECT * FROM tblcustquotadtls WHERE custid = ? AND planid = ? AND is_deleted = 0 ORDER BY quotadtlsid DESC LIMIT 1";

	public static final String GET_QUOTA_BY_CUSTOMER_ID_PLAN_ID_AND_PACKAGE_ID = "SELECT * FROM tblcustquotadtls WHERE custid = ? AND planid = ? AND custpackageid = ? AND is_deleted = 0 ORDER BY quotadtlsid DESC LIMIT 1";

	public static final String GET_ACTIVE_PACKAGE_BY_CUSTOMER_ID =
			"SELECT cpm.* FROM tblcustpackagerel cpm " +
					"WHERE cpm.custid = ? " +
					"AND cpm.is_delete = 0 " +
					"AND cpm.service NOT IN ('FTTH') " +
					"AND cpm.cust_plan_status = 'Active' " +
					"ORDER BY cpm.startdate DESC, cpm.custpackageid DESC LIMIT 1";
	
	public static final String GET_ACTIVE_PACKAGE_BY_CUSTOMER_ID_AND_SUBSCRIBER =
			"SELECT cpm.* FROM tblcustpackagerel cpm " +
					"INNER JOIN tbltcustomerservicemapping sm " +
					"ON sm.id = cpm.custservicemappingid AND sm.custid = cpm.custid " +
					"WHERE cpm.custid = ? " +
					"AND sm.is_deleted = 0 " +
					"AND (sm.imsi = ? OR sm.msisdn = ?) " +
					"AND cpm.is_delete = 0 " +
					"AND sm.status= 'Active' " +
					"AND cpm.startdate <= CURRENT_TIMESTAMP " +
					"AND cpm.enddate > CURRENT_TIMESTAMP " +
					"ORDER BY cpm.startdate DESC, cpm.custpackageid DESC LIMIT 1";
	
	public static final String UPDATE_QUOTA_BY_CUSTOMER_ID = "UPDATE tblcustquotadtls SET usedquota = usedquota + ? WHERE custid = ? AND planid = ? AND custpackageid = ? AND is_deleted = 0";
	
	public static final String UPDATE_TIME_QUOTA_BY_CUSTOMER_ID = "UPDATE tblcustquotadtls SET timequotaused = timequotaused + ? WHERE custid = ? AND planid = ? AND is_deleted = 0";
	
	public static final String UPDATE_SMS_QUOTA_BY_CUSTOMER_ID = "UPDATE tblcustsmsdtls SET usedsms = usedsms + ? WHERE custid = ? AND planid = ? AND custpackageid = ?";
	
	public static final String UPDATE_VOICE_QUOTA_BY_CUSTOMER_ID = "UPDATE tblcustvoicedtls SET usedVoice = usedVoice + ? WHERE custid = ? AND planid = ? AND custpackageid = ?";
	
	public static final String UPDATE_FUP_STATUS_BY_QUOTA_ID = "UPDATE tblcustquotadtls SET isfupapllied = 1 , fupapplieddate=NOW() WHERE quotadtlsid = ? AND isfupapllied = 0";
	
	public static final String CHECK_POLICY_EXISTS = "SELECT 1 FROM tbl_qos_policy WHERE name = ?";

	public static final String INSERT_POLICY = "INSERT INTO tbl_qos_policy (name, description, thpolicyname, baseparam1, baseparam2, baseparam3, thparam1, thparam2, thparam3, is_deleted, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, basepolicyname, createbyname, updatebyname, MVNOID, type, qosspeed, upstreamprofileuid, downstreamprofileuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public static final String GET_ALL_POLICIES = "SELECT * FROM tbl_qos_policy";
	
	public static final String GET_POLICY_BY_ID = "SELECT * FROM tbl_qos_policy WHERE id = ?";
	
	public static final String GET_POLICY_BY_NAME = "SELECT * FROM tbl_qos_policy WHERE name = ?";
	
	public static final String INSERT_PEER_CONFIGURATION = "INSERT INTO tblpeerconfiguration (\r\n"
			+ "	            node_name, realm, fqdn, sctp_listen_port, tcp_listen_port,\r\n"
			+ "	            dtls_sctp_listen_port, tls_tcp_listen_port, radius_udp_server_ports,\r\n"
			+ "	            enable_radius_udp_client_ports, radius_client_udp_port_range_start,\r\n"
			+ "	            radius_client_udp_port_range_end, verification_mode, certificate_type,\r\n"
			+ "	            certificate_name, ip_addresses, remote_ip_address, remote_port, status, watchdog_interval\r\n"
			+ "	        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    public static final String SELECT_ALL_PEER_CONFIGS = "SELECT * FROM tblpeerconfiguration";

	    public static final String SELECT_PEER_CONFIG_BY_ID = "SELECT * FROM tblpeerconfiguration WHERE id = ?";

	    public static final String UPDATE_PEER_CONFIGURATION = "UPDATE tblpeerconfiguration SET\r\n"
	    		+ "	            node_name = ?, realm = ?, fqdn = ?, sctp_listen_port = ?, tcp_listen_port = ?,\r\n"
	    		+ "	            dtls_sctp_listen_port = ?, tls_tcp_listen_port = ?, radius_udp_server_ports = ?,\r\n"
	    		+ "	            enable_radius_udp_client_ports = ?, radius_client_udp_port_range_start = ?,\r\n"
	    		+ "	            radius_client_udp_port_range_end = ?, verification_mode = ?, certificate_type = ?,\r\n"
	    		+ "	            certificate_name = ?, ip_addresses = ?, remote_ip_address = ?, remote_port = ?, status = ?, watchdog_interval = ?\r\n"
	    		+ "	        WHERE id = ?";

	    public static final String DELETE_PEER_CONFIGURATION = "DELETE FROM tblpeerconfiguration WHERE id = ?";

	    public static final String CHECK_PEER_CONFIG_EXISTS = "SELECT COUNT(*) FROM tblpeerconfiguration WHERE id = ?";
	    
		public static final String SELECT_PEER_CONFIG_BY_NAME = "SELECT * FROM tblpeerconfiguration WHERE node_name = ?";

		public static final String CHECK_PEER_EXISTS_BY_NAME = "SELECT COUNT(*) FROM tblpeerconfiguration WHERE node_name = ?";

		public static final String CHECK_PEER_EXISTS_BY_NAME_EXCEPT_ID = "SELECT COUNT(*) FROM tblpeerconfiguration WHERE node_name = ? AND id != ?";
		
		public static final String SELECT_PEER_CONFIG_BY_STATUS = "SELECT * FROM tblpeerconfiguration WHERE status = ?";

		public static final String SELECT_PEER_CONFIG_BY_REMOTE_IP = "SELECT * FROM tblpeerconfiguration WHERE remote_ip_address = ?";

		public static final String SELECT_PEER_CONFIG_BY_REALM = "SELECT * FROM tblpeerconfiguration WHERE realm = ?";

	    public static final String INSERT_ATTRIBUTE = "INSERT INTO tblm_attribute (id, attribute_id, name, mandatory, protected, encryption, type, status, dictionary_type, minimum, maximum, attribute_vendor_id, parent_attribute_id, vendor_id, created_date, created_by, modified_date, modified_by, regex) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		public static final String INSERT_ATTRIBUTE_MAPPING = "INSERT INTO tblm_attribute_mapping (id, attribute_id, vendor_id) VALUES (?, ?, ?)";

		public static final String SELECT_ALL_ATTRIBUTES = "SELECT * FROM tblm_attribute";

		public static final String SELECT_ATTRIBUTE_BY_ID = "SELECT * FROM tblm_attribute WHERE id = ?";

		public static final String SELECT_ATTRIBUTE_BY_NAME = "SELECT * FROM tblm_attribute WHERE name = ?";

		public static final String SELECT_ATTRIBUTE_BY_ATTRIBUTE_ID = "SELECT * FROM tblm_attribute WHERE attribute_id = ?";

		public static final String DELETE_ATTRIBUTE_MAPPINGS = "DELETE FROM tblm_attribute_mapping WHERE attribute_id = ?";

		public static final String DELETE_ATTRIBUTE = "DELETE FROM tblm_attribute WHERE id = ?";

		public static final String UPDATE_ATTRIBUTE = "UPDATE tblm_attribute SET \r\n"
				+ "		            attribute_id = ?, name = ?, mandatory = ?, protected = ?, encryption = ?,\r\n"
				+ "		            type = ?, status = ?, dictionary_type = ?, minimum = ?, maximum = ?,\r\n"
				+ "		            attribute_vendor_id = ?, parent_attribute_id = ?, vendor_id = ?,\r\n"
				+ "		            modified_date = ?, modified_by = ?, regex = ?\r\n"
				+ "		        WHERE id = ?";

		public static final String CHECK_IF_ATTRIBUTE_MAPPING_EXISTS = "SELECT COUNT(*) FROM tblm_attribute_mapping WHERE attribute_id = ? AND vendor_id = ?";

		public static final String SELECT_VALID_VENDOR_IDS = "SELECT id FROM tblm_vendor WHERE id IN (%s)";

		public static final String SELECT_ALL_ACTIVE_ATTRIBUTES = "SELECT a.id, a.attribute_id, a.name, a.mandatory, a.protected, a.encryption, a.type, a.status, a.dictionary_type,"
				+ " a.minimum, a.maximum, a.attribute_vendor_id, a.parent_attribute_id,a.created_date, a.created_by, a.modified_date, a.modified_by, a.regex, v.id, v.name, v.status, v.id FROM tblm_attribute a, tblm_vendor v WHERE a.vendor_id = v.id and a.status = ?";
		
		public static final String SELECT_ALL_ACTIVE_VENDORS_WITH_ATTRIBUTES = "SELECT v.id AS id, v.vendor_id AS vendor_id, v.name AS vendor_name, v.status AS vendor_status, v.description AS vendor_description, a.attribute_id AS attribute_id, a.name,"
				+ "a.dictionary_type, a.minimum, a.maximum, a.attribute_vendor_id, a.parent_attribute_id, a.vendor_id AS attribute_vendor_id_fk, a.created_date, "
				+ "a.created_by, a.modified_date, a.modified_by, a.regex, a.status AS attribute_status, a.encryption, a.mandatory, a.protected, a.type "
				+ "FROM tblm_vendor v JOIN tblm_attribute a ON v.id = a.vendor_id "
				+ "WHERE v.status = 'ACTIVE' AND a.status = 'ACTIVE' ORDER BY v.id, a.id";

		public static final String INSERT_CUSTOMER_PACKAGE_REL =
			    "INSERT INTO tblcustpackagerel (custpackageid, custid, planid, startdate, enddate, " +
			    "expirydate, status, createdate) " +
			    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		public static final String GET_PLAN_DETAILS = "select * from tblmpostpaidplan where POSTPAIDPLANID = ?";


		/* =========================
		 * VENDOR QUERIES
		 * ========================= */

		public static final String INSERT_VENDOR =
				"INSERT INTO tblm_vendor (id, vendor_id, name, description, status) " +
						"VALUES (?, ?, ?, ?, ?)";

		public static final String UPDATE_VENDOR =
				"UPDATE tblm_vendor SET vendor_id = ?, name = ?, description = ?, status = ? " +
						"WHERE id = ?";

		public static final String DELETE_VENDOR =
				"DELETE FROM tblm_vendor WHERE id = ?";

		public static final String GET_VENDOR_BY_ID =
				"SELECT id, vendor_id, name, description, status " +
						"FROM tblm_vendor WHERE id = ?";

		public static final String GET_VENDOR_BY_VENDOR_ID =
				"SELECT id, vendor_id, name, description, status " +
						"FROM tblm_vendor WHERE vendor_id = ?";

		public static final String GET_VENDOR_BY_NAME =
				"SELECT id, vendor_id, name, description, status " +
						"FROM tblm_vendor WHERE name = ?";

		public static final String GET_ALL_VENDORS =
				"SELECT id, vendor_id, name, description, status FROM tblm_vendor";

		public static final String GET_ALL_VENDORS_BY_STATUS =
				"SELECT id, vendor_id, name, description, status " +
						"FROM tblm_vendor WHERE status = ?";

		public static final String CHECK_VENDOR_ID_EXISTS =
				"SELECT COUNT(*) FROM tblm_vendor WHERE vendor_id = ?";

		public static final String CHECK_VENDOR_NAME_EXISTS =
				"SELECT COUNT(*) FROM tblm_vendor WHERE name = ?";

		public static final String CHECK_VENDOR_ID_EXISTS_EXCEPT_SELF =
				"SELECT COUNT(*) FROM tblm_vendor WHERE vendor_id = ? AND id <> ?";

		public static final String GET_PLAN_BY_ID =
				"SELECT * FROM tblmpostpaidplan WHERE POSTPAIDPLANID = ? AND is_delete = 0";

		public static final String GET_PLAN_BY_NAME =
				"SELECT  * FROM tblmpostpaidplan WHERE NAME = ? AND is_delete = 0";

		public static final String GET_ALL_PLANS =
				"SELECT * FROM tblmpostpaidplan WHERE is_delete = 0";

		public static final String GET_GATEWAY_MAPPING_BY_QOS_POLICY_ID =
				"SELECT id, name, download_speed, upload_speed, " +
						"base_download_speed, base_upload_speed, " +
						"throttle_download_speed, throttle_upload_speed, qos_policy_id " +
						"FROM tbltqospolicy_gateway_mapping " +
						"WHERE qos_policy_id = ?";

	public static final String CREATE_PLAN =
			"INSERT INTO tblmpostpaidplan (" +
					"POSTPAIDPLANID, NAME, DISPLAYNAME, PLANCODE, DESCRIPTION, PLANCATEGORY, " +
					"MAXALLOWEDCHILD, STARTDATE, ENDDATE, QUOTA, QUOTAUNIT, UPLOADQOS, DOWNLOADQOS, " +
					"STATUS, PLANSTATUS, CHILDQUOTA, CHILDQUOTAUNIT, SLICE, SLICEUNIT, PARAM1, PARAM2, PARAM3, " +
					"ATTACHEDTOALLHOTSPOT, CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE, " +
					"MVNOID, TAXID, serviceid, plantype, dbr, plangroup, validity, UPLOADTS, DOWNLOADTS, " +
					"allowoverusage, saccode, quotatype, quotaunittime, quotatime, maxconcurrentsession, " +
					"qospolicy_id, radiusprofile_id, offerprice, is_delete, " +
					"quotadid, quotaintercom, quotaunitdid, quotaunitintercom, createbyname, updatebyname, " +
					"taxamount, datacategory, quotarestinterval, unitsofvalidity, timebasepolicyid, chunk, " +
					"use_quota, usage_quota_type, addon_to_base, SMSLIMIT, VOICELIMIT, smstype, voicetype, " +
					"SMSRESETINTERVAL, VOICERESETINTERVAL, pulse) " +
					"VALUES (" +
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
					")";

		public static final String UPDATE_PLAN =
				"UPDATE tblmpostpaidplan SET " +
						"NAME=?, DISPLAYNAME=?, PLANCODE=?, DESCRIPTION=?, PLANCATEGORY=?, " +
						"MAXALLOWEDCHILD=?, STARTDATE=?, ENDDATE=?, QUOTA=?, QUOTAUNIT=?, " +
						"UPLOADQOS=?, DOWNLOADQOS=?, STATUS=?, PLANSTATUS=?, CHILDQUOTA=?, CHILDQUOTAUNIT=?, " +
						"SLICE=?, SLICEUNIT=?, PARAM1=?, PARAM2=?, PARAM3=?, ATTACHEDTOALLHOTSPOT=?, " +
						"LASTMODIFIEDBYSTAFFID=?, LASTMODIFIEDDATE=?, MVNOID=?, TAXID=?, serviceid=?, plantype=?, dbr=?, " +
						"plangroup=?, validity=?, UPLOADTS=?, DOWNLOADTS=?, allowoverusage=?, saccode=?, quotatype=?, " +
						"quotaunittime=?, quotatime=?, maxconcurrentsession=?, qospolicy_id=?, radiusprofile_id=?, " +
						"offerprice=?, is_delete=?, quotadid=?, quotaintercom=?, quotaunitdid=?, " +
						"quotaunitintercom=?, createbyname=?, updatebyname=?, taxamount=?, datacategory=?, " +
						"quotarestinterval=?, unitsofvalidity=?, timebasepolicyid=?, chunk=?, use_quota=?, " +
						"usage_quota_type=?, addon_to_base=?, SMSLIMIT=?, VOICELIMIT=?, smstype=?, voicetype=?, " +
						"SMSRESETINTERVAL=?, VOICERESETINTERVAL=?, pulse=? " +
						"WHERE POSTPAIDPLANID=?";

		public static final String SOFT_DELETE_PLAN =
				"UPDATE tblmpostpaidplan SET is_delete=? WHERE POSTPAIDPLANID=?";

		public static final String GET_NEXT_PLAN_ID =
				"SELECT COALESCE(MAX(POSTPAIDPLANID),0) + 1 FROM tblmpostpaidplan";

	public static final String UPDATE_POLICY =
			"UPDATE tbl_qos_policy SET " +
					"name = ?, " +
					"description = ?, " +
					"thpolicyname = ?, " +
					"baseparam1 = ?, " +
					"baseparam2 = ?, " +
					"baseparam3 = ?, " +
					"thparam1 = ?, " +
					"thparam2 = ?, " +
					"thparam3 = ?, " +
					"is_deleted = ?, " +
					"lastmodifiedbystaffid = ?, " +
					"lastmodifieddate = ?, " +
					"basepolicyname = ?, " +
					"updatebyname = ?, " +
					"MVNOID = ?, " +
					"type = ?, " +
					"qosspeed = ?, " +
					"upstreamprofileuid = ?, " +
					"downstreamprofileuid = ? " +
					"WHERE id = ?";

	public static final String DELETE_GATEWAY_MAPPING_BY_POLICY_ID =
				"DELETE FROM tbltqospolicy_gateway_mapping WHERE qos_policy_id = ?";

	public static final String INSERT_GATEWAY_MAPPING =
			"INSERT INTO tbltqospolicy_gateway_mapping " +
					"(id, name, download_speed, upload_speed, " +
					" base_download_speed, base_upload_speed, " +
					" throttle_download_speed, throttle_upload_speed, " +
					" qos_policy_id) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

   // public static final String GET_CUSTOMER_BY_USERNAME = "SELECT * FROM tblcustomers WHERE username = ? AND is_deleted = 0";

	public static final String CREATE_AUDIT =
			"INSERT INTO tbl_diameter_audit(" +
					"TRANSACTION_ID," +
					"SESSION_ID," +
					"PROTOCOL," +
					"COMMAND_CODE," +
					"APPLICATION_ID," +
					"REQUEST_TYPE," +
					"SERVICE_TYPE," +
					"MSISDN," +
					"IMSI," +
					"IMEI," +
					"APN," +
					"ORIGIN_HOST," +
					"ORIGIN_REALM," +
					"DESTINATION_HOST," +
					"DESTINATION_REALM," +
					"RESULT_CODE," +
					"RESULT_DESCRIPTION," +
					"STATUS," +
					"ERROR_MESSAGE," +
					"PROCESSING_TIME_MS," +
					"REQUEST_PAYLOAD," +
					"RESPONSE_PAYLOAD," +
					"PEER_NAME," +
					"POD_NAME," +
					"CC_REQUEST_NUMBER," +
					"SUBSCRIPTION_ID," +
					"FRAMED_IP_ADDRESS," +
					"FRAMED_IPV6_PREFIX," +
					"CALLED_STATION_ID," +
					"THREE_GPP_RAT_TYPE," +
					"QOS_INFORMATION," +
					"BEARER_IDENTIFIER," +
					"IP_CAN_TYPE," +
					"AN_GW_ADDRESS," +
					"THREE_GPP_SGSN_ADDRESS," +
					"USER_NAME," +
					"ORIGIN_STATE_ID," +
					"USER_EQUIPMENT_INFO," +
					"CC_SUB_SESSION_ID," +
					"TFT_PACKET_FILTER_INFORMATION," +
					"CHARGING_RULE_INSTALL," +
					"CHARGING_RULE_REMOVE," +
					"DEFAULT_EPS_BEARER_QOS," +
					"SUPPORTED_FEATURES," +
					"EVENT_TRIGGER," +
					"USAGE_MONITORING_INFORMATION," +
					"CHARGING_RULE_REPORT," +
					"THREE_GPP_USER_LOCATION_INFO," +
					"TERMINATION_CAUSE," +
					"CREATED_AT" +
					") VALUES(" +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
					"NOW()" +
					")";

	public static final String GET_AUDIT_BY_ID =
			"SELECT * FROM tbl_diameter_audit " +
					"WHERE ID=?";



	public static final String GET_AUDIT_BY_TXN =
			"SELECT * FROM tbl_diameter_audit " +
					"WHERE TRANSACTION_ID=?";


	public static final String GET_ALL_AUDIT =
			"SELECT * FROM tbl_diameter_audit";

	public static final String UPDATE_AUDIT =
			"UPDATE tbl_diameter_audit SET " +
					"TRANSACTION_ID=?," +
					"SESSION_ID=?," +
					"PROTOCOL=?," +
					"COMMAND_CODE=?," +
					"APPLICATION_ID=?," +
					"REQUEST_TYPE=?," +
					"SERVICE_TYPE=?," +
					"MSISDN=?," +
					"IMSI=?," +
					"IMEI=?," +
					"APN=?," +
					"ORIGIN_HOST=?," +
					"ORIGIN_REALM=?," +
					"DESTINATION_HOST=?," +
					"DESTINATION_REALM=?," +
					"RESULT_CODE=?," +
					"RESULT_DESCRIPTION=?," +
					"STATUS=?," +
					"ERROR_MESSAGE=?," +
					"PROCESSING_TIME_MS=?," +
					"REQUEST_PAYLOAD=?," +
					"RESPONSE_PAYLOAD=?," +
					"PEER_NAME=?," +
					"POD_NAME=?," +
					"CC_REQUEST_NUMBER=?," +
					"SUBSCRIPTION_ID=?," +
					"FRAMED_IP_ADDRESS=?," +
					"FRAMED_IPV6_PREFIX=?," +
					"CALLED_STATION_ID=?," +
					"THREE_GPP_RAT_TYPE=?," +
					"QOS_INFORMATION=?," +
					"BEARER_IDENTIFIER=?," +
					"IP_CAN_TYPE=?," +
					"AN_GW_ADDRESS=?," +
					"THREE_GPP_SGSN_ADDRESS=?," +
					"USER_NAME=?," +
					"ORIGIN_STATE_ID=?," +
					"USER_EQUIPMENT_INFO=?," +
					"CC_SUB_SESSION_ID=?," +
					"TFT_PACKET_FILTER_INFORMATION=?," +
					"CHARGING_RULE_INSTALL=?," +
					"CHARGING_RULE_REMOVE=?," +
					"DEFAULT_EPS_BEARER_QOS=?," +
					"SUPPORTED_FEATURES=?," +
					"EVENT_TRIGGER=?," +
					"USAGE_MONITORING_INFORMATION=?," +
					"CHARGING_RULE_REPORT=?," +
					"THREE_GPP_USER_LOCATION_INFO=?," +
					"TERMINATION_CAUSE=? " +
					"WHERE ID=?";

	public static final String DELETE_AUDIT =
			"DELETE FROM tbl_diameter_audit " +
					"WHERE ID=?";

	public static final String GET_AUDIT_BY_SESSION_ID =
			"SELECT * FROM tbl_diameter_audit " +
					"WHERE SESSION_ID=?";

	public static final String GET_AUDIT_BY_MSISDN =
			"SELECT * FROM tbl_diameter_audit " +
					"WHERE MSISDN=?";

	public static final String GET_AUDIT_BY_IMSI =
			"SELECT * FROM tbl_diameter_audit " +
					"WHERE IMSI=?";

	public static final String CREATE_LIVE_SESSION =
			"INSERT INTO tbl_diameter_live_session(" +
					"SESSION_ID," +
					"TRANSACTION_ID," +
					"CC_REQUEST_TYPE," +
					"CC_REQUEST_NUMBER," +
					"DIAMETER_INTERFACE," +
					"SERVICE_TYPE," +
					"SERVICE_CONTEXT_ID," +
					"RATING_GROUP," +
					"SERVICE_IDENTIFIER," +
					"MSISDN," +
					"IMSI," +
					"IMEI," +
					"CALLING_PARTY," +
					"CALLED_PARTY," +
					"IP_ADDRESS," +
					"APN," +
					"ORIGIN_HOST," +
					"ORIGIN_REALM," +
					"DESTINATION_HOST," +
					"DESTINATION_REALM," +
					"MEDIA_TYPE," +
					"SIP_METHOD," +
					"AF_CHARGING_IDENTIFIER," +
					"FLOW_STATUS," +
					"CODEC," +
					"POLICY_NAME," +
					"CHARGING_RULE_BASE_NAME," +
					"QOS_PROFILE," +
					"QCI," +
					"UPLINK_BYTES," +
					"DOWNLINK_BYTES," +
					"TOTAL_BYTES," +
					"VOICE_SECONDS," +
					"SMS_COUNT," +
					"USED_UNITS," +
					"GRANTED_UNITS," +
					"SESSION_STATUS," +
					"ACTIVE_FLAG," +
					"RESULT_CODE," +
					"STATUS," +
					"NODE_NAME," +
					"POD_NAME," +

					"NAS_IP_ADDRESS," +
					"IPV4_ADDRESS," +
					"IPV6_ADDRESS," +
					"CURRENT_PLAN," +
					"PLAN_START_DATE," +
					"PLAN_EXPIRY_DATE," +

					"START_TIME," +
					"LAST_UPDATE_TIME," +
					"GEO_TYPE, " +
	                "MCC, " +
	                "MNC, " +
	                "TAC, " +
	                "ECI, " +
	                "ENODEB_ID, " +
	                "CELL_ID" +
					") VALUES(" +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					",NOW(),NOW(),?,?,?,?,?,?,?" +
					")";

	public static final String UPDATE_LIVE_SESSION =
			"UPDATE tbl_diameter_live_session SET " +
					"TRANSACTION_ID=?," +
					"CC_REQUEST_TYPE=?," +
					"CC_REQUEST_NUMBER=?," +
					"DIAMETER_INTERFACE=?," +
					"SERVICE_TYPE=?," +
					"SERVICE_CONTEXT_ID=?," +
					"RATING_GROUP=?," +
					"SERVICE_IDENTIFIER=?," +
					"MSISDN=?," +
					"IMSI=?," +
					"IMEI=?," +
					"CALLING_PARTY=?," +
					"CALLED_PARTY=?," +
					"IP_ADDRESS=?," +
					"APN=?," +
					"ORIGIN_HOST=?," +
					"ORIGIN_REALM=?," +
					"DESTINATION_HOST=?," +
					"DESTINATION_REALM=?," +
					"MEDIA_TYPE=?," +
					"SIP_METHOD=?," +
					"AF_CHARGING_IDENTIFIER=?," +
					"FLOW_STATUS=?," +
					"CODEC=?," +
					"POLICY_NAME=?," +
					"CHARGING_RULE_BASE_NAME=?," +
					"QOS_PROFILE=?," +
					"QCI=?," +
					"UPLINK_BYTES=?," +
					"DOWNLINK_BYTES=?," +
					"TOTAL_BYTES=?," +
					"VOICE_SECONDS=?," +
					"SMS_COUNT=?," +
					"USED_UNITS=?," +
					"GRANTED_UNITS=?," +
					"SESSION_STATUS=?," +
					"ACTIVE_FLAG=?," +
					"RESULT_CODE=?," +
					"STATUS=?," +
					"NODE_NAME=?," +
					"POD_NAME=?," +

					"NAS_IP_ADDRESS=?," +
					"IPV4_ADDRESS=?," +
					"IPV6_ADDRESS=?," +
					"CURRENT_PLAN=?," +
					"PLAN_START_DATE=?," +
					"PLAN_EXPIRY_DATE=?," +

					"LAST_UPDATE_TIME=NOW()," +
					"GEO_TYPE=?, " +
	                "MCC=?, " +
	                "MNC=?, " +
	                "TAC=?, " +
	                "ECI=?, " +
	                "ENODEB_ID=?, " +
	                "CELL_ID=?" +
					" WHERE SESSION_ID=?";

	public static final String GET_ALL_LIVE_SESSION =
			"SELECT * FROM tbl_diameter_live_session";


	public static final String GET_BY_SESSION_ID =
			"SELECT * FROM tbl_diameter_live_session WHERE SESSION_ID=?";


	public static final String GET_BY_MSISDN =
			"SELECT * FROM tbl_diameter_live_session WHERE MSISDN=?";


	public static final String GET_BY_IMSI =
			"SELECT * FROM tbl_diameter_live_session WHERE IMSI=?";


	public static final String GET_BY_TRANSACTION_ID =
			"SELECT * FROM tbl_diameter_live_session WHERE TRANSACTION_ID=?";


	public static final String DELETE_LIVE_SESSION =

			"DELETE FROM tbl_diameter_live_session " +

					"WHERE SESSION_ID=?";

	public static final String CREATE_CDR =
			"INSERT INTO tbl_diameter_session_cdr(" +
					"SESSION_ID," +
					"TRANSACTION_ID," +
					"DIAMETER_INTERFACE," +
					"CC_REQUEST_NUMBER," +
					"SERVICE_TYPE," +
					"SERVICE_CONTEXT_ID," +
					"RATING_GROUP," +
					"SERVICE_IDENTIFIER," +
					"MSISDN," +
					"IMSI," +
					"IMEI," +
					"CALLING_PARTY," +
					"CALLED_PARTY," +
					"IP_ADDRESS," +
					"APN," +
					"ORIGIN_HOST," +
					"ORIGIN_REALM," +
					"DESTINATION_HOST," +
					"DESTINATION_REALM," +
					"MEDIA_TYPE," +
					"SIP_METHOD," +
					"AF_CHARGING_IDENTIFIER," +
					"FLOW_STATUS," +
					"CODEC," +
					"POLICY_NAME," +
					"CHARGING_RULE_BASE_NAME," +
					"QOS_PROFILE," +
					"QCI," +
					"UPLINK_BYTES," +
					"DOWNLINK_BYTES," +
					"TOTAL_BYTES," +
					"VOICE_SECONDS," +
					"SMS_COUNT," +
					"USED_UNITS," +
					"GRANTED_UNITS," +
					"START_TIME," +
					"LAST_UPDATE_TIME," +
					"END_TIME," +
					"SESSION_DURATION," +
					"TERMINATION_CAUSE," +
					"TERMINATION_REASON," +
					"DISCONNECT_SOURCE," +
					"RESULT_CODE," +
					"RESULT_DESCRIPTION," +
					"STATUS," +
					"ERROR_MESSAGE," +
					"REQUEST_PAYLOAD," +
					"RESPONSE_PAYLOAD," +
					"NODE_NAME," +
					"POD_NAME," +
					"GEO_TYPE, " +
	                "MCC, " +
	                "MNC, " +
	                "TAC, " +
	                "ECI, " +
	                "ENODEB_ID, " +
	                "CELL_ID" +
					") VALUES(" +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					")";


	public static final String GET_CDR_BY_ID =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE ID=?";


	public static final String GET_ALL_CDR =
			"SELECT * FROM tbl_diameter_session_cdr";


	public static final String GET_CDR_BY_SESSION_ID =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE SESSION_ID=?";


	public static final String GET_CDR_BY_MSISDN =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE MSISDN=?";


	public static final String GET_CDR_BY_IMSI =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE IMSI=?";

	public static final String GET_CDR_BY_SERVICE_TYPE =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE SERVICE_TYPE = ?";

	public static final String UPDATE_CDR =
			"UPDATE tbl_diameter_session_cdr SET " +
					"SESSION_ID=?," +
					"TRANSACTION_ID=?," +
					"DIAMETER_INTERFACE=?," +
					"CC_REQUEST_NUMBER=?," +
					"SERVICE_TYPE=?," +
					"SERVICE_CONTEXT_ID=?," +
					"RATING_GROUP=?," +
					"SERVICE_IDENTIFIER=?," +
					"MSISDN=?," +
					"IMSI=?," +
					"IMEI=?," +
					"CALLING_PARTY=?," +
					"CALLED_PARTY=?," +
					"IP_ADDRESS=?," +
					"APN=?," +
					"ORIGIN_HOST=?," +
					"ORIGIN_REALM=?," +
					"DESTINATION_HOST=?," +
					"DESTINATION_REALM=?," +
					"MEDIA_TYPE=?," +
					"SIP_METHOD=?," +
					"AF_CHARGING_IDENTIFIER=?," +
					"FLOW_STATUS=?," +
					"CODEC=?," +
					"POLICY_NAME=?," +
					"CHARGING_RULE_BASE_NAME=?," +
					"QOS_PROFILE=?," +
					"QCI=?," +
					"UPLINK_BYTES=?," +
					"DOWNLINK_BYTES=?," +
					"TOTAL_BYTES=?," +
					"VOICE_SECONDS=?," +
					"SMS_COUNT=?," +
					"USED_UNITS=?," +
					"GRANTED_UNITS=?," +
					"START_TIME=?," +
					"LAST_UPDATE_TIME=?," +
					"END_TIME=?," +
					"SESSION_DURATION=?," +
					"TERMINATION_CAUSE=?," +
					"TERMINATION_REASON=?," +
					"DISCONNECT_SOURCE=?," +
					"RESULT_CODE=?," +
					"RESULT_DESCRIPTION=?," +
					"STATUS=?," +
					"ERROR_MESSAGE=?," +
					"REQUEST_PAYLOAD=?," +
					"RESPONSE_PAYLOAD=?," +
					"NODE_NAME=?," +
					"POD_NAME=? " +
					"WHERE ID=?";

	public static final String DELETE_CDR =
			"DELETE FROM tbl_diameter_session_cdr " +
					"WHERE ID=?";

	public static final String GET_CDR_BY_TRANSACTION_ID =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE TRANSACTION_ID=?";


	public static final String GET_CDR_BY_STATUS =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE STATUS=?";


	public static final String GET_CDR_BY_DATE =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE DATE(CREATED_DATE)=?";


	public static final String GET_CDR_BETWEEN_DATES =
			"SELECT * FROM tbl_diameter_session_cdr " +
					"WHERE DATE(CREATED_DATE) " +
					"BETWEEN ? AND ?";

	public static final String CREATE_AUDIT_DETAIL_INFORMATION =
	        "INSERT INTO tbl_diameter_audit_detail_information (" +
	                "AUDIT_ID, " +
	                "PDP_TYPE, " +
	                "IMSI_UNAUTHENTICATED_FLAG, " +
	                "PDP_CONTEXT_TYPE, " +
	                "SERVING_NODE_TYPE, " +
	                "CHARGING_ID, " +
	                "PDP_ADDRESS, " +
	                "GGSN_ADDRESS, " +
	                "DYNAMIC_ADDRESS_FLAG, " +
	                "IMSI_MCC_MNC, " +
	                "NSAPI, " +
	                "CHARGING_CHARACTERISTICS, " +
	                "SGSN_MCC_MNC, " +
	                "MS_TIME_ZONE, " +
	                "USER_LOCATION_INFO_TIME, " +
	                "GEO_TYPE, " +
	                "MCC, " +
	                "MNC, " +
	                "TAC, " +
	                "ECI, " +
	                "ENODEB_ID, " +
	                "CELL_ID" +
	        ") VALUES (" +
	                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
	        ")";

	// ---- Quota Reset Scheduler ----
	public static final String RESET_DAILY_QUOTAS =
			"UPDATE tblcustquotadtls q " +
			"INNER JOIN tblcustpackagerel cpr ON cpr.custpackageid = q.custpackageid AND cpr.custid = q.custid " +
			"INNER JOIN tbltcustomerservicemapping csm ON csm.custid = q.custid " +
			"INNER JOIN tblmpostpaidplan pp ON pp.POSTPAIDPLANID = q.planid " +
			"SET q.usedquota = 0, " +
			"    q.usedquotakb = 0, " +
			"    q.timequotaused = 0, " +
			"    q.timeusedquotasec = 0, " +
			"    q.speeddowngradeflag = 0, " +
			"    q.isfupapllied = 0, " +
			"    q.fupapplieddate = NULL, " +
			"    q.last_quota_reset = NOW(), " +
			"    q.lastmodifieddate = NOW() " +
			"WHERE cpr.is_delete = 0 " +
			"  AND cpr.cust_plan_status = 'Active' " +
			"  AND cpr.startdate <= NOW() " +
			"  AND cpr.enddate >= NOW() " +
			"  AND csm.is_deleted = 0 " +
			"  AND csm.status = 'Active' " +
			"  AND pp.quotarestinterval = 'Daily' " +
			"  AND pp.is_delete = 0 " +
			"  AND q.is_deleted = 0";
}
