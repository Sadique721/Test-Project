package com.savbill.inventorymanagement.core.constants;

public class ACLMenuConstants {
    public interface Dashboards{
        public static final String DASHBOARD = "dashboard";
        public static final String DASHBOARD_APPROVAL = "dashboard_approval";
        public static final String DASHBOARD_INVENTORY = "dashboard_inventory";
        public static final String DASHBOARD_SALES_CRM = "dashboard_sales_crm";
    }
    public interface Masters {
        String MASTER = "master";
        String COUNTRY = "country";
        String COUNTRY_CREATE = "country_create";
        String COUNTRY_EDIT = "country_edit";
        String COUNTRY_DELETE = "country_delete";
        String STATE = "state";
        String STATE_CREATE = "state_create";
        String STATE_EDIT = "state_edit";
        String STATE_DELETE = "state_delete";
        String CITY = "city";
        String CITY_CREATE = "city_create";
        String CITY_EDIT = "city_edit";
        String CITY_DELETE = "city_delete";
        String PINCODE = "pincode";
        String PINCODE_CREATE = "pincode_create";
        String PINCODE_EDIT = "pincode_edit";
        String PINCODE_DELETE = "pincode_delete";
        String AREA = "area";
        String AREA_CREATE = "area_create";
        String AREA_EDIT = "area_edit";
        String AREA_DELETE = "area_delete";
        String SERVICE_AREA = "service_area";
        String SERVICE_AREA_CREATE = "service_area_create";
        String SERVICE_AREA_EDIT = "service_area_edit";
        String SERVICE_AREA_DELETE = "service_area_delete";
        String INVESTMENT_CODE = "investment_code";
        String INVESTMENT_CODE_CREATE = "investment_code_create";
        String INVESTMENT_CODE_EDIT = "investment_code_edit";
        String INVESTMENT_CODE_DELETE = "investment_code_delete";
        String BUSINESS_UNIT = "business_unit";
        String BUSINESS_UNIT_CREATE = "business_unit_create";
        String BUSINESS_UNIT_EDIT = "business_unit_edit";
        String BUSINESS_UNIT_DELETE = "business_unit_delete";
        String SUB_BUSINESS_UNIT = "sub_business_unit";
        String SUB_BUSINESS_UNIT_CREATE = "sub_business_unit_create";
        String SUB_BUSINESS_UNIT_EDIT = "sub_business_unit_edit";
        String SUB_BUSINESS_UNIT_DELETE = "sub_business_unit_delete";
        String BANK = "bank";
        String BANK_CREATE = "bank_create";
        String BANK_EDIT = "bank_edit";
        String BANK_DELETE = "bank_delete";
        String BRANCH = "branch";
        String BRANCH_CREATE = "branch_create";
        String BRANCH_EDIT = "branch_edit";
        String BRANCH_DELETE = "branch_delete";
        String REGION = "region";
        String REGION_CREATE = "region_create";
        String REGION_EDIT = "region_edit";
        String REGION_DELETE = "region_delete";
        String BUSINESS_VERTICALS = "business_verticals";
        String BUSINESS_VERTICALS_CREATE = "business_verticals_create";
        String BUSINESS_VERTICALS_EDIT = "business_verticals_edit";
        String BUSINESS_VERTICALS_DELETE = "business_verticals_delete";
        String SUB_BUSINESS_VERTICAL = "sub_business_vertical";
        String SUB_BUSINESS_VERTICALS_CREATE = "sub_business_verticals_create";
        String SUB_BUSINESS_VERTICALS_EDIT = "sub_business_verticals_edit";
        String SUB_BUSINESS_VERTICALS_DELETE = "sub_business_verticals_delete";
        String DEPARTMENT = "department";
        String DEPARTMENT_CREATE = "department_create";
        String DEPARTMENT_EDIT = "department_edit";
        String DEPARTMENT_DELETE = "department_delete";
    }
    public interface Products {
        String PRODUCT = "product";
        String SERVICE = "service";
        String SERVICE_CREATE = "service_create";
        String SERVICE_EDIT = "service_edit";
        String SERVICE_DELETE = "service_delete";
        String TAX = "tax";
        String TAX_CREATE = "tax_create";
        String TAX_EDIT = "tax_edit";
        String TAX_DELETE = "tax_delete";
        String CHARGE = "charge";
        String CHARGE_CREATE = "charge_create";
        String CHARGE_EDIT = "charge_edit";
        String CHARGE_DELETE = "charge_delete";
        String QOS_POLICY = "qos_policy";
        String QOS_POLICY_CREATE = "qos_policy_create";
        String QOS_POLICY_EDIT = "qos_policy_edit";
        String QOS_POLICY_DELETE = "qos_policy_delete";
        String TIME_POLICY = "time_policy";
        String TIME_POLICY_CREATE = "time_policy_create";
        String TIME_POLICY_EDIT = "time_policy_edit";
        String TIME_POLICY_DELETE = "time_policy_delete";
        String PLAN = "plan";
        String PLAN_CREATE = "plan_create";
        String PLAN_EDIT = "plan_edit";
        String PLAN_DELETE = "plan_delete";
        String PLAN_CHANGE_STATUS = "plan_change_status";
        String DISCOUNT = "discount";
        String DISCOUNT_CREATE = "discount_create";
        String DISCOUNT_EDIT = "discount_edit";
        String DISCOUNT_DELETE = "discount_delete";
        String PLAN_GROUP = "plan_group";
        String PLAN_GROUP_CREATE = "plan_group_create";
        String PLAN_GROUP_EDIT = "plan_group_edit";
        String PLAN_GROUP_DELETE = "plan_group_delete";
        String SPECIAL_PLAN_MAPPING = "special_plan_mapping";
        String SPECIAL_PLAN_MAPPING_CREATE = "special_plan_mapping_create";
        String SPECIAL_PLAN_MAPPING_EDIT = "special_plan_mapping_edit";
        String SPECIAL_PLAN_MAPPING_DELETE = "special_plan_mapping_delete";
        String VOUCHER_MANAGEMENT = "voucher_management";
        String VOUCHER_CREATE = "voucher_create";
        String SHOW_VOUCHER_PROFILE = "show_voucher_profile";
        String VOUCHER_EDIT = "voucher_edit";
        String VOUCHER_DELETE = "voucher_delete";
        String VOUCHER_GENERATE = "voucher_generate";
        String SHOW_VOUCHER_BATCH = "show_voucher_batch";
        String EXTEND_EXPIRY_VOUCHER_BATCH = "extend_expiry_voucher_batch";
        String SHOW_MANAGE_VOUCHERS = "show_manage_vouchers";
        String VOUCHER_ACTIVE = "voucher_active";
        String VOUCHER_BLOCK = "voucher_block";
        String VOUCHER_UNBLOCK = "voucher_unblock";
        String VOUCHER_SCRAP = "voucher_scrap";
        String SEND_SMS_MANAGE_VOUCHERS = "send_sms_manage_vouchers";
        String DOWNLOAD_VOUCHER = "download_voucher";
    }
    public interface Partners {
        String PARTNER = "partner";
        String PARTNER_PLAN_BUNDLE = "partner_plan_bundle";
        String PARTNER_BUNDLE_CREATE = "partner_bundle_create";
        String PARTNER_BUNDLE_EDIT = "partner_bundle_edit";
        String PARTNER_BUNDLE_DELETE = "partner_bundle_delete";
        String PARTNER_LIST = "partner_list";
        String PARTNER_CREATE = "partner_create";
        String PARTNER_EDIT = "partner_edit";
        String PARTNER_DELETE = "partner_delete";
        String PARTNER_MANAGE_BALANCE = "partner_manage_balance";
        String PARTNER_UPLOAD_DELETE = "partner_upload_delete";
        String PARTNER_SHIFT_PARTNER = "partner_shift_partner";
        String MANAGE_BALANCE = "manage_balance";
        String MANAGE_BALANCE_CREATE = "manage_balance_create";
    }
    public interface Settings {
        String ROLE_MANAGEMENT="role";
        String ROLE_CREATE="role_create";
        String ROLE_EDIT="role_edit";
        String ROLE_DELETE="role_delete";
        String ROLE_LIST = "role_list";
        String STAFFUSER="staffuser";
        String STAFFUSER_CREATE="staffuser_create";
        String STAFFUSER_EDIT="staffuser_edit";
        String STAFFUSER_DELETE="staffuser_delete";

