package com.savbill.commonGateway.constants;

public class MenuConstants {
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
        String SUB_BUSINESS_VERTICALS_CREATE = "sub_business_verticals_create";
        String SUB_BUSINESS_VERTICALS_EDIT = "sub_business_verticals_edit";
        String SUB_BUSINESS_VERTICALS_DELETE = "sub_business_verticals_delete";
        String SUB_BUSINESS_VERTICALS = "sub_business_vertical";
        String DEPARTMENT = "department";
        String DEPARTMENT_CREATE = "department_create";
        String DEPARTMENT_EDIT = "department_edit";
        String DEPARTMENT_DELETE = "department_delete";
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

        String STAFFUSER="staff";
        String STAFFUSER_CREATE="staff_create";
        String STAFFUSER_EDIT="staff_edit";
        String STAFFUSER_DELETE="staff_delete";

        String SYSTEM_CONFIG="system_configuration";
        String SYSTEM_CONFIG_CREATE="system_configuration_create";
        String SYSTEM_CONFIG_EDIT="system_configuration_edit";
        String SYSTEM_CONFIG_DELETE="system_configuration_delete";

    }
    public interface IwfSettings{
        String ROLE_MANAGEMENT="iwf_role";
        String ROLE_CREATE="iwf_role_create";
        String ROLE_EDIT="iwf_role_edit";
        String ROLE_DELETE="iwf_role_delete";
        String STAFFUSER="iwf_staff";
        String STAFFUSER_CREATE="iwf_staff_create";
        String STAFFUSER_EDIT="iwf_staff_edit";
        String STAFFUSER_DELETE="iwf_staff_delete";

    }

    public interface teams
    {
        // Constants for teams operations
        String TEAMS = "teams";
        String TEAMS_CREATE = "teams_create";
        String TEAMS_EDIT = "teams_edit";
        String TEAMS_DELETE = "teams_delete";

    }

    public interface IwfTeams
    {
        // Constants for teams operations
        String TEAMS = "iwf_teams_management";
        String TEAMS_CREATE = "iwf_teams_management_create";
        String TEAMS_EDIT = "iwf_teams_management_edit";
        String TEAMS_DELETE = "iwf_teams_management_delete";

    }

    public static final String AUDIT_LOG = "audit_log";

}
