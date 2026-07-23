package com.savbill.cpm.soap.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RemoveAccountRequest {
    private String actionItem;
    private String requestId;
    private String userName;
}