        String SYSTEM_CONFIG="system_config";
        String SYSTEM_CONFIG_CREATE="system_config_create";
        String SYSTEM_CONFIG_EDIT="system_config_edit";
        String SYSTEM_CONFIG_DELETE="system_config_delete";
    }
    /**
     * Inventory Management
     */
    public interface Manufacturer {
        String MANUFACTURER = "manufacturer";
        String MANUFACTURER_CREATE = "manufacturer_create";
        String MANUFACTURER_EDIT = "manufacturer_edit";
        String MANUFACTURER_DELETE = "manufacturer_delete";
    }

    public interface Product_Category {
        String PRODUCT_CATEGORY = "product_category";
        String PRODUCT_CATEGORY_CREATE = "product_category_create";
        String PRODUCT_CATEGORY_EDIT = "product_category_edit";
        String PRODUCT_CATEGORY_DELETE = "product_category_delete";
    }

    public interface Product {
        String PRODUCT = "inven_product";
        String PRODUCT_CREATE = "inven_product_create";
        String PRODUCT_EDIT = "inven_product_edit";
        String PRODUCT_DELETE = "inven_product_delete";
    }

    public interface Pop {

        String POP = "pop";
        String POP_CREATE = "pop_create";
        String POP_EDIT = "pop_edit";
        String POP_DELETE = "pop_delete";
        String POP_INVENTORY_LIST = "pop_inven_list";
        String POP_INVENTORY_LIST_ASSIGN_INVENTORY = "pop_inven_list_assign_inventory";
        String POP_INVENTORY_LIST_EDIT = "inven_list_edit";
        String POP_INVENTORY_LIST_DELETE = "inven_list_delete";
        String POP_INVENTORY_LIST_APPROVE = "inven_list_approve";
        String POP_INVENTORY_LIST_REJECT = "inven_list_reject";
        String POP_INVENTORY_APPROVAL_PROGRESS = "inven_list_progress";
    }

