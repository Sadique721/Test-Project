package com.savbill.integrationsystem.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerMessage {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private Integer mvnoId;
    private Long buId;
    private Long servicearea;
    private Long branch;
    private String status;
    private String countryCode;
    private Integer parentcustid;



    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;


    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastmodifiedDate;
    private String createbyname;
    private String updatebyname;
    private Integer createdByStaffId;
    private Integer lastModifiedByStaffId;

    private String accountNumber;
    private String customerType;
    private Boolean isorgcust = false;
    private String pan;

    private String olt;
    private String pop;
    private String blockNo;
}
