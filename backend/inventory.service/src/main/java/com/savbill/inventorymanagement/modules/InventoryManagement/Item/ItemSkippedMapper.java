package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.MacSerialListDTO;
import org.springframework.stereotype.Component;

@Component
public class ItemSkippedMapper {

    public ItemSkipped toEntity(
            MacSerialListDTO dto,
            Inward inward,
            String reason) {

        ItemSkipped e = new ItemSkipped();
        e.setInwardId(inward.getId());
        e.setMvnoId(inward.getMvnoId().longValue());

//        e.setImsi(dto.getImsi());
//        e.setIccid(dto.getIccid());
//
//        e.setPin1(dto.getPin1());
//        e.setPuk1(dto.getPuk1());
//        e.setPin2(dto.getPin2());
//        e.setPuk2(dto.getPuk2());
//
//        e.setKiEncrypted(dto.getKiEncrypted());
//        e.setAcc(dto.getAcc());
//        e.setAdm(dto.getAdm());
//        e.setKic(dto.getKic());
//        e.setKid(dto.getKid());
//        e.setKik(dto.getKik());
//        e.setMsisdn(dto.getMsisdn());
        e.setReason(reason);
        e.setType("Inward");
        return e;
    }

    public ItemSkipped toEntity(
            Inward inward,
            String reason) {

        ItemSkipped e = new ItemSkipped();
        e.setInwardId(inward.getId());
        e.setMvnoId(inward.getMvnoId().longValue());
        e.setReason(reason);
        return e;
    }

}
