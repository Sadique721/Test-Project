package com.diameter.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceActivationMessage {
    private Integer id;
    private Integer custId;
    private String status;
}