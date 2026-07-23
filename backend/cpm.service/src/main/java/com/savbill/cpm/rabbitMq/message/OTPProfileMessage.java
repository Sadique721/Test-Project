package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.common.OTPManagement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OTPProfileMessage {



//    private Long profileId;
//
//
//    private String profileName;
//
//    private Integer otpLength;
//
//
//    private Long otpValidityInMin;
//
//
//    private String generationType;
//
//
//    private List<FieldType> type;
//
//
//
//    private Integer mvnoId;
//
//    @CreationTimestamp
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
//    @DiffIgnore
//    private LocalDateTime createdate;
//
//    @UpdateTimestamp
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
//    private LocalDateTime updatedate;
//
//
//
//    private Integer createdById;
//
//    private Integer lastModifiedById;
//
//
//    private String staticOtp;
//
//
//    private String mvnoName;

    OTPManagement otpManagement = new OTPManagement();




}
