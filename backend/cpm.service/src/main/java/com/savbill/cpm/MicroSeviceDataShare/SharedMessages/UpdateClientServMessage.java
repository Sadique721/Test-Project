package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientServMessage {

    Integer id;

    String name;

    String value;

    Integer mvnoId;
}
