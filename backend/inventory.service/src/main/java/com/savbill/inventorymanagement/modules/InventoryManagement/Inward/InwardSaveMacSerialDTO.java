package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import lombok.Data;

import java.util.List;

@Data
public class InwardSaveMacSerialDTO {
    private Long inwardId;
    private List<MacSerialListDTO> macSerialListDTOList;
}
