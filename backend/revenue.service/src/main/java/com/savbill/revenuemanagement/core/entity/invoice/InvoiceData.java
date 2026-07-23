package com.savbill.revenuemanagement.core.entity.invoice;

import java.util.HashSet;

public class InvoiceData {
    private Invoice invoice;
//    private List<DbrPojo> dbrList;
//    private Subscriber parentCustomerInfo;
//    private ArrayList<SubscriberAddress> parentSubscriberAddresses;

    private Long partnerLedgerId;

    Integer loggedInUserId;
    HashSet<Integer> oldDebitDocumentId;
    private String createdByName;
    private String updateByName;

    String creditDocumentId;

    String isFromFlutterWave;

    String paymentOwner;
    Integer inventoryMappingId;

}
