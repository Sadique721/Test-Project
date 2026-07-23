package com.savbill.radius.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VlanSearch
{
    private String vlanName;
    private String nasIdentifier;
}