    public interface Warehouse {
        String WAREHOUSE = "warehouse";
        String WAREHOUSE_CREATE = "warehouse_create";
        String WAREHOUSE_EDIT = "warehouse_edit";
        String WAREHOUSE_DELETE = "warehouse_delete";
    }

    public interface Inward {
        String INWARD = "inven_inwards";
        String INWARD_CREATE = "inven_inwards_create";
        String INWARD_EDIT = "inven_inwards_edit";
        String INWARD_DELETE = "inven_inwards_delete";
        String INWARD_SHOW_MAC = "inven_inwards_show_mac";
        String INWARD_APPROVAL = "inven_inwards_approve";
        String INWARD_REJECT = "inward_reject";
    }

    public interface Outward {
        String OUTWARD = "inven_outwards";
        String OUTWARD_CREATE = "inven_outwards_create";
        String OUTWARD_EDIT = "inven_outwards_edit";
        String OUTWARD_DELETE = "inven_outwards_delete";
        String OUTWARD_SHOW_MAC = "inven_outwards_show_mac";
        String OUTWARD_ADD_MAC = "inven_outwards_add_mac";
    }

    public interface External_Item {
        String EXTERNAL_ITEM = "ext_item";
        String EXTERNAL_ITEM_CREATE = "ext_item_create";
        String EXTERNAL_ITEM_EDIT = "ext_item_edit";
        String EXTERNAL_ITEM_DELETE = "ext_item_delete";
        String EXTERNAL_ITEM_ADD_MAC_ADDRESS = "ext_item_add_mac_address";
        String EXTERNAL_ITEM_SHOW_MAC_ADDRESS = "ext_item_show_mac_address";
        String EXTERNAL_ITEM_APPROVE = "ext_item_approve";
        String EXTERNAL_ITEM_REJECT = "ext_item_reject";
    }

    public interface Bulk_Consumption {
        String BULK_CONSUMPTION = "bulk_consumption";
        String BULK_CONSUMPTION_CREATE = "create_bulk_consumption";
        String BULK_CONSUMPTION_EDIT = "edit_bulk_consumption";
        String BULK_CONSUMPTION_DELETE = "delete_bulk_consumption";
        String BULK_CONSUMPTION_VIEW_MAC = "view_inward_mac_mapping";
        String BULK_CONSUMPTION_APPROVE = "bulk_consumption_approve";
        String BULK_CONSUMPTION_REJECT = "bulk_consumption_reject";
    }

    public interface Inventory_Request {
        String INVENTORY_REQUEST = "inven_request";
        String INVENTORY_RAISED_REQUEST = "raised_inven_request";
        String INVENTORY_REQUEST_DELETE = "inven_request_delete";
        String INVENTORY_ASSIGNED_REQUEST = "assigned_inven_request";
        String INVENTORY_ASSIGNED_REQUEST_FORWARD = "assigned_inven_request_forward";
        String INVENTORY_ASSIGNED_REQUEST_FULLFILLMENT = "assigned_inven_request_fullfillment";
        String INVENTORY_ASSIGNED_REQUEST_APPROVE = "assigned_inven_request_approve";
        String INVENTORY_ASSIGNED_REQUEST_REJECT = "assigned_inven_request_reject";
    }

