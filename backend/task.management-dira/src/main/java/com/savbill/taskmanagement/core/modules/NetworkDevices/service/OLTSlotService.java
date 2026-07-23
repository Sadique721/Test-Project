package com.savbill.taskmanagement.core.modules.NetworkDevices.service;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.NetworkDevices.domain.Oltslots;
import com.savbill.taskmanagement.core.modules.NetworkDevices.dto.OLTSlotDetailDTO;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class OLTSlotService extends ExBaseAbstractService<OLTSlotDetailDTO, Oltslots, Long> {
    public OLTSlotService(JpaRepository<Oltslots, Long> repository, IBaseMapper<OLTSlotDetailDTO, Oltslots> mapper) {
        super(repository, mapper);
    }
//    public OLTSlotService(OLTSlotRepository repository, OLTSlotMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Autowired
//    OLTSlotRepository slotRepository;
//
//    @Autowired
//    OLTSlotMapper mapper;
//
//    @Override
//    public String getModuleNameForLog() {
//        return " [OLTSlotService()] ";
//    }
//
//    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        List<GenericDataDTO> temp = new ArrayList<GenericDataDTO>();
//        return (GenericDataDTO) temp;
//    }
//
//    public List<OLTSlotDetailDTO> getEntityByNetworkId(Long networkdevice_id) {
//        List<Oltslots> oltslotsList = slotRepository.findAllByNetworkDevices_Id(networkdevice_id);
//        return oltslotsList.stream().map(data ->
//                mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//    }
//
//
//    public boolean duplicateVerifyAtSaveInSloat(String name,Integer deviceId)throws Exception
//    {
//        boolean flag=false;
//        Integer count=slotRepository.duplicateVerifyAtSave(deviceId,name);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
//    public boolean duplicateVerifyEditInSloat(String name,Integer deviceId,Integer sloatId)throws Exception
//    {
//        boolean flag=false;
//        Integer count=slotRepository.duplicateVerifyAtEdit(deviceId,name,sloatId);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
//
//    @Override
//    public boolean deleteVerification(Integer id)throws Exception
//    {
//        boolean flag=false;
//        if(id!=null){
//            Integer count=slotRepository.deleteVerifySlot(id);
//            if(count==0){
//                flag=true;
//            }
//        }
//    return flag;
//    }


    @Override
    public String getModuleNameForLog() {
        return " [OLTSlotService()] ";
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("OltSlot");
        createExcel(workbook, sheet, OLTSlotDetailDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, OLTSlotDetailDTO.class, null);
    }
}
