package com.savbill.cpm.modules.ChangePlanDTOs;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChangePlanMessageList {
    List<ChangePlanMessage> changePlanMessageList = new ArrayList<>();
}