    public interface Inventory_Details {
        String INVENTORY_DETAILS = "inven_details";
        String INVENTORY_DETAILS_CHANGE_TYPE = "inven_details_change_type";
        String INVENTORY_DETAILS_WARRANTY = "inven_details_warranty";
        String INVENTORY_DETAILS_STATUS = "inven_details_status";
        String INVENTORY_DETAILS_OWNERSHIP_STATUS = "inven_details_ownership_status";
        String INVENTORY_DETAILS_ASSIGNED_INVENTORY = "inven_details_assigned_inventory";
        String INVENTORY_DETAILS_ASSIGNED_TO_CUSTOMER = "inven_details_inven_assigned_to_cust";
        String INVENTORY_DETAILS_ASSIGNED_TO_POP = "inven_details_inven_assigned_to_pop";
        String INVENTORY_DETAILS_ASSIGNED_TO_SERVICE_AREA = "inven_details_inven_assigned_to_sa";
        String INVENTORY_DETAILS_ASSIGNED_SERIALIZED = "inven_details_inven_assigned_serialized";
        String INVENTORY_DETAILS_ASSIGNED_NON_SERIALIZED = "inven_details_inven_assigned_nonserialized";
    }

    /**
     * Network Management
     */
    public interface Network_Device {
        String NETWORK_DEVICE = "network_device";
        String NETWORK_DEVICE_CREATE = "network_device_create";
        String NETWORK_DEVICE_EDIT = "network_device_edit";
        String NETWORK_DEVICE_DELETE = "network_device_delete";
        String NETWORK_DEVICE_PARENT_MAPPING = "network_device_parent_mapping";
    }
    public interface Network_Map {
        String NETWORK_MAP = "network_map";
    }
    public interface Ip_Address {
        String IP = "ip";
        String IP_CREATE = "ip_create";
        String IP_EDIT = "ip_edit";
        String IP_DELETE = "ip_delete";
    }

    public interface Service_Area_Inventory {
        String SERVICE_AREA_INVENTORY_LIST = "sa_inventory_list";
        String SERVICE_AREA_INVENTORY_LIST_EDIT = "sa_inventory_edit";
        String SERVICE_AREA_INVENTORY_LIST_APPROVE = "sa_inventory_approve";
        String SERVICE_AREA_INVENTORY_LIST_REJECT = "sa_inventory_reject";
        String SERVICE_AREA_INVENTORY_LIST_APPROVAL_PROGRESS = "sa_inventory_progress";
        String SERVICE_AREA_INVENTORY_LIST_ASSIGN_INVENTORY = "sa_inventory_assign";
        String SERVICE_AREA_INVENTORY_LIST_DELETE = "sa_inventory_delete";
    }

    public interface INVENTORY_PRE_CUSTOMER_ASSIGN {
        String PRE_CUST_INVENTORY = "pre_cust_inventory";
        String PRE_CUST_INVENTORY_PLAN = "pre_cust_inven_plan";
        String PRE_CUST_INVENTORY_OTHER = "pre_cust_inven_other";
        String PRE_CUST_INVENTORY_EXTERNAL = "pre_cust_inven_external";
        String PRE_CUST_INVENTORY_HISTORY = "pre_cust_inven_history";
        String PRE_CUST_INVENTORY_SWAP = "pre_cust_inven_swap";
        String PRE_CUST_INVENTORY_REMOVE = "pre_cust_inven_remove";
        String PRE_CUST_INVENTORY_EDIT = "pre_cust_inven_edit";
        String PRE_CUST_INVENTORY_REPLACE = "pre_cust_inven_replace";
        String PRE_CUST_INVENTORY_DTV = "pre_cust_inven_dtv";
    }

    public interface INVENTORY_POST_CUSTOMER_ASSIGN {
        String POST_CUST_INVENTORY = "post_cust_inventory";
        String POST_CUST_INVENTORY_PLAN = "post_cust_inven_plan";
        String POST_CUST_INVENTORY_OTHER = "post_cust_inven_other";
        String POST_CUST_INVENTORY_EXTERNAL = "post_cust_inven_external";
        String POST_CUST_INVENTORY_HISTORY = "post_cust_inven";
        String POST_CUST_INVENTORY_SWAP = "post_cust_inven_swap";
        String POST_CUST_INVENTORY_REMOVE = "post_cust_inven_remove";
        String POST_CUST_INVENTORY_EDIT = "post_cust_inven_edit";
        String POST_CUST_INVENTORY_REPLACE = "post_cust_inven_replace";
        String POST_CUST_INVENTORY_DTV = "post_cust_inven_dtv";
    }

    public static final String AUDIT_LOG = "audit_log";
}
