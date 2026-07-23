package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.SlotService;

import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.Oltslots;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper.OLTSlotMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.SloatRepository.OLTSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OLTSlotService extends ExBaseAbstractService<OLTSlotDetailDTO, Oltslots, Long> {
    public OLTSlotService(OLTSlotRepository repository, OLTSlotMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    OLTSlotRepository slotRepository;

    @Autowired
    OLTSlotMapper mapper;

    @Override
    public String getModuleNameForLog() {
        return " [OLTSlotService()] ";
    }

    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        List<GenericDataDTO> temp = new ArrayList<GenericDataDTO>();
        return (GenericDataDTO) temp;
    }

    public List<OLTSlotDetailDTO> getEntityByNetworkId(Long networkdevice_id) {
        List<Oltslots> oltslotsList = slotRepository.findAllByNetworkDevices_Id(networkdevice_id);
        return oltslotsList.stream().map(data ->
                mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }


    public boolean duplicateVerifyAtSaveInSloat(String name,Integer deviceId)throws Exception
    {
        boolean flag=false;
        Integer count=slotRepository.duplicateVerifyAtSave(deviceId,name);
        if(count==0){
            flag=true;
        }
        return flag;
    }
    public boolean duplicateVerifyEditInSloat(String name,Integer deviceId,Integer sloatId)throws Exception
    {
        boolean flag=false;
        Integer count=slotRepository.duplicateVerifyAtEdit(deviceId,name,sloatId);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        if(id!=null){
            Integer count=slotRepository.deleteVerifySlot(id);
            if(count==0){
                flag=true;
            }
        }
    return flag;
    }


//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("OltSlot");
//        createExcel(workbook, sheet, OLTSlotDetailDTO.class, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, OLTSlotDetailDTO.class, null);
//    }
}
