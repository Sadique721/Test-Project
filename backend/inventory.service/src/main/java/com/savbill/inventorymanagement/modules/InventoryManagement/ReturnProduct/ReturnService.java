package com.savbill.inventorymanagement.modules.InventoryManagement.ReturnProduct;

import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnService extends ExBaseAbstractService<ReturnDto, Return, Long> {

    public ReturnService(ReturnRepo repository, ReturnMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private ReturnRepo returnRepo;

    public ReturnDto saveReturn(ReturnDto returnDto) throws Exception{

        try {
            Return aReturn = new Return();
            //aReturn.setId(returnDto.getId());
            aReturn.setProduct_name(returnDto.getProduct_name());
            aReturn.setMac_name(returnDto.getMac_name());
            aReturn.setSerial_no(returnDto.getSerial_no());
            aReturn.setItem_condition(returnDto.getItem_condition());
            aReturn.setProduct_id(returnDto.getProduct_id());
            aReturn.setCurrent_inward_id(returnDto.getCurrent_inward_id());
            aReturn.setCurrent_inward_type(returnDto.getCurrent_inward_type());
            aReturn.setItem_status(returnDto.getItem_status());
            aReturn.setCust_id(returnDto.getCust_id());
            returnRepo.save(aReturn);

            return returnDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Return> getreturnforcustomer(Long id){
        List<Return> returns = new ArrayList<>();
        returns = returnRepo.getallforCustomer(id);
        return returns;
    }

    @Override
    public String getModuleNameForLog() {
        return "[ReturnService]";
    }
}
