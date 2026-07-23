package com.savbill.integrationsystem.integrationMenu;

public class ThirdPartyIntigrationConstant {

    public interface IntigrationList {
        public static final String TRA_Integration = "TRA Integration";
        public static final String KRA_Integration = "KRA Integration";
    }

    public interface EventList {
        public static final String INVOICE_INTIGRATION = "Invoice Creation";
    }

    public interface  TRA_Integration{

        public static final String TRA_API = "TRA_API";

        public static final String TRA_AUTH = "TRA_AUTH";


    }

    public interface  KRA_Integration{

        public static final String KRA_API = "KRA_API";

        public static final String KRA_AUTH = "KRA_AUTH";

        public static final String KRA_itemClassifiCode="itemClassifiCode";

        public static final String KRA_chargeItemClassifiCode="Charge_ItemClassifiCode";


    }

}
