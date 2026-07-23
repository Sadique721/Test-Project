package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class SavePincodeSharedDataMessage {

    private Long id;

    private String pincode;
    @JsonCreator
    public SavePincodeSharedDataMessage(String pincode) {
        this.pincode = pincode;
    }
    private String status;

    private Boolean isDeleted ;


    private Integer countryId;


    private Integer cityId;


    private Integer stateId;


    //private List<Area> areaList = new ArrayList<>();


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private  String createdByName;
    private  String lastModifiedByName;


}
