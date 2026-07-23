package com.savbill.integrationsystem.AirtelIntigration;

import java.util.HashMap;
import java.util.Map;

public class AirtelApiConstant {

    public static final Map<String, String> STATUS_ABBREVIATIONS = new HashMap<>();

    public interface API_CALLS {

         String GET_TOKEN = "/auth/oauth2/token";

        String CREATE_PAYMENT = "/merchant/v2/payments/";

        String CHECK_PAYMENT_STATUS = "/standard/v1/payments/";
    }

    public interface  STATUS_CODE{
        String Ambiguous =  "DP00800001000";
        String Success = "DP00800001001";
        String Incorrect_Pin = "DP00800001002";
        String Exceeds_withdrawal_amount_limit = "DP00800001003";

        String Invalid_Amount = "DP00800001004";
        String Transaction_ID_is_invalid = "DP00800001005";

        String In_process = "DP00800001006";
        String Not_enough_balance = "DP00800001007";

        String Refused = "DP00800001008";

        String Transaction_not_permitted_to_Payee = "DP00800001010";

        String Transaction_Timed_Out = "DP00800001024";

        String Transaction_Not_Found = "DP00800001025";

        String Forbidden = "DP00800001026";

        String Transaction_Expired = "DP00800001029";
    }


    static {
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Ambiguous, "A");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Success, "S");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Incorrect_Pin, "IP");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Exceeds_withdrawal_amount_limit, "EWAL");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Invalid_Amount, "IA");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Transaction_ID_is_invalid, "TII");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.In_process, "TIP");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Not_enough_balance, "NEB");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Refused, "R");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Transaction_not_permitted_to_Payee, "TNPP");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Transaction_Timed_Out, "TTO");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Transaction_Not_Found, "TNF");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Forbidden, "F");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.Transaction_Expired, "TE");
    }

    public static String getStatusAbbreviation(String code) {
        return STATUS_ABBREVIATIONS.getOrDefault(code, "UNKNOWN");
    }
}

