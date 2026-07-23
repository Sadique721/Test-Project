package com.savbill.revenuemanagement.utils;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonUtils {

    public static final String PAYMENT_STATUS_PENDING = "pending";

    public static final String INITIAL_PAYMENT_ADJUST = "0";
    public static final String PAYMENT_MODE_CHEQUE = "cheque";
    public static final String ADDR_TYPE_PRESENT = "Present";
    public static final String PAYMENT_STATUS_APPROVED = "approved";
    public static final String PAYMENT_TYPE = "Payment";
    public static final String PAYMENT_MODE_DIRECTDEPOSIT = "directdeposit";
    public static String BILL_PATH = null;
    public static String PARTNER_BILL_PATH = null;

    public static String PARTNER_ROLE_ID = null;
    public static String TRIAL_BILL_PATH = null;

    public static final Integer PAYMENT_STATUS_ADVANCED = 0;

    static Javers javers = JaversBuilder.javers().build();
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


    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void resetCachedObjects() {
//        activeInvoiceServer = null;
        BILL_PATH = null;
        PARTNER_BILL_PATH = null;
        PARTNER_ROLE_ID = null;
        TRIAL_BILL_PATH = null;
    }

    public static String getUpdatedDiff(Object customers22, Object newcust2w) {
        String updated = "";
        try {
            Diff diff = javers.compare(customers22, newcust2w);
            if (diff.hasChanges()) {
                List<Change> changes = diff.getChanges();
                for (Change change : changes) {
                    if (change instanceof ValueChange) {
                        ValueChange valChange = (ValueChange) change;

                        if (!(valChange.getPropertyName().equals("createdOn")
                                || valChange.getPropertyName().equals("lastModifiedOn")
                                || valChange.getPropertyName().equals("createdBy")
                                || valChange.getPropertyName().equals("lastModifiedBy")
                                || valChange.getPropertyName().equals("lastModifiedById")
                                ||valChange.getPropertyName().equals("lastModifiedByName")
                                || valChange.getPropertyName().equals("mvnoId")
                                || valChange.getPropertyName().equals("createdByName")
                                ||valChange.getPropertyName().equals("createdate")
                                ||valChange.getPropertyName().equals("isDelete")
                                || valChange.getPropertyName().equals("updatedate")
                                || valChange.getPropertyName().equals("createdById")

                        )) {
                            if((valChange.getLeft() != valChange.getRight()) && (valChange.getLeft() != null && valChange.getRight() != null)
                                    && (valChange.getLeft() != valChange.getRight())) {
                                updated = updated + "property: " + valChange.getPropertyName() + " from "
                                        + valChange.getLeft() + " to " + valChange.getRight() + " ,";
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            return null;
        }
        System.out.println("Javers changes updated >>>>>>>>>>>>>>>>>> "+updated.trim());
        return updated.trim();
    }

    public static final List<String> POSSIBLE_IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR",
            "True-Client-IP",
            "CF-Connecting-IP",
            "X-Real-IP",
            "Forwarded",
            "Fastly-Client-IP",
            "X-Original-Forwarded-For"
    );

    public static String getIpAddressFromHeader(HttpServletRequest request) {
        for (String ipHeader : POSSIBLE_IP_HEADERS) {
            String headerValue = Collections.list(request.getHeaders(ipHeader)).stream()
                    .filter(StringUtils::hasLength)
                    .findFirst()
                    .orElse(null);

            if (headerValue != null && !"0:0:0:0:0:0:0:1".equals(headerValue)) {
                return headerValue;
            }
        }
        return request.getRemoteAddr();
    }

    public static Long getDaysForExpiryDateByMonth(Double double1, LocalDate date) {
        Long totalDays = 0l;
        for (int i = 0; i < double1; i++) {
            totalDays = totalDays + date.plusMonths(i).lengthOfMonth();
        }
        return totalDays;
    }

    public static Long getDaysForExpiryDateByYear(Double double1, LocalDate date) {
        Long totalDays = 0l;
        for (int i = 1; i <= double1; i++) {
            totalDays = totalDays + date.plusYears(i).lengthOfYear();
            if(date.isLeapYear()) { //check for leap year
                totalDays = totalDays + 1;
            }
        }
        return totalDays;
    }

    public interface CHANGEPLANBILLINGCYCLECONSTANT {
        public static final String New_Billing_Cycle = "New billing cycle";
        public static final String Existing_Billing_Cycle = "Existing billing cycle";
    }
}
