package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import lombok.Data;

@Data
public class SaveBusinessUnitSharedDataMessage {
    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private String planBindingType;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

    // private List<InvestmentCode> investmentCodeid=new ArrayList<>();

    //@JsonIgnore
    //private Long investmentCodeid ;

}
