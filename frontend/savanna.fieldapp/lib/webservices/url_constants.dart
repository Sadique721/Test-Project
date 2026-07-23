class UrlConstants {
  ///*__________________ Play Store Live Url __________________________*/

  /// Old Url in Play Store
  // static const MAIN_URL = "http://134.209.145.227:4200";
  // static const PAYMENT_RECEIPT_URL = "http://134.209.145.227:4200/";
  /// New Url in Play Store
  // static const MAIN_URL = "http://165.232.182.10:30080";
  // static const PAYMENT_RECEIPT_URL = "http://165.232.182.10:30080";

  ///*_______________________ Live Url ________________________*/

  // static const MAIN_URL = "https://wifiSavbillnet.in:30080";
  // static const PAYMENT_RECEIPT_URL = "https://wifiSavbillnet.in:30080";

  ///*______________________ Development Url __________________*/

  // static const MAIN_URL = "http://192.168.24.31:30080";
  // static const PAYMENT_RECEIPT_URL = "http://192.168.24.31:30080";
  // static const MAIN_URL = "https://wifiSavbillnet.in:30080";

  // static const MAIN_URL = "http://192.168.24.9:30085";
  // static const PAYMENT_RECEIPT_URL = "http://192.168.24.9:30085";

  // static const MAIN_URL = "http://165.232.182.10:30080";
  // static const PAYMENT_RECEIPT_URL = "http://165.232.182.10:30080";

  // static const MAIN_URL = "http://192.168.24.26:30085";
  // static const PAYMENT_RECEIPT_URL = "http://192.168.24.26:30085";

  // static const MAIN_URL = "http://192.168.24.6:30080";
  // static const PAYMENT_RECEIPT_URL = "http://192.168.24.6:4201";
  /// Live Tanzania
  // static const MAIN_URL = "https://crmhub.savannafibre.co.tz:30080";
  // static const PAYMENT_RECEIPT_URL = "https://crmhub.savannafibre.co.tz:4201";

  /// PrePod Server

  // static const PAYMENT_RECEIPT_URL = "http://savbilluat.savannafibre.com:4200";
  // static const MAIN_URL = "https://savbilluat.savannafibre.com:38124";
  static const MAIN_URL = "http://102.209.108.106:38124";

  // static const MAIN_URL = "http://197.211.6.60:38124";
  static const PAYMENT_RECEIPT_URL = "http://102.209.108.106:4200";
  // static const PAYMENT_RECEIPT_URL = "https://savbilluat.savannafibre.com:4200";
  //  static const MAIN_URL = "http://216.48.180.92:38124";

  //static const MAIN_URL = "http://billing.savannafibre.co.ug:38124";

  // static const MAIN_URL = "http://197.211.6.60:38124";

  // static const MAIN_URL = "https://common.savannafibre.co.ke:30080";
  // static const MAIN_URL = "http://197.211.6.60:38124";

  // static const PAYMENT_RECEIPT_URL = "http://216.48.180.92:4200";
 // static const PAYMENT_RECEIPT_URL = "http://105.28.32.202:4201";


  static const webURL = "${MAIN_URL}/api";
  static const baseUrl = "$webURL/portal/v1/";
  static const get_customer_detail = "${baseUrl}subscriber/getBasicCustDetails";

  // ticket management
  static const get_user_tickets = "${webURL}/v1/TicketManagement/case";
  static const get_tickets_detail = "${webURL}/v1/TicketManagement/case";
  static const get_tickets_followup_detail =
      "${webURL}/v1/TicketManagement/ticketFollowupDetails/getAllByCaseId";
  static const generateNameOfTheTicketFollowUp =
      "${webURL}/v1/TicketManagement/ticketFollowUp/generateNameOfTheTicketFollowUp";
  static const get_case_reason = "${webURL}/v1/caseReason/all";
  static const getAllStaffUserByServiceArea =
      "${webURL}/v1/TicketManagement/case/getAllStaffUserByServiceArea";
  static const add_case_ratting = "${webURL}/v1/case/rating";
  static const add_case_followup = "${webURL}/v1/ticketFollowupDetails/save";
  static const case_assign = "${webURL}/v1/TicketManagement/case/updateDetails";
  static const ticketFollowUpSave =
      "${webURL}/v1/TicketManagement/ticketFollowUp/save";
  static const getTicketFollowUpRemark =
      "${webURL}/v1/TicketManagement/ticketFollowUp/findAll/ticketFollowUpRemark";
  static const addTicketFollowUpRemark =
      "${webURL}/v1/TicketManagement/ticketFollowUp/ticketFollowUp/remark";
  static const ticketCloseFollowUp =
      "${webURL}/v1/TicketManagement/ticketFollowUp/closefollowup";
  static const getAllTeamNameByStaffId =
      "${webURL}/v1/TicketManagement/ticketFollowupDetails/getAllTeamNameByStaffId";

  static const getAllActiveStaffUser =
      "${webURL}/v1/SavbillCommonGateway/staffuser/allActive";
  static const getOnCallDisconnected =
      "${webURL}/v1/TicketManagement/case/findAll/ContactFailed";
  static const customer_online_offline =
      "${MAIN_URL}/SavbillRadius/liveUser/isCustomersOnlineOrOffline";

  // payments module
  static const get_payment_list =
      "${webURL}/v1/cpm/paymentGateway/payment/search?type=Payment";
  static const approve_payment =
      "${webURL}/v1/cpm/paymentGateway/payment/approve";
  static const reject_payment =
      "${webURL}/v1/cpm/paymentGateway/payment/reject";
  static const invoice_mapping = "${webURL}/v1/invoicemapping/";
  static const payment_team_hierarchy = "${webURL}/v1/cpm/teamHierarchy/";
  static const payment_workflow_detail =
      "${webURL}/v1/TicketManagement/workflowaudit/";
  static const payment_receipt_url =
      "${webURL}/v1/Revenue/payment/generatereceipt/";
  static const invoice_receipt_url = "${webURL}/v1/Revenue/regeneratepdfsub/";
  static const trial_invoice_receipt_url =
      "${webURL}/v1/Revenue/regeneratePdfForTrail/";
  static const invoiceDownloadUrl = "${webURL}/v1/Revenue/invoicePdf/download/";
  static const trialInvoiceDownloadUrl =
      "${webURL}/v1/Revenue/trialinvoicePdf/download/";
  static const generatePdfByInvoice =
      "${webURL}/v1/Revenue/generatePdfByInvoiceId/";
  static const generateTrialPdfByInvoice =
      "${webURL}/v1/Revenue/generateTrialPdfByInvoiceId/";

  ///Revenue

  // customer module
  static const customer_invoice_list =
      "${webURL}/v1/Revenue/invoiceList/byCustomer/";
  static const record_payment = "${webURL}/v1/Revenue/record/payment";
  static const change_cust_req = "${webURL}/v1/cpm/changeStatus";
  static const charge_by_type = "${webURL}/v1/cpm/charge/ByType/";
  static const get_outwards = "${webURL}/v1/outwards/all";

  /// Revenue Report
  static const customer_revenue_report = "${webURL}/v1/Revenue/getCustomer";
  static const balanceAndCommissionInfoForShiftLocation =
      "${webURL}/v1/Revenue/balanceAndCommissionInfoForShiftLocation";
  static const get_mac_outwards =
      "${webURL}/v1/inoutWardMacMapping/getAllMACMappingByOutwardId?outwardId";
  static const parent_customer_list = "${webURL}/v1/cpm/parentCustomers/list";
  static const customer_category =
      "${webURL}/v1/SavbillCommonGateway/commonList/CustomerCategory";
  static const location_list =
      "${webURL}/v1/SavbillCommonGateway/serviceArea/getPlaceId?query=";
  static const location_to_latlong =
      "${webURL}/v1/SavbillCommonGateway/serviceArea/getLatitudeAndLongitude?placeId=";
  static const active_partner_list = "${webURL}/v1/partner/allActive";
  static const getAllServicesByServiceAreaId =
      "${webURL}/v1/cpm/serviceArea/getAllServicesByServiceAreaId";
  static const getPlanByServiceId = "${webURL}/v1/cpm/postpaidplanByService";
  static const BuildingAndSubareaNames =
      "${webURL}/v1/cpm/BuildingAndSubareaNames";

  static const pincode_to_area =
      "${webURL}/v1/SavbillCommonGateway/area/pincode?pincodeId=";
  static const plan_group = "${webURL}/v1/cpm/planGroupMappings?mode=";
  static const staffsByServiceAreaId =
      "${webURL}/v1/SavbillCommonGateway/staffsByServiceAreaId";
  static const deparment_list =
      "${webURL}/v1/SavbillCommonGateway/department/all";
  static const all_charge = "${webURL}/v1/cpm/charge/all";
  static const chargeById = "${webURL}/v1/cpm/charge";
  static const check_exist_customer =
      "${webURL}/v1/cpm/customer/customerUsernameIsAlreadyExists";
  static const add_customer = "${webURL}/v1/cpm/customers";
  static const edit_customer = "${webURL}/v1/cpm/customers";
  static const update_customer = "${webURL}/v1/cpm/customers";

  static const cancel_trial_plan = "${webURL}/v1/subscriber/cancel/trailplan";
  static const extend_days_trial_plan =
      "${webURL}/v1/subscriber/extendTrailPlan";
  static const subscribe_trial_plan_notify =
      "${webURL}/v1/subscriber/trailToNormalPlan";
  static const extend_current_plan_Validity_inBulk =
      "${webURL}/v1/cpm/subscriber/extendPlanValidityInBulk";
  static const customer_invoice_list_detail =
      "${webURL}/v1/Revenue/invoice/search?";
  static const customer_caf_invoice_list_detail =
      "${webURL}/v1/Revenue/trial/invoice/search?";
  static const customer_ledger_detail = "${webURL}/v1/Revenue/customerLedgers";
  static const customer_payment_list = "${webURL}/v1/Revenue/paymentHistory";
  static const get_customer_change_plan_dueAmount = "${webURL}/v1/Revenue/customers/getCustomerChangePlanDueAmount";
  static const customer_inventory = "${webURL}/v1/inwards/getByCustomerId";
  static const change_customer_password =
      "${webURL}/v1/cpm/portal/subscriber/updatePassword";
  static const change_status_list = "${webURL}/v1/cpm/allCustApprove";
  static const inventory_doc_view_list =
      "${webURL}/v1/SavbillInventoryManagement/inwards/inventory/documentList";

  // static const fetch_customer_discount_detail = "${webURL}/v1/subscriber/fetchCustomerDiscountDetail/";
  static const fetch_customer_discount_detail =
      "${webURL}/v1/cpm/subscriber/fetchCustomerDiscountDetailServiceLevel";
  static const update_customer_discount_detail =
      "${webURL}/v1/cpm/subscriber/changeCustomerDiscountServiceLevel/";
  static const customer_charge_list =
      "${webURL}/v1/cpm/getAllCustomerDirectChargeByCustomer/";
  static const ticket_document_view =
      "${webURL}/v1/TicketManagement/case/documentList";

  static const customer_wallet_bal = "${webURL}/v1/Revenue/wallet";
  static const check_customer_prime =
      "${webURL}/v1/isCustomerPrimeOrNot?custId=";
  static const partner_service_data =
      "${webURL}/v1/cpm/getPartnerByServiceAreaId/";
  static const assign_inventory =
      "${webURL}/v1/SavbillInventoryManagement/inwards/assignToCustomer";
  static const cust_inventory_upload_doc =
      "${webURL}/v1/SavbillInventoryManagement/inwards/inventory/document/upload/";
  static const cust_inventory_download_doc =
      "${webURL}/v1/SavbillInventoryManagement/inwards/inventory/document/download/";
  static const cust_reject_download_doc =
       "${webURL}/v1/cpm/customer-caf-image/download/";
  static const cust_inventory_delete_doc =
      "${webURL}/v1/SavbillInventoryManagement/inwards/inventory/document/delete/";
  static const getAllInventorySpecByItemId =
      "${webURL}/v1/SavbillInventoryManagement/inventorySpecification/getAllInventorySpecByItemId";

  static const online_payment_audit =
      "${webURL}/v1/SavbillIntegrationSystem/onlinePayAudit/allByCustId";
  static const online_payment_retry =
      "${webURL}/v1/SavbillIntegrationSystem/ByOrderId";
  static const addToWalletAmountByOrderId =
      "${webURL}/v1/SavbillIntegrationSystem/addToWalletByOrderId";

  static const service_area_to_plan = "${webURL}/v1/plans/";
  static const service_planmode_serviceAreaId =
      "${webURL}/v1/cpm/plans/serviceArea";
  static const plan_group_to_plan =
      "${webURL}/v1/plansByPlanGroupId?planGroupId=";
  static const change_special_plan =
      "${webURL}/v1/plansByServiceAreaCustId?custId=";

  static const cust_plan_group_findPlanGroupById =
      "${webURL}/v1/cpm/findPlanGroupById?planGroupId=";
  static const getNetworkDevicesByDeviceType =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/getNetworkDevicesByDeviceType?deviceType=";
  static const cust_plan_group_to_plan =
      "${webURL}/v1/cpm/findPlanGroupMappingByPlanGroupId?planGroupId=";
  static const cust_change_plan = "${webURL}/v1/subscriber/changePlan01";

  static const cust_charge_override =
      "${webURL}/v1/cpm/createCustChargeOverride";
  static const cust_postpaid_plan = "${webURL}/v1/cpm/postpaidplan/";
  static const cust_connection_history = "${MAIN_URL}/SavbillRadius/";

  static const update_cust_location = "${webURL}/v1/cpm/shiftCustomerLocation/";
  static const premiere_plan = "${webURL}/v1/premierePlan/";

  static const deactivate_plan = "${webURL}/v1/subscriber/deactivatePlan";
  static const get_plan_start_end_date =
      "${webURL}/v1/subscriber/getStartAndEndDate";
  static const promise_to_pay_remarks = "${webURL}/v1/subscriber/promiseToPay/";

  static const plan_service_by_cust =
      "${webURL}/v1/cpm/subscriber/getPlanByCustService/";

  static const getWifiConfig =
      "${webURL}/v1/SavbillIntegrationSystem/nmsIntegration/getWifiConfig";

  static const NMSWifiConfig =
      "${webURL}/v1/SavbillIntegrationSystem/nmsIntegration/NMSWifiConfig";

  static const serviceNickNameUpdate = "${webURL}/v1/cpm/subscriber/nickName";
  static const add_New_Service = "${webURL}/v1/cpm/subscriber/addNewService";

  static const get_product_for_non_trackable_product_category =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductForNonTrackableProductCategory";
  static const inventoryItemDeleteId =
      "${webURL}/v1/SavbillInventoryManagement/item/";
  static const inventoryItemDelete =
      "${webURL}/v1/SavbillInventoryManagement/inoutWardMacMapping/removeInventory";

  static const getTeamWorkApprovalProgressInventory =
      "${webURL}/v1/cpm/teamHierarchy/getApprovalProgress";
  static const getCustomerTeamWorkApprovalProgressInventory =
      "${webURL}/v1/SavbillInventoryManagement/teamHierarchy/getApprovalProgress";
  static const customerWorkFlowAuditInventory =
      "${webURL}/v1/SavbillInventoryManagement/workflowaudit/list";

  static const customerApproveReplaceInventory =
      "${webURL}/v1/SavbillInventoryManagement/inwards/approveReplaceInventory";
  static const productByMacSerial =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductsByMacSerial";
  static const getAllCustomerInventoryListUrl =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getAllCustomerInventoryList";

  static const inventoryReactivateBox =
      "${webURL}/v1/inwards/reactivateBoxResponse";
  static const inwards_by_product_staff =
      "${webURL}/v1/outwards/getItemHistoryByProduct";
  static const replacementMacAddressIds =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getItemBasedOnCondtion";

  static const payment_owner_change_plan =
      "${webURL}/v1/SavbillCommonGateway/getstaffuserbyserviceareaid";
  static const get_invoice_payment_mapping_list =
      "${webURL}/v1/Revenue/paymentmapping/";

  /// customer credit Note
  ///
  static const customer_credit_note_list =
      "${webURL}/v1/Revenue/payment/search?type=CreditNote";

  // Inventory management

  static const view_vendor_add =
      "${webURL}/v1/SavbillInventoryManagement/vendor/save";
  static const view_vendor_update =
      "${webURL}/v1/SavbillInventoryManagement/vendor/update";
  static const view_vendor_delete =
      "${webURL}/v1/SavbillInventoryManagement/vendor/delete";
  static const view_vendor_getById =
      "${webURL}/v1/SavbillInventoryManagement/vendor/getById";
  static const view_vendor_manufacturer =
      "${webURL}/v1/SavbillInventoryManagement/vendor";

  static const view_product_category =
      "${webURL}/v1/SavbillInventoryManagement/productCategory";
  static const product_category_add =
      "${webURL}/v1/SavbillInventoryManagement/productCategory/save";

  static const product_category_edit =
      "${webURL}/v1/SavbillInventoryManagement/productCategory/update";
  static const product_category_delete =
      "${webURL}/v1/SavbillInventoryManagement/productCategory/delete";

  static const view_product = "${webURL}/v1/SavbillInventoryManagement/product";
  static const product_delete =
      "${webURL}/v1/SavbillInventoryManagement/product/delete";
  static const product_add =
      "${webURL}/v1/SavbillInventoryManagement/product/save";
  static const product_edit =
      "${webURL}/v1/SavbillInventoryManagement/product/update";

  static const getAllActiveProductCategoriesList =
      "${webURL}/v1/SavbillInventoryManagement/productCategory/getAllActiveProductCategories";
  static const product_manufacturer_all =
      "${webURL}/v1/SavbillInventoryManagement/vendor/findAll";
  static const view_pop =
      "${webURL}/v1/SavbillInventoryManagement/popmanagement";

  static const pop_delete =
      "${webURL}/v1/SavbillInventoryManagement/popmanagement/delete";
  static const pop_add =
      "${webURL}/v1/SavbillInventoryManagement/popmanagement/save";

  static const pop_edit =
      "${webURL}/v1/SavbillInventoryManagement/popmanagement/update";
  static const pop_staff_service_area =
      "${webURL}/v1/SavbillCommonGateway/serviceArea/getAllServiceAreaByStaff";

  static const get_all_serialized_product_item =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllNetworkandNaBindProduct";
  static const get_all_non_serialized_product_item =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllNetworkAndNABindNonSerializedProduct";

  static const view_pop_inventory =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getByOwnerIdAndType";
  static const getNonTrackableProductQtyUrl =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getNonTrackableProductQty";

  static const getAvailableQtyDetailsByProductAndDestination =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getAvailableQtyDetailsByProductAndDestination";
  static const assignNonSerializedItemToEndOwner =
      "${webURL}/v1/SavbillInventoryManagement/inwards/assignNonSerializedItemToEndOwner";

  static const updateItemMacAndSerial =
      "${webURL}/v1/SavbillInventoryManagement/item/updateItemMacAndSerial";
  static const get_product_mac_address_list =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getItemHistoryByProduct";

  static const get_assign_inventory_to_end_owner =
      "${webURL}/v1/SavbillInventoryManagement/inwards/assignToEndOwner";
  static const replaceInventoryCustomer =
      "${webURL}/v1/SavbillInventoryManagement/inwards/replaceInventory";
  static const all_pop_for_outward =
      "${webURL}/v1/SavbillInventoryManagement/popmanagement/all";

  static const product_all_active_product_category =
      "${webURL}/v1/SavbillInventoryManagement/productCategory/getAllActiveProductCategoriesByCB";
  static const getAllTeamBasedInventory =
      "${webURL}/v1/SavbillInventoryManagement/teams/getAllTeamBasedOnAttchedStaff";
  static const get_active_product =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllActiveProduct";

  static const get_inventory_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/inventoryJobType";

  static const get_nature =
      "${webURL}/v1/SavbillCommonGateway/commonList/nature";

  static const all_ware_houses =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/getAllWareHouses";
  static const approveInventory =
      "${webURL}/v1/SavbillInventoryManagement/inwards/approveInventory";

  static const assign_product_by_product_inventory =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductByProductCategory";
  static const assign_product_by_service_id_inventory =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductByServiceId";

  static const product_all_product_category =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllActiveProductsByProductCategoryId";
  static const product_direct_charge =
      "${webURL}/v1/cpm/product/getAllChargeType/CUSTOMER_DIRECT";
  static const view_product_category_all = "${webURL}/v1/productCategory/all";
  static const getAllPlanInventoryIdOnPlanId =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllPlanIvnetoryIdOnPlanId";
  static const getProductCategoryByPlanId =
      "${webURL}/v1/SavbillInventoryManagement/product_plan_mapping/getProductCategoryByPlanId";
  static const getProductByPlanId =
      "${webURL}/v1/SavbillInventoryManagement/product_plan_mapping/getProductByPlanId";

  static const getItemBasedOnProductType =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getItemBasedOnProductType";
  static const getProductMappingDetails =
      "${webURL}/v1/SavbillInventoryManagement/product/getMappingDetails";

  static const saveAllInventoryRequest =
      "${webURL}/v1/SavbillInventoryManagement/outwards/saveAllInventoryRequest";
  static const get_case_package_url = "${webURL}/v1/cpm/casepackage/all";
  static const assignInventoryFromStaffList =
      "${webURL}/v1/SavbillInventoryManagement/inwards/assignFromStaffList";
  static const assignInventoryEveryStaff =
      "${webURL}/v1/SavbillInventoryManagement/teamHierarchy/assignEveryStaff";

  //  Assign Inventory With External Item Group

  static const getExternalItemGroupAllProductsByCustomer =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductsByCustomerOwned";
  static const getAllExternalItemGroupByProductAndStaff =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/getAllExternalItemGroupByProductAndStaff";
  static const getAllMACMappingByExternalId =
      "${webURL}/v1/SavbillInventoryManagement/inoutWardMacMapping/getAllMACMappingByExternalId";
  static const getAllCustomerInventoryDetailsHistory =
      "${webURL}/v1/SavbillInventoryManagement/item/getAllCustomerInvetoryDetailshistory";

  static const view_warehouse =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement";
  static const warehouse_delete =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/delete";
  static const warehouse_parent_service_area =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/getAllParentServiceAreaList";

  static const warehouse_add =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/save";
  static const warehouse_edit =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/update";
  static const warehouse_to_parent_service_area =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/getAllParentServiceAreaListByWarehouseId/";

  static const get_all_active_warehouse =
      "${webURL}/v1/SavbillInventoryManagement/warehouseManagement/getAllActiveWarehouse";

  static const view_inwards = "${webURL}/v1/SavbillInventoryManagement/inwards";
  static const delete_inwards =
      "${webURL}/v1/SavbillInventoryManagement/inwards/delete";
  static const add_inwards =
      "${webURL}/v1/SavbillInventoryManagement/inwards/save";
  static const edit_inwards =
      "${webURL}/v1/SavbillInventoryManagement/inwards/update";

  static const inwards_details =
      "${webURL}/v1/SavbillInventoryManagement/inwards";
  static const outwards_details =
      "${webURL}/v1/SavbillInventoryManagement/outwards";

  static const view_outwards =
      "${webURL}/v1/SavbillInventoryManagement/outwards";
  static const delete_outwards =
      "${webURL}/v1/SavbillInventoryManagement/outwards/delete";
  static const add_outwards =
      "${webURL}/v1/SavbillInventoryManagement/outwards/save";
  static const edit_outwards =
      "${webURL}/v1/SavbillInventoryManagement/outwards/update";

  static const view_external_lite =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement";
  static const delete_external_lite =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/delete";
  static const add_external_lite =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/save";
  static const edit_external_lite =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/update";

  static const external_lite_mac_map_save =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmacserialmapping/save";
  static const external_lite_mac_map_view =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmacserialmapping/getExternalItemGroupMacSerialMapping?externalItemId=";
  static const external_group_approve_reject =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/externalItemApproval";
  static const external_group_customer_owner_list =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/getCustomerListServiceArea";
  static const external_group_partner_owner_list =
      "${webURL}/v1/SavbillInventoryManagement/externalitemmanagement/getPartnerListServiceArea";

  static const view_bulk_consumption =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption";
  static const add_bulk_consumption =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/save";
  static const edit_bulk_consumption =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/update";
  static const view_bulk_consumption_search =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/searchByNamebybulkconsumption";
  static const view_mapping_bulk_consumption =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/getBulkConsumptionMapping?bulkconsumptionId=";
  static const get_item_for_inward =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getItemForInward";
  static const save_request_inventory =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/save";

  static const on_behalf_off_requester =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory";
  static const assigned_request_inventory_list =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/getAllAssignedRequestInventory";

  static const view_inwards_mac_mapping =
      "${webURL}/v1/inoutWardMacMapping/getInwardMacMapping?inwardId=";

  // static const delete_inwards_mac_mapping = "${webURL}/v1/inoutWardMacMapping/";
  static const delete_inwards_mac_mapping =
      "${webURL}/v1/SavbillInventoryManagement/inwards/item";

  // static const view_outwards_mac_mapping = "${webURL}/v1/inoutWardMacMapping/getbyoutwardid?id=";
  static const view_outwards_mac_mapping_by_inward =
      "${webURL}/v1/inoutWardMacMapping/getbyinwardid?id=";
  static const add_inwards_mac_mapping =
      "${webURL}/v1/inoutWardMacMapping/save";
  static const save_manual_mac_serial =
      "${webURL}/v1/SavbillInventoryManagement/inwards/saveManualMacSerial";

  static const inwards_approve_reject =
      "${webURL}/v1/SavbillInventoryManagement/inwards/inwardApproval";
  static const inventory_request_fulfilment_by_id =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/getById";
  static const products_all = "${webURL}/v1/product/all";
  static const warehouse_all_active = "${webURL}/v1/warehouseManagement/all";
  static const all_partner =
      "${webURL}/v1/SavbillInventoryManagement/partner/all";

  static const all_active_partner =
      "${webURL}/v1/SavbillInventoryManagement/partner/allActive";
  static const inwards_for_outwards =
      "${webURL}/v1/inwards/SavbillInventoryManagement/getInwardDetailsByProductAndDestination";
  static const new_all_partner = "${webURL}/v1/cpm/partner/all";
  static const approveChangeDiscountService =
      "${webURL}/v1/cpm/approveChangeDiscountServiceLevel";
  static const outward_mac_mapping =
      "${webURL}/v1/SavbillInventoryManagement/inoutWardMacMapping/updateMACMappingList";
  static const view_assigned_inventory = "${webURL}/v1/outwards";
  static const view_assigned_inventory_customer =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getCustomerInventoryMappingByStaffId";

  static const view_request_inventory_list =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/getAllByCurrentStaff";
  static const assigned_req_invent_approve_status =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/approveStatus";
  static const view_assigned_inventory_pop =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getPopByInventoryMappingByStaffId";
  static const view_assigned_inventory_service_area =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getServiceAreaByInventoryMappingByStaffId";

  static const save_forward_req_inv =
      "${webURL}/v1/requestinventory/forwardReqInv";
  static const delete_request_inventory =
      "${webURL}/v1/SavbillInventoryManagement/requestinventory/delete";
  static const branch_by_service_area =
      "${webURL}/v1/branchManagement/getBranchByServiceArea";

  static const bulk_consumption_inwards_data =
      "${webURL}/v1/inwards/getAllInwardByProductAndStaffforPopandSeriveareaandCustomer";
  static const delete_pop_mapping =
      "${webURL}/v1/inoutWardMacMapping/removeInventoryfromowner?macMappingId=";
  static const inoutwards_mapping_data_im =
      "${webURL}/v1/inoutWardMacMapping/getAllMACMappingByInwardId?inward_id=";

  static const all_inventory_data =
      "${webURL}/v1/SavbillInventoryManagement/item/";
  static const removeInventoryById =
      "${webURL}/v1/SavbillInventoryManagement/item/getItemDetails";
  static const generateRemoveInventoryRequest =
      "${webURL}/v1/SavbillInventoryManagement/inoutWardMacMapping/generateRemoveInventoryRequest";
  static const all_inwards =
      "${webURL}/v1/SavbillInventoryManagement/inwards/all";

  static const return_inventory_item =
      "${webURL}/v1/SavbillInventoryManagement/item/return";
  static const change_warranty_item =
      "${webURL}/v1/SavbillInventoryManagement/item/updateItemWarrantyByList";
  static const change_item_status =
      "${webURL}/v1/SavbillInventoryManagement/item/updateItemStatusByList";
  static const change_ownership_status =
      "${webURL}/v1/SavbillInventoryManagement/item/updateItemOwnerShipStatusByList";
  static const update_item_type =
      "${webURL}/v1/SavbillInventoryManagement/item/updateItemTypeByList";

  static const activePlanList = "${webURL}/v1/cpm/subscriber/getActivePlanList";
  static const customerServiceTermination =
      "${webURL}/v1/cpm/subscriber/deleteService";
  static const customerServiceHold =
      "${webURL}/v1/cpm/subscriber/holdServiceInBulk";
  static const customerServiceResume =
      "${webURL}/v1/cpm/subscriber/resumeServiceInBulk";
  static const custServiceStatusAudit =
      "${webURL}/v1/cpm/subscriber/servicestatusAudit";

  // Stop Service InBulk
  static const custStopServiceInBulk =
      "${webURL}/v1/cpm/subscriber/stopServiceInBulk";

  // ticketing system

  static const ticket_assigned_to_me =
      "${webURL}/v1/TicketManagement/case/filter";
  static const view_tat_ticket_list =
      "${webURL}/v1/TicketManagement/tickettatmatrix";
  static const delete_tat_ticket =
      "${webURL}/v1/TicketManagement/tickettatmatrix/delete";
  static const add_tat_ticket =
      "${webURL}/v1/TicketManagement/tickettatmatrix/save";
  static const edit_tat_ticket =
      "${webURL}/v1/TicketManagement/tickettatmatrix/update";
  static const tat_for_ticket =
      "${webURL}/v1/TicketManagement/tickettatmatrix/searchByStatus";

  static const view_root_cause_list =
      "${webURL}/v1/TicketManagement/resolutionReasons";
  static const delete_root_cause =
      "${webURL}/v1/TicketManagement/resolutionReasons/delete";
  static const add_root_cause =
      "${webURL}/v1/TicketManagement/resolutionReasons/save";
  static const edit_root_cause =
      "${webURL}/v1/TicketManagement/resolutionReasons/update";
  static const view_resolutionReasonSubCategory =
      "${webURL}/v1/TicketManagement/resolutionReasons/searchBySubCategory";

  static const view_problem_domain_list =
      "${webURL}/v1/TicketManagement/ticketReasonCategory";
  static const delete_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/delete";
  static const add_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/save";
  static const edit_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/update";

  static const view_sub_problem_domain_list =
      "${webURL}/v1/TicketManagement/ticketReasonSubCategory";
  static const delete_sub_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonSubCategory/delete";
  static const add_sub_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonSubCategory/save";
  static const edit_sub_problem_domain =
      "${webURL}/v1/TicketManagement/ticketReasonSubCategory/update";
  static const ticket_reason_category =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/getAllActiveReasonCatgory";
  static const customer_ticket =
      "${webURL}/v1/TicketManagement/case/getCasesByCustomer";
  static const staff_user_all_active =
      "${webURL}/v1/SavbillInventoryManagement/staffuser/allActive";
  static const get_all_product_based_item_type =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllProductbasedOnItemType";
  static const get_all_serialized_item_base_on_product =
      "${webURL}/v1/SavbillInventoryManagement/product/getAllSerializedItemBaseOnProduct";

  /// Bulk Consumption
  static const bulk_cons_approve_reject_status =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/approveStatus";
  static const bulk_consumption_delete_item =
      "${webURL}/v1/SavbillInventoryManagement/bulk_consumption/delete";

  static const get_active_service_subscriber =
      "${webURL}/v1/cpm/ticketReasonCategory/getActiveServiceForSubscribers";
  static const all_ticket_reason_category =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/all";
  static const assign_ticket_staff_list =
      "${webURL}/v1/TicketManagement/case/reassignTicket?caseId=";
  static const ticket_reason_category_by_active_services =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/getReasonCategoryByActiveServices";
  static const ticket_get_serial_number =
      "${webURL}/v1/cpm/subscriber/getSerialNumber";
  static const ticket_reason_category_customer =
      "${webURL}/v1/TicketManagement/ticketReasonCategory/getReasonCategoryByCustomer?customerId=";
  static const ticket_sub_category_by_parent_category =
      "${webURL}/v1/TicketManagement/ticketReasonSubCategory/getSubCategoryReasons?parentCategoryId=";
  static const view_ticket = "${webURL}/v1/TicketManagement/case";
  static const create_ticket = "${webURL}/v1/TicketManagement/case/save";
  static const edit_ticket = "${webURL}/v1/TicketManagement/case/updateDetails";
  static const assign_pick_ticket =
      "${webURL}/v1/TicketManagement/case/assignPickedTicket";
  static const assign_pick_task =
      "${webURL}/v1/TaskManagement/case/assignPickedTicket";

  // static const link_ticket = "${webURL}/v1/TicketManagement/case/linkTicket";
  static const link_ticket =
      "${webURL}/v1/TicketManagement/case/linkBulkTicket";
  static const ticket_upload_document =
      "${webURL}/v1/TicketManagement/case/updateDocumentDetails?caseId=";

  static const ticket_download_document =
      "${webURL}/v1/TicketManagement/case/document/download";
  static const ticket_doc_delete_document =
      "${webURL}/v1/TicketManagement/case/document/delete/";
  static const task_doc_delete_document =
      "${webURL}/v1/TaskManagement/case/document/delete/";
  static const ticket_etr_customer =
      "${webURL}/v1/TicketManagement/case/sendETRtoCustomer";

  static const ticket_follow_up_save =
      "${webURL}/v1/TicketManagement/ticketFollowupDetails/save";
  static const ticket_follow_up_find_all =
      "${webURL}/v1/TicketManagement/ticketFollowUp/findAll";
  static const showTatDetails =
      "${webURL}/v1/TicketManagement/case/getTatDetials";

  static const getTicketETRReport =
      "${webURL}/v1/TicketManagement/case/getTicketETRReport";
  static const getTicketTATAuditDetail =
      "${webURL}/v1/TicketManagement/case/getTatAuditDetails";
  static const getCustomerCafFollowUp = "${webURL}/v1/cpm/cafFollowUp/findAll";

  static const isCustomerDocPending =
      "${webURL}/v1/cpm/custDoc/isCustDocPending";

  // approvals pending
  // customer
  static const ap_customers =
      "${webURL}/v1/cpm/dashboard/approval/getCustomersApprovals";
  static const customers_approve_reject = "${webURL}/v1/cpm/approveCaf";

  //termination
  static const ap_customers_termination =
      "${webURL}/v1/cpm/dashboard/approval/getCustomersApprovalsForTermination";
  static const customers_termination_approve_reject =
      "${webURL}/v1/cpm/changeStatusCustomerApprove/";

  static const customer_change_status_approve_reject =
      "${webURL}/v1/cpm/changeStatusCustomerApprove/";

  // plan
  static const ap_plan_list =
      "${webURL}/v1/cpm/dashboard/approval/getPlanApprovalsList";
  static const plan_approve_reject = "${webURL}/v1/cpm/approvePlan";

  static const get_special_plan_mapping_approvals =
      "${webURL}/v1/cpm/dashboard/approval/getSpecialPlanMappingApprovals";

  // plan group
  static const ap_plan_group_list =
      "${webURL}/v1/cpm/dashboard/approval/getPlanGroupApprovalsList";
  static const plan_group_approve_reject = "${webURL}/v1/approvePlanGroup";

  // payment group
  static const ap_payment_list =
      "${webURL}/v1/cpm/dashboard/approval/getPaymentApprovalsList";
  static const payment_approve_reject = "${webURL}/v1/cpm/payment/";

  // Lead Approval Dashboard
  // Assigned Lead List
  static const la_assign_lead_list =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAllByCurrentUser";

  // Lead Followup List
  static const la_team_follow_up_lead_list =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/findAllByCurruntUserAndTeam";

  // Lead Approval List
  static const la_team_lead_approval_list =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAllByCurrentUserTeamLead";

  // customer document
  //getCustomerDocForApprovals
  static const ap_customer_doc_approval =
      "${webURL}/v1/cpm/dashboard/approval/getCustomerDocForApprovals";

  //Team Lead Followup List
  static const la_followup_approval_lead_list =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/findAllByCurruntUser";

  //inventory dashboard

  //Product Quantity of Staff
  static const getProductQtyByStaff =
      "${webURL}/v1/SavbillInventoryManagement/dashboard/inventory/getProductQtyByStaff";

  // Product Quantity By WareHouse
  static const getProductQtyByWarehouse =
      "${webURL}/v1/SavbillInventoryManagement/dashboard/inventory/getProductQtyByWarehouse";

  // tickets
  // static const ap_ticket_list = "${webURL}/v1/cpm/dashboard/approval/getTicketApprovals";
  static const ap_ticket_list =
      "${webURL}/v1/TicketManagement/case/approval/getTicketApprovals";
  static const ticket_assign_staff = "${webURL}/v1/TicketManagement/case/";
  static const ticket_approve_reject = "${webURL}/v1/cpm/teamHierarchy/";

  //Change Discount
  static const ap_change_discount =
      "${webURL}/v1/cpm/dashboard/approval/getChangeDiscountApprovals";
  static const change_discount_approve_reject =
      "${webURL}/v1/cpm/approveChangeDiscount";
  static const get_Open_lead_approval =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAllByCurrentUserTeamLead";

  /// customer shift locaiton
  static const customer_new_address_shift_location_list =
      "${webURL}/v1/cpm/newcustomeraddress/";
  static const approveCustomerAddress =
      "${webURL}/v1/cpm/approveCustomerAddress";

  //Invoices

  static const ap_invoice_list =
      "${webURL}/v1/cpm/dashboard/approval/getBillToOrgApprovals";
  static const invoice_approve_reject = "${webURL}/v1/approveChangeDiscount";

  // static const void_invoice = "${webURL}/v1/invoiceV2/voidInvoice";
  static const void_invoice = "${webURL}/v1/Revenue/voidInvoice";
  static const cancel_and_regenerate =
      "${webURL}/v1/Revenue/cancelAndRegenerate";
  static const invoiceDetails = "${webURL}/v1/Revenue/invoiceDetails";
  static const custInvoiceDownload =
      "${webURL}/v1/Revenue/documentForInvoice/download";
  static const custInvoiceTra = "${webURL}/v1/Revenue/invoice/reSendQrPayload";
  static const checkInvoiceIntegration =
      "${webURL}/v1/SavbillIntegrationSystem/thirdPartyMenu/getThirdPartyConfigurationByEvent";

  // partner

  static const ap_partner_list =
      "${webURL}/v1/cpm/dashboard/approval/getPartnerPaymentApprovals";
  static const get_all_type_partner =
      "${webURL}/v1/SavbillInventoryManagement/partner/getAllTypePartner";
  static const partner_approve_reject =
      "${webURL}/v1/approvePartnerBalance"; // need to change
  static const invoice_payment_adjust = "${webURL}/v1/invoicePaymentAdjust";
  static const ap_inventory_approval =
      "${webURL}/v1/SavbillInventoryManagement/inwards/getInventoryApprovals";

  // Network Management
  static const device_list =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/";
  static const device_detail =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/";
  static const update_device_location =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/update";
  static const delete_device =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/delete";
  static const check_port_availability =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/checkPortAvailability";
  static const get_bind_device_port_detail =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/boundParents";
  static const get_parent_device_for_bind =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/availableParents";
  static const update_device_port_bind =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/deviceChildParentBinding";
  static const network_device_product =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/getAllProduct";
  static const network_inward_product =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/getAllInwardByProduct";
  static const network_add_product =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/save";
  static const get_assign_outward_mapping_item =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getAssignOutwardItem";
  static const get_item_for_mac_mapping_outward =
      "${webURL}/v1/SavbillInventoryManagement/outwards/getItemForOutward";
  static const approveInventoryFromOwner =
      "${webURL}/v1/SavbillInventoryManagement/inwards/approveInventoryFromOwner";

  /// assignNonSerializedItemToCustomer
  static const assignNonSerializedItemToCustomer =
      "${webURL}/v1/SavbillInventoryManagement/inwards/assignNonSerializedItemToCustomer";

  /// cpm Module Url(Customer Management System)

  static const customer_list = "${webURL}/v1/cpm/customers/list";
  static const customer_search = "${webURL}/v1/cpm/customers/search";
  static const customer_detail = "${webURL}/v1/cpm/customers";
  static const delete_customer = "${webURL}/v1/cpm/customers";
  static const send_payment_link =
      "${webURL}/v1/cpm/generatePaytmLinkAndSend?custId=";
  static const nearby_devices =
      "${webURL}/v1/SavbillInventoryManagement/NetworkDevice/getNearbyDevices";
  static const customer_document = "${webURL}/v1/cpm/custDoc/getDocsByCustomer";
  static const customer_upload_document = "${webURL}/v1/cpm/custDoc/uploadDoc";
  static const approve_upload_customer_doc =
      "${webURL}/v1/cpm/custDoc/approveUploadCustomerDoc";
  static const verify_customer_document = "${webURL}/v1/cpm/verifyDocument";
  static const customer_upload_document_Update =
      "${webURL}/v1/cpm/custDoc/update";
  static const customer_upload_document_Delete =
      "${webURL}/v1/cpm/custDoc/delete";
  static const customer_document_verification_cpm =
      "${webURL}/v1/cpm/commonList/custdocverificationmodes";
  static const cust_document_sub_type =
      "${webURL}/v1/cpm/commonList/custdocsubtype";
  static const getCustomerNetworkDetails =
      "${webURL}/v1/SavbillInventoryManagement/customer/getCustNetworkDetail";

  static const postpaid_plan = "${webURL}/v1/cpm/postpaidplan/all";
  static const workFlowAuditInventroyProcess =
      "${webURL}/v1/cpm/workflowaudit/list";
  static const inventory_taxes_all = "${webURL}/v1/cpm/taxes/all";

  static const customer_audit_details =
      "${webURL}/v1/cpm/auditLog/getAuditList";
  static const customer_dunning_management =
      "${webURL}/v1/cpm/dunnninghistory/findByPartnerOrCustomerId";
  static const customer_dunning_status_change =
      "${webURL}/v1/cpm/customer/changedunningenabalestatus";
  static const customer_notification_management =
      "${MAIN_URL}/SavbillNotification/findByCustomerUsername";
  static const customer_notification_status_change =
      "${webURL}/v1/cpm/customer/changenotificationenabalestatus";

  // credit module
  static const get_creditNote_list =
      "${webURL}/v1/cpm/paymentGateway/payment/search?type=CreditNote";

  // static const credit_invoice_list = "${webURL}/v1/cpm/invoiceListForCreditNote/byCustomer";

  static const credit_invoice_list =
      "${webURL}/v1/Revenue/invoiceListForCreditNote/byCustomer";
  static const creditNote_assign_every_staff =
      "${webURL}/v1/cpm/teamHierarchy/assignEveryStaff";
  static const creditNote_pick_up_flow =
      "${webURL}/v1/cpm/workflow/pickupworkflow";
  static const creditNote_reassign_workflow_get_staff_list =
      "${webURL}/v1/cpm/teamHierarchy/reassignWorkflowGetStaffList";
  static const reassignWorkflow =
      "${webURL}/v1/cpm/teamHierarchy/reassignWorkflow";
  static const assignFromStaffCreditNoteList =
      "${webURL}/v1/cpm/teamHierarchy/assignFromStaffList";
  static const plan_service = "${webURL}/v1/cpm/planservice/all";
  static const assignFromStaffTicketList =
      "${webURL}/v1/TicketManagement/teamHierarchy/assignFromStaffList";
  static const assignFromEveryStaffTicketList =
      "${webURL}/v1/TicketManagement/case/assignEveryStaffFromList";

  ///ip pool

  static const ip_pool_list = "${webURL}/v1/SavbillInventoryManagement/ippool";
  static const save_ip_management =
      "${webURL}/v1/SavbillInventoryManagement/ippool/save";
  static const update_ip_management =
      "${webURL}/v1/SavbillInventoryManagement/ippool/update";
  static const delete_ip_management =
      "${webURL}/v1/SavbillInventoryManagement/ippool/delete";

  /// customer 360 Module

  /// Plan
  static const plansUrl = "${webURL}/v1/cpm/subscriber";
  static const custServiceTermination =
      "${webURL}/v1/cpm/subscriber/approveCustomerServiceTermination";
  static const get_trial_plan =
      "${webURL}/v1/cpm/portal/subscriber/getTrialPlanList";
  static const get_future_plan = "${plansUrl}/getFuturePlanList";
  static const get_expired_plan = "${plansUrl}/getExpiredPlanList";
  static const get_active_plan = "${plansUrl}/getActivePlanList";
  static const get_plan_by_filter = "${webURL}/v1/cpm/getPlansByFilters";

  /// Common Api GateWay

  // auth & user detail
  static const login = "${webURL}/v1/SavbillCommonGateway/login";
  static const generateOtpValidate =
      "${webURL}/v1/SavbillCommonGateway/otp/generate";

  //user role
  static const role_operation =
      "${webURL}/v1/SavbillCommonGateway/acl/getRoleOperations";
  static const get_demo_graphic_mapping =
      "${webURL}/v1/SavbillCommonGateway/getdemographicmapping";
  static const pincode_detail = "${webURL}/v1/SavbillCommonGateway/pincode";
  static const getAllBranchesByServiceAreaId =
      "${webURL}/v1/SavbillCommonGateway/branchManagement/getAllBranchesByServiceAreaId";
  static const device_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/networkDeviceType";
  static const ticket_priority =
      "${webURL}/v1/SavbillCommonGateway/commonList/ticket_priority";
  static const ticket_source_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/ticketSourceType";
   static const ticket_classification =
       "${webURL}/v1/SavbillCommonGateway/commonList/ticketClassification";
  static const view_case_condition =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/CASE_CONDITION";
  static const dtv_category =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/dtvCategory";
  static const department_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/departmentType";
  static const service_area_to_pincode =
      "${webURL}/v1/SavbillCommonGateway/pincode/getPincodeListByServiceId";
  static const valley_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/valleyType";
  static const inside_valley =
      "${webURL}/v1/SavbillCommonGateway/commonList/insideValley";
  static const outside_valley =
      "${webURL}/v1/SavbillCommonGateway/commonList/outsideValley";
  static const customer_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/Customer_Type";
  static const customer_sector =
      "${webURL}/v1/SavbillCommonGateway/commonList/Customer_Sector";
  static const customer_document_verification =
      "${webURL}/v1/SavbillCommonGateway/commonList/custdocverificationmode";

  static const cust_document_status =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/docStatus";
  static const bill_to = "${webURL}/v1/SavbillCommonGateway/commonList/billTo";
  static const payment_mode =
      "${webURL}/v1/SavbillCommonGateway/commonList/paymentMode";
  static const customer_status =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/custStatus";
  static const generic_request =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/";
  static const customer_title =
      "${webURL}/v1/SavbillCommonGateway/commonList/title";
  static const get_case_status =
      "${webURL}/v1/SavbillCommonGateway/commonList/caseStatus";
  static const get_case_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/caseType";
  static const customer_sub_type =
      "${webURL}/v1/SavbillCommonGateway/commonList/";
  static const get_all_team_list =
      "${webURL}/v1/SavbillCommonGateway/teams/getAllTeamsWithoutPagination?from_cache=true";
  static const ticket_staff_detail =
      "${webURL}/v1/SavbillCommonGateway/staffuser/";
  static const payment_owner_list =
      "${webURL}/v1/SavbillCommonGateway/staffuser/Activestaff";

  static const service_area_detail =
      "${webURL}/v1/SavbillCommonGateway/serviceArea";
  static const area_detail = "${webURL}/v1/SavbillCommonGateway/area";
  static const customer_quota =
      "${webURL}/v1/SavbillCommonGateway/customer/custQuota";

  static const service_area_new =
      "${webURL}/v1/SavbillCommonGateway/serviceArea/dropdown/all";
  static const service_area_caf_new =
      "${webURL}/v1/SavbillCommonGateway/serviceArea/dropdown/all/caf/customer";
  static const system_configuration =
      "${webURL}/v1/SavbillCommonGateway/system/configuration/";
  static const sub_area = "${webURL}/v1/SavbillCommonGateway/subarea/all";

  static const sub_area_new = "${webURL}/v1/SavbillCommonGateway/subarea";
  static const sub_area_with_pagination = "${webURL}/v1/SavbillCommonGateway/subarea/allWithPagination";
  static const building_mgmt =
      "${webURL}/v1/SavbillCommonGateway/buildingmgmt/all";
  static const buildingReferenceAll =
      "${webURL}/v1/SavbillCommonGateway/buildingRefrence/all";
  static const get_area_all = "${webURL}/v1/SavbillCommonGateway/area/all";
  static const get_pincode_all =
      "${webURL}/v1/SavbillCommonGateway/pincode/getAll";
  static const get_building_mgmt_numbers =
      "${webURL}/v1/SavbillCommonGateway/buildingmgmt/getBuildingMgmtNumbers";

  static const getServic_AreaIdBy_Pincode =
      "${webURL}/v1/SavbillCommonGateway/pincode/getServicAreaIdByPincode";
  static const get_building_mgmt =
      "${webURL}/v1/SavbillCommonGateway/buildingmgmt/getBuildingMgmt";
  static const get_area_id_from_sub_area_id =
      "${webURL}/v1/SavbillCommonGateway/subarea/getAreaIdFromSubAreaId";
  static const get_subArea_from_area =
      "${webURL}/v1/SavbillCommonGateway/subarea/getSubAreaFromArea";

  ///Country, City, State, PinCode

  // static const get_all_pincode = "${webURL}/v1/SavbillCommonGateway/pincode/all";// Old Api's
  static const get_all_pincode =
      "${webURL}/v1/SavbillCommonGateway/pincode/getAll"; // New Api's
  static const get_all_city = "${webURL}/v1/SavbillCommonGateway/city/all";
  static const get_all_state = "${webURL}/v1/SavbillCommonGateway/state/all";
  static const get_all_country =
      "${webURL}/v1/SavbillCommonGateway/country/all";
  static const get_staff_user_list =
      "${webURL}/v1/SavbillCommonGateway/staffuser/list?product=BSS";
  static const bank_list_data =
      "${webURL}/v1/SavbillCommonGateway/bankManagement/searchByStatus";
  static const get_partner_detail = "${webURL}/v1/pms/partner";
  static const getOfferPriceWithTax =
      "${webURL}/v1/Revenue/getOfferPriceWithTax/plan";

  /// CAF Follow Up
  static const getCloseFollowUp = "${webURL}/v1/cpm/cafFollowUp/closefollowup";
  static const getCafFollowUpRemark =
      "${webURL}/v1/cpm/cafFollowUp/findAll/cafFollowUpRemark";
  static const addCafFollowUpRemark =
      "${webURL}/v1/cpm/cafFollowUp/cafFollowUp/remark";
  static const rescheduleFollowUp =
      "${webURL}/v1/cpm/cafFollowUp/reSchedulefollowup";
  static const scheduleFollowUpSave = "${webURL}/v1/cpm/cafFollowUp/save";
  static const generateNameOfTheCafFollowUp =
      "${webURL}/v1/cpm/cafFollowUp/generateNameOfTheCafFollowUp";
  static const rejectReasonCaf = "${webURL}/v1/cpm/caf/rejectReason/all";
  static const postCloseCaf = "${webURL}/v1/cpm/caf/close";
  static const approveCustomerCAF = "${webURL}/v1/cpm/approveCaf";
  static const reactivateService = "${webURL}/v1/cpm/reactivateService";

  /// Lead Management

  static const lead_master_list = "${webURL}/v1/SavbillSalesCrmsBss/leadMaster";
  static const lead_add_notes =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/add/notes";
  static const leadAllRejectedReasonList =
      "${webURL}/v1/SavbillSalesCrmsBss/rejectReason/allRejectedReasonsList";
  static const lead_approve_reject =
      "${webURL}/v1/cpm/teamHierarchy/approveLead";
  static const assignFromStaffListForLead =
      "${webURL}/v1/cpm/teamHierarchy/assignFromStaffListForLead";
  static const leadReopen =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/lead/reopen";
  static const allRejectedReasonLead =
      "${webURL}/v1/SavbillSalesCrmsBss/rejectReason/all";
  static const saveCloseLead =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/lead/close";
  static const reOpenLead =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/lead/reopen";
  static const reassignLead = "${webURL}/v1/cpm/teamHierarchy/reassignLead";
  static const updateLeadAssignee =
      "${webURL}/v1/cpm/teamHierarchy/updateLeadAssignee";
  static const leadDetailById =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findById";
  static const leadServiceArea =
      "${webURL}/v1/SavbillCommonGateway/serviceArea";
  static const leadStatusProgress =
      "${webURL}/v1/cpm/teamHierarchy/getApprovalProgressForLead";
  static const allLeadAudit =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAllLeadAudit";
  static const allLeadNotes =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAllLeadNoteWithPagination";

  static const generateLeadNo =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/generateLeadNo";
  static const requiredServiceType =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/requireServiceTypes";
  static const leadType =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/leadType";
  static const lead_origin_types =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/leadOriginTypes";
  static const lead_source_list =
      "${webURL}/v1/SavbillSalesCrmsBss/leadSource/list";
  static const lead_feasibility_list =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/feasibility";
  static const leadCustomerGenderType =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/leadcustomergendertype";
  static const leadServiceType =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/servicerType";
  static const getFindServiceAreaByBranchId =
      "${webURL}/v1/SavbillCommonGateway/findServiceAreaByBranchId";

  static const getLeadSourcePartnerList =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/Partner";
  static const getLeadSourceStaffUsersList =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/StaffUser";
  static const getLeadSourceServiceAreaList =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/ServiceArea";
  static const getLeadSourceBranchList =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/Branch";
  static const getLeadSourceCustomerList =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/findAll/Customers";
  static const saveLead = "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/save";
  static const updateLead =
      "${webURL}/v1/SavbillSalesCrmsBss/leadMaster/update";
  static const checkExistingLeadByName =
      "${webURL}/v1/cpm/customers/getActiveCustomersList/username";

  // lead follow up
  static const leadFollowUpAllList =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/all";
  static const generateNameFollowUP =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/generateNameOfTheFollowUp";
  static const leadFollowUpSave =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/save";
  static const leadReScheduleFollowUpRemarks =
      "${webURL}/v1/cpm/findAll/reScheduleFollowUpRemarks";
  static const leadReSchedulefollowUp =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/reSchedulefollowup";
  static const leadCloseFollowUp =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/closefollowup";
  static const leadFollowUpRemark =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/findAll/followUpRemark";
  static const leadFollowUpSaveRemark =
      "${webURL}/v1/SavbillSalesCrmsBss/followUp/save/leadFollowUpRemark";
  static const reScheduleTicketFollowUp =
      "${webURL}/v1/TicketManagement/ticketFollowUp/reScheduleTicketfollowup";
  static const cpmLeadDocList = "${webURL}/v1/SavbillSalesCrmsBss/leadDoc/all";
  static const cpmLeadDocStatus =
      "${webURL}/v1/cpm/commonList/generic/docStatus";
  static const cpmLeadDocSave = "${webURL}/v1/SavbillSalesCrmsBss/leadDoc/save";
  static const cpmLeadDocDelete =
      "${webURL}/v1/SavbillSalesCrmsBss/leadDoc/delete";
  static const cpmLeadUploadDocOnline =
      "${webURL}/v1/SavbillSalesCrmsBss/leadDoc/uploadDocOnline";

  //Acl
  static const getAclEntry =
      "${webURL}/v1/SavbillCommonGateway/acl/getAclEntry";

  // change plan
  static const plan_group_by_filters = "${webURL}/v1/cpm/getPlanGroupByFilters";
  static const change_plan_date =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/changePlanDate";
  static const deActivePlanBulk =
      "${webURL}/v1/cpm/subscriber/deactivatePlanInBulk";

  //Child Customer
  static const get_all_child_customer = "${webURL}/v1/cpm/getAllChildCustomer";

  static const changeplan_add_on_plan =
      "${webURL}/v1/cpm/subscriber/changePlan01";

  // static const changeplan_add_on_plan = "${webURL}/v1/Revenue/getOfferPriceWithTax/plan?planIds=";

  // task managment

  static const view_tat_task_list = "${webURL}/v1/TaskManagement/tasktatmatrix";
  static const view_tat_task_list_searchAll =
      "${webURL}/v1/TaskManagement/tasktatmatrix/searchAll";

  static const delete_tat_task =
      "${webURL}/v1/TaskManagement/tasktatmatrix/delete";
  static const add_tat_task = "${webURL}/v1/TaskManagement/tasktatmatrix/save";
  static const edit_tat_task =
      "${webURL}/v1/TaskManagement/tasktatmatrix/update";

  static const task_case_category_by_id =
      "${webURL}/v1/TaskManagement/CaseCategory";

  static const task_all_active_reason_category =
      "${webURL}/v1/TaskManagement/CaseCategory/getAllActiveReasonCatgory";
  static const task_case_category = "${webURL}/v1/TaskManagement/CaseCategory";
  static const task_add_case_category_save =
      "${webURL}/v1/TaskManagement/CaseCategory/save";
  static const task_add_case_category_update =
      "${webURL}/v1/TaskManagement/CaseCategory/update";

  static const task_case_category_delete =
      "${webURL}/v1/TaskManagement/CaseCategory/delete";
  static const task_case_category_searchAll =
      "${webURL}/v1/TaskManagement/CaseCategory/searchAll";

  static const task_case_condition =
      "${webURL}/v1/SavbillCommonGateway/commonList/generic/CASE_CONDITION?from_cache=true";

  static const task_search_status =
      "${webURL}/v1/TaskManagement/tasktatmatrix/searchByStatus";
  static const task_case = "${webURL}/v1/TaskManagement/case";
  static const task_case_category_all =
      "${webURL}/v1/TaskManagement/CaseCategory/all";
  static const view_task_subCategory_list =
      "${webURL}/v1/TaskManagement/CaseSubCategory/getAll";
  static const search_task_subCategory_list =
      "${webURL}/v1/TaskManagement/CaseSubCategory/searchAll";
  static const task_case_sub_category =
      "${webURL}/v1/TaskManagement/CaseSubCategory";
  static const view_task = "${webURL}/v1/TaskManagement/case";
  static const getTaskCaseStatus =
      "${webURL}/v1/SavbillCommonGateway/commonList/taskStatus";
  static const get_task_followup_detail =
      "${webURL}/v1/TaskManagement/ticketFollowupDetails/getAllByCaseId";
  static const getTaskTATAuditDetail =
      "${webURL}/v1/TaskManagement/case/getTatAuditDetails";
  static const showTaskTatDetails =
      "${webURL}/v1/TaskManagement/case/getTatDetials";
  static const getTaskETRReport =
      "${webURL}/v1/TaskManagement/case/getTicketETRReport";
  static const task_workflow_detail =
      "${webURL}/v1/TaskManagement/workflowaudit/";
  static const getAllTaskTeamNameByStaffId =
      "${webURL}/v1/TaskManagement/ticketFollowupDetails/getAllTeamNameByStaffId";
  static const assign_task_staff_list =
      "${webURL}/v1/TaskManagement/case/reassignTicket?caseId=";
  static const getByTeamId =
      "${webURL}/v1/SavbillCommonGateway/staffuser/getByTeamId";
  static const task_case_assign_update =
      "${webURL}/v1/TaskManagement/case/updateDetails";
  static const getAllStaff =
      "${webURL}/v1/SavbillCommonGateway/staffuser/ActivestaffWithoutPaggination";
  static const viewTaskResolutionReasonSubCategory =
      "${webURL}/v1/TaskManagement/resolutionReasons/searchBySubCategory";
  static const task_upload_document =
      "${webURL}/v1/TaskManagement/case/updateDocumentDetails?caseId=";
  static const task_etr_customer =
      "${webURL}/v1/TaskManagement/case/sendETRtoCustomer";
  static const task_follow_up_save =
      "${webURL}/v1/TaskManagement/ticketFollowupDetails/save";

  static const task_all_sub_category =
      "${webURL}/v1/TaskManagement/CaseSubCategory/getAllSubCaseCategoryListByCategoryId";

  static const link_task = "${webURL}/v1/TaskManagement/case/linkBulkTicket";
  static const task_documentList =
      "${webURL}/v1/TaskManagement/case/documentList";
  static const task_document_download =
      "${webURL}/v1/TaskManagement/case/document/download";
  static const create_task = "${webURL}/v1/TaskManagement/case/save";

  static const active_customer_list =
      "${webURL}/v1/cpm/getActivecustomers/list";
  static const customer_both_search = "${webURL}/v1/cpm/customers/search/Both";

  static const staff_user_search =
      "${webURL}/v1/SavbillCommonGateway/staffuser/search";

  /// paymentGateway

  static const payment_config =
      "${webURL}/v1/SavbillCommonGateway/paymentconfig/getActivePaymentConfig";

  //MoMo Pay
  static const momoPayRequest =
      "${webURL}/v1/SavbillIntegrationSystem/requestToPay";

  //Selcom Pay
  static const selcomPayRequest =
      "${webURL}/v1/SavbillIntegrationSystem/selcomPay";

  //Airtel Pay
  static const airtelPayRequest =
      "${webURL}/v1/SavbillIntegrationSystem/airtel/requestToPay";

  static const paymentStatus =
      "${webURL}/v1/SavbillIntegrationSystem/getpaymentstatus";
  static const customerInvoicePaymentLink =
      "${webURL}/v1/cpm/generatePaymentLink";
  static const customerGeneratePaymentLinkForRenew =
      "${webURL}/v1/cpm/generatePaymentLinkForRenew";

  static const add_notes = "${webURL}/v1/cpm/add/notes";
  static const deactivatePlan = "${webURL}/v1/cpm/subscriber/deactivatePlan";

  static const get_cust_caf_notes =
      "${webURL}/v1/cpm/findAllCustomerNotesWithPagination";
   static const get_cust_all_notes =
       "${webURL}/v1/cpm/findAllCustomerNotes";
   static const get_staff_user =
      "${webURL}/v1/SavbillCommonGateway/getStaffUser";

  ///
  static const customer_drop_down_list =
      "${webURL}/v1/SavbillCommonGateway/staffList/dropdown/all";

  static const getBuildingAndSubareaNamesDetails =
      "${webURL}/v1/cpm/BuildingAndSubareaNames";

  static const getRefreshToken = "${webURL}/v1/SavbillCommonGateway/refreshtoken";
  static const void_Plan = "${webURL}/v1/cpm/subscriber/voidPlan";
  static const caf_feasibility = "${webURL}/v1/SavbillCommonGateway/commonList/feasibility";
  static const changePlan_BillingCycle = "${webURL}/v1/SavbillCommonGateway/commonList/generic/changePlanBillingCycle";
  static const changePlan_Type = "${webURL}/v1/SavbillCommonGateway/commonList/generic/changePlanType";
  static const city_list =
       "${webURL}/v1/SavbillCommonGateway/city/list";
}
