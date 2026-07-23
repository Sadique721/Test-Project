package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.deviceveri.domain.SerializedItemData;
import com.savbill.integrationsystem.deviceveri.mapper.SerializedItemMapper;
import com.savbill.integrationsystem.deviceveri.model.SerializedItemDTO;
import com.savbill.integrationsystem.deviceveri.repository.SerializedItemRepo;
import com.savbill.integrationsystem.deviceveri.service.SerializedItemService;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
//import com.savbill.integrationsystem.rabbitmq.MessageSender;
import com.savbill.integrationsystem.rms.entity.InOutWardMACMapping;
import com.savbill.integrationsystem.rms.entity.Inward;
import com.savbill.integrationsystem.rms.entity.Product;
import com.savbill.integrationsystem.rms.entity.WareHouse;
import com.savbill.integrationsystem.rms.mapper.InOutWardMACMappingMapper;
import com.savbill.integrationsystem.rms.mapper.InwardIntegrationMapper;
import com.savbill.integrationsystem.rms.mapper.ProductIntegrationMapper;
import com.savbill.integrationsystem.rms.model.*;
//import com.savbill.integrationsystem.rms.repository.InwardRepo;
import com.savbill.integrationsystem.rms.model.InOutWardMACMapingDTO;
import com.savbill.integrationsystem.rms.model.InwardDto;
import com.savbill.integrationsystem.rms.model.InwardRmsDto;
import com.savbill.integrationsystem.rms.model.ProductDetailDto;
import com.savbill.integrationsystem.rms.repository.InOutWardMACMappingRepo;
import com.savbill.integrationsystem.rms.repository.InwardRepo;
import com.savbill.integrationsystem.rms.repository.ProductRepo;
import com.savbill.integrationsystem.rms.repository.WareHouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.List;

@Service
public class InwardServiceImpl implements InwardService{

    @Autowired
    InwardRepo inwardRepo;

    @Autowired
    InwardIntegrationMapper inwardIntegrationMapper;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    WareHouseRepo wareHouseRepo;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    SerializedItemRepo serializedItemRepo;

    @Autowired
    SerializedItemService serializedItemService;

    @Autowired
    SerializedItemMapper serializedItemMapper;

    @Autowired
    ProductIntegrationMapper productIntegrationMapper;

    @Autowired
    InOutWardMACMappingMapper inOutWardMACMappingMapper;

    @Autowired
    InOutWardMACMappingRepo inOutWardMACMappingRepo;

    @Override
    public Inward saveInwardFromInventory(InwardDto inwardDto) {
        Inward inward = inwardIntegrationMapper.dtoToDomain(inwardDto,new CycleAvoidingMappingContext());
        if (inwardDto.getId() != null){
            inward.setId(inwardDto.getId());
        }
        try{
            inwardRepo.save(inward);
            return inward;
        }catch (PersistenceException e) {
            throw new PersistenceException("Not able to save Product Category from inventory : " + e);
        }

    }


