package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;


import lombok.Data;

@Data
public class UpdateBusinessUnitSharedDataMessage {
    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private String planBindingType;
    private Boolean isDeleted ;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

    //@JsonIgnore
    //private Long investmentCodeid;

    //private List<InvestmentCode> investmentCodeid=new ArrayList<>();
}
