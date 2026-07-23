package com.savbill.radius.kafka;


import lombok.Data;

@Data
public class UpdateMvnoData {
    private Integer oldmvnoId;
    private Integer newmvnoId;
}