    @Override
    public Inward saveInwardFromRms(InwardRmsDto inwardRmsDto) {
        InwardDto inwardDto = mapInwardFromRms(inwardRmsDto);
        Inward inward = inwardIntegrationMapper.dtoToDomain(inwardDto,new CycleAvoidingMappingContext());

        // Save inward from Rms
        inwardRepo.save(inward);
        inward.setRmsInwardId(String.valueOf(inward.getId()));
        inwardDto.setRmsInwardId(String.valueOf(inward.getId()));
        inwardRepo.save(inward);

        // Send inward to inventory
//        messageSender.send(inwardDto, RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN);
        kafkaMessageSender.send(new KafkaMessageData(inwardDto, InwardDto.class.getSimpleName()));

        // Save Serialized Item create from serial Number given in product Detail
        List<ProductDetailDto> productDetailDtos = inwardRmsDto.getProductDetails();
        SerializedItemDTO serializedItemDTO = null;

        for(ProductDetailDto productDetailDto:productDetailDtos){
            try {
                serializedItemDTO = saveSerializedItemDto(inward,productDetailDto.getSerialNumber(),inwardRmsDto);
                InOutWardMACMapingDTO inOutWardMACMapingDTO = saveInOutWardMACMapping(productDetailDto.getSerialNumber(),serializedItemDTO,inward);
                InOutWardMACMapping inOutWardMACMapping = inOutWardMACMappingMapper.dtoToDomain(inOutWardMACMapingDTO,new CycleAvoidingMappingContext());
                inOutWardMACMappingRepo.save(inOutWardMACMapping);
                serializedItemService.saveEntity(serializedItemDTO);
//                messageSender.send(inOutWardMACMapingDTO,RabbitMqConstants.QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN);
                kafkaMessageSender.send(new KafkaMessageData(inOutWardMACMapingDTO,inOutWardMACMapingDTO.getClass().getSimpleName()));
//                messageSender.send(serializedItemDTO,RabbitMqConstants.QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return inward;
    }


    public InwardDto mapInwardFromRms(InwardRmsDto inwardRmsDto){
        InwardDto inwardDto = new InwardDto();
        Product product = productRepo.findByName(inwardRmsDto.getProductName());
        WareHouse wareHouse = wareHouseRepo.findByName(inwardRmsDto.getWarehouseName());
        inwardDto.setQty(inwardRmsDto.getQuantity());
        inwardDto.setProductId(product);
        inwardDto.setType(inwardRmsDto.getType());
        inwardDto.setDestinationType("Warehouse");
        inwardDto.setDestinationId(wareHouse.getId());
        inwardDto.setStatus("ACTIVE");
        inwardDto.setApprovalStatus("Approve");
        inwardDto.setCategoryType("Forwarded");
        inwardDto.setMvnoId(2L);
        inwardDto.setTotalMacSerial((long) inwardRmsDto.getProductDetails().size());
        inwardDto.setInwardNumber(getRandomenumberForInward("IN", "-", ""));
        inwardDto.setInTransitQty(0L);
        return inwardDto;
    }

    public SerializedItemDTO saveSerializedItemDto(Inward inward,String serialNumber,InwardRmsDto inwardRmsDto) throws Exception {
        SerializedItemDTO serializedItemDTO = new SerializedItemDTO();
       // serializedItemDTO.setName(getRandomenumberForSerializedItem("SI","-",""));
        serializedItemDTO.setName(inward.getProductId().getName());
        serializedItemDTO.setSerialNumber(serialNumber);
        serializedItemDTO.setItemCondition(inward.getType());
        serializedItemDTO.setProductId(inward.getProductId().getId());
        serializedItemDTO.setCurrentInwardId(Long.valueOf(inward.getRmsInwardId()));
        serializedItemDTO.setCurrentInwardType(inward.getCategoryType().toLowerCase());
        WareHouse wareHouse = wareHouseRepo.findByName(inwardRmsDto.getWarehouseName());
        serializedItemDTO.setOwner_id(wareHouse.getId());
        serializedItemDTO.setOwnerType(inward.getDestinationType());
        serializedItemDTO.setItemStatus("UnAllocated");
        serializedItemDTO.setOwnershipType("Subisu Owned");

        return serializedItemDTO;
    }

    public InOutWardMACMapingDTO saveInOutWardMACMapping(String serialNumber,SerializedItemDTO serializedItemDTO,Inward inward) throws Exception {
        InOutWardMACMapingDTO inOutWardMACMapingDTO = new InOutWardMACMapingDTO();
        inOutWardMACMapingDTO.setStatus("ACTIVE");
        inOutWardMACMapingDTO.setSerialNumber(serialNumber);
        inOutWardMACMapingDTO.setIsForwarded(0);
        inOutWardMACMapingDTO.setIsReturned(0);
        inOutWardMACMapingDTO.setItemId(serializedItemDTO.getId());
        inOutWardMACMapingDTO.setInwardId(Long.valueOf(inward.getRmsInwardId()));
        inOutWardMACMapingDTO.setMvnoId(2L);
        return inOutWardMACMapingDTO;
    }

    public String getRandomenumberForInward(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            Inward inward = inwardRepo.findTopByOrderByIdDesc();
            if (inward == null) {
                flag += 1;
            } else {
                flag += inward.getId() + 1;
            }
        }
        return flag;
    }

    public String getRandomenumberForSerializedItem(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            SerializedItemData serializedItemData = serializedItemRepo.findTopByOrderByIdDesc();
            if (serializedItemData == null) {
                flag += 1;
            } else {
                flag += serializedItemData.getId() + 1;
            }
        }
        return flag;
    }
}
