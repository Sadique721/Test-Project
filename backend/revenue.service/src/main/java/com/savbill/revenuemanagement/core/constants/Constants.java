package com.savbill.revenuemanagement.core.constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class Constants {

    public static final String PAYMENT_STATUS_APPROVED = "approved";
    public static final String ADVANCE = "advance";

    public static final String STATUS_PENDING = "pending";
    public static final String TRANS_CATEGORY_CUST_CREATE = "Balance";
    public static final String TRANS_CATEGORY_PAYMENT = "PAYMENT";

    public static final String ADDR_TYPE_PRESENT = "Present";
    public static final String PAYMENT_MODE_TYPE_CASH = "Cash";
    public static final String PAYMENT_TYPE = "Payment";
    public static final String CANCEL = "Cancel";
    public static final String TRANS_CATEGORY_REVERT_COMMISSION = "Revert Commision";

    public static final String TRANS_CATEGORY_ADD_BALANCE = "Balance";

    public static final String PAYMENT_STATUS_PENDDING = "pending";

    public static final String CUSTOMER_STATUS_REJECTED = "Rejected";

    public static Integer DEFAULT_PARTNER_ID = 1;

    public static final String COMMISSION_ON_PLAN = "Plan level";

    public static final String TRANS_TYPE_CREDIT = "CR";

    public static final String TRANS_TYPE_DEBIT = "DR";

    public static final String TRANS_CATEGORY_COMMISSION = "Commision";


    public static String CHARGE_TYPE_RECURRING = "RECURRING";
    public static String CHARGE_TYPE_ONE_TIME = "NON_RECURRING";
    public static String CHARGE_TYPE_ADVANCE = "ADVANCE";
    public static final String SUBISU = "SUBISU";
    public static String CHARGE_TYPE_ADVANCE_RECURRING = "ADVANCE_RECURRING";
    public static String CHARGE_TYPE_REFUNDABLE = "REFUNDABLE";
    public static String CHARGE_TYPE_CUSTOMER_DIRECT = "CUSTOMER_DIRECT";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

    public static final String AVOID_SAVE_MULTIPLE_BU = "You are not allowed to perform this action, Please contact your system administrator.";

    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    public static final String CUSTOMER_STATUS_NEW_ACTIVATION = "NewActivation";

    public static final String CUSTOMER_STATUS_ACTIVE = "Active";

    public static final String CUSTOMER_STATUS_SUSPEND = "Suspend";

    public static final String CUSTOMER_STATUS_INACTIVE = "Inactive";

    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    public static final SimpleDateFormat INVOICE_DATE_STRING_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static final int INVOICE_DUE_DAYS = 15;
    public static final SimpleDateFormat DATE_STRING_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static final int SUCCESS_SMS = 3;
    public static final int FAIL_SMS = 3;

    public static final String SUCCESS_REPLY = "Success";
    public static final String FAIL_REPLY = "Error";

    public static final String CREDIT_DOC_TYPE_CREDITNOTE = "creditnote";

    public static final String TRANS_CREDIT_NOTE = "CREDITNOTE";

    public static final String transfer = "transfer";

    public static final String STOP_STATUS = "STOP";
    public static final String ACTIVE = "ACTIVE";
    public static final String ALLOWZEROCHARGEINVOICE = "AllowZeroRsInvoice";

    public static final String ORGANIZATION = "ORGANIZATION";

    public static final String AUTO_RENEWAL_PREFERANCE = "Your auto-renewal preference ";

    public static final String AUTO_RENEW_FREE_PLAN = "AutoRenewFreePlan";

    public enum BILL_RUN_STATUS {
        CREATED("Created"), IN_PROGRESS("In Progress"), GENERATED("Generated"), EXPORTED("Exported"), DISPATCHED(
                "Dispatched");

        private String status;

        BILL_RUN_STATUS(String status) {
            this.status = status;
        }

        public String status() {

            return status;
        }

    }

    public enum INVOICE_PAYMENT_STATUS {
        UNPAID("UnPaid");
        private String status;

        INVOICE_PAYMENT_STATUS(String status) {
            this.status = status;
        }

        public String status() {

            return status;
        }
    }

    public enum INVOICE_STATUS {
        GENERATED("Generated"), EXPORTED("Exported"), DISPATCHED("Dispatched"), MAILED("mailed");

        private String status;

        INVOICE_STATUS(String status) {
            this.status = status;
        }

        public String status() {

            return status;
        }

    }

    public interface CUSTOMER_TYPE {
        String POSTPAID = "Postpaid";
        String PREPAID = "Prepaid";
    }

    public interface CUSTOMER_INVOICE_TYPE {
        String GROUP = "Group";
        String INDEPENDENT = "Independent";
    }

    public interface INVOICE_TYPE {
        String CUSTOMER_CHARGE = "Customer_Charge";
        String CREATE_CUSTOMER = "Create_Customer";
        String RENEW = "Renew";
        String ADDON = "addon";
        String CHANGE_PLAN = "Change_Plan";
        String INVENTORY = "Inventory";
        String CANCEL_REGENERATE = "Cancel_Regenerate";

        String ADD_NEW_SERVICE = "addNewService";

        String CREATE_CAF_CUSTOMER = "Create_Caf_Customer";

        String IS_CAF_CUSTOMER = "isCAFCustomer";

    }

    public interface CREDIT_DOC_STATUS {
        String FULLY_ADJUSTED = "Fully Adjusted";
        String PARTIAL_ADJUSTED = "Partialy Adjusted";
        String ADVANCE_PAYMENT = "advance";
        String INVOICE = "invoice";
        String APPROVED = "approved";

        String PENDING = "pending";

        String WITHDRAWAL = "Withdrawal";

        String GENERATED = "Generated";

        String ADJUSTED = "Adjusted";
        String ADJUSTMENT = "ADJUSTMENT";


    }

    public interface DEBIT_DOC_STATUS {
        String FULLY_PAID = "Fully Paid";
        String PARTIALY_PAID = "Partialy Paid";
        String PENDING = "pending";
        String REJCTED = "rejected";

        String APPROVED = "approved";
        String PENDING_SENT = "Pending Sent";
        String PENDING_ACCEPTED = "Pending Accepted";
        String PARTIAL_PENDING = "Partial Pending";

        String CLEAR = "Clear";

        String PAYABLE = "Payable";

        String UNPAID = "Unpaid";

        String CANCELLED = "Cancelled";
        String EXPORTED = "Exported";
        String DISTRIBUTED = "Distributed";

        String VOID = "Void";

    }

    public interface PAYMENT_MODE {
        String CREDIT_NOTE = "Credit Note";
        String CREDIT_NOTE1 = "CreditNote";
        String CASH = "Cash";
        String BUSINESS_PROMOTION = "Buiness Promotion";

    }

    public interface THREAD_STATUS {
        String PENDING = "Pending";
        String IN_PROGRESS = "In-Progress";
        String STARTED = "Started";
        String COMPLETED = "Completed";
        String Failed = "Failed";

    }

    public interface CUSTOMER_LEDGER {
        String TRANS_CATEGORY_ = "AddBalance";
        String TRANS_CATEGORY_CUST_CREATE = "Balance";
        String TRANS_TYPE_CREDIT = "CR";
        String TRANS_TYPE_DEBIT = "DR";
        String TRANS_BUSINESS_PROMOTION = "Business Promotion";
        public static final String TRANS_CATEGORY_PAYMENT = "PAYMENT";
        public static final String TRANS_CATEGORY_REFUND = "REFUND";
        String TRANS_CATEGORY_TRANSFER = "TRANSFER";
        String CHILD_BUY_PLAN="CHILD_WALLET";
    }

    public interface CHARGER_TYPE {

        public static String CHARGE_TYPE_RECURRING = "RECURRING";
        public static String CHARGE_TYPE_ONE_TIME = "NON_RECURRING";
        public static String CHARGE_TYPE_ADVANCE = "ADVANCE";
        public static String CHARGE_TYPE_ADVANCE_RECURRING = "ADVANCE_RECURRING";
        public static String CHARGE_TYPE_REFUNDABLE = "REFUNDABLE";
        public static String CHARGE_TYPE_CUSTOMER_DIRECT = "CUSTOMER_DIRECT";
    }

    public static long getUniqueNumber() {
        AtomicReference<Long> currentTime = new AtomicReference<>(System.currentTimeMillis());
        Long prev;
        Long next = System.currentTimeMillis();
        do {
            prev = currentTime.get();
            next = next > prev ? next : prev + 1;
        } while (!currentTime.compareAndSet(prev, next));
        return next;
    }

    public interface USER_CONSTANTS {
        Integer SUPER_ADMIN_MVNO_ID = 1;
        Integer ADMIN_MVNO_ID = 2;

        Integer DEFAULT_PARTNER_ID = 1;
    }

    public interface PATHS {
        String PDF_READ_PATH = "pdfreadpath";
        String PAYMENT_PDF_READ_PATH = "paymentpdfreadpath";

        String PAYMENT_READ_DOC = "customerinvoicepaymentdocreadpath";
    }

    public interface SCHEDULER_AUDIT {
        public static final String SCHEDULER_STATUS_SUCCESS = "success";
        public static final String SCHEDULER_STATUS_LOCKED = "locked";
        public static final String SCHEDULER_STATUS_FAILURE = "failure";
        public static final String SCHEDULER_GENERATE_INVOICE_NUMBER = "Generate Invoice Number";
        public static final String SCHEDULER_ADD_DAY_WISE_REVENUE = "Add Day Wise Revenue";
        public static final String SCHEDULER_GENERATE_PARTNER_COMMISSION_INVOICE = "Generate Partner Commission Invoice";
        public static final String SCHEDULER_POSTPAID_INVOICE = "Postpaid Invoice Scheduler";
        public static final String SCHEDULER_ADD_MONTH_WISE_REVENUE = "Add Month Wise Revenue";
        public static final String SCHEDULER_Postpaid_Customer_Automate_Payment_Adjustment = "Customer Automate Payment Adjustment";
        public static final String SCHEDULER_PARTNER_COMMISSION = "Partner Commission Scheduler";
        public static final String INVOICE_PDF_GENERATE= "Invoice Pdf Generate";
        public static final String SCHEDULER_KRA_INVOICE_SYNC = "KRA Invoice Sync Scheduler";
        public static final String SCHEDULER_KRA_CREDIT_NOTE_SYNC = "KRA Credit Note Sync Scheduler";

    }

    public interface SCHEDULER_RESPONSE_MESSAGES{
        String SCHEDULER_SAVE = "Scheduler Saved Successfully.";
        String SCHEDULER_UPDATE = "Scheduler Update Successfully.";
        String SCHEDULER_DELETE = " Scheduler Delete Successfully.";
        String SCHEDULER_FETCH = " Scheduler Fetch Successfully.";
        String SUCCESS = "Success";
        String ERROR = "Error";
        String SCHEDULER_OBJECT_IS_EMPTY = "Schedueler Object is Empty";
    }
    public interface SCHEDULERS_NAME{
        String MANUAL_INVOICE_PDF_GENERATE = "Invoice_Export";
        String MANUAL_INVOICE_NOTIFICATION = "Invoice_Distribution";
        String AUTO_INVOICE_PDF = "Auto_Invoice_Export";
        String AUTO_INVOICE_NOTIFICATION = "Auto_Invoice_Distribution";
    }

    public interface  TRANSFER_RESPONSE_MESSAGES{
        String TRANSFER_NULL = "Please send valid transfer request";
    }

    public interface  WALLET_RESPONSE_MESSAGES{
        String WAL = "Please send valid transfer request";
    }

    public interface PURCHASE_TYPE {
        String RENEW = "Renew";
        String CHANGE_PLAN ="Change Plan";
    }
}
