package com.savbill.cpm.modules.InventoryManagement.ItemGroup;

import com.savbill.cpm.constants.DeleteContant;
 import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL+UrlConstants.ITEM_ASSEMBLY)
@Api(value = "ItemAssemblyController", description = "REST APIs related to ItemAssembly Entity!!!!", tags = "Item_Assembly_Controller")
public class ItemAssemblyController extends ExBaseAbstractController<ItemAssemblyDto> {

    @Autowired
    ItemAssemblyServiceImp itemAssemblyServiceImp;

    @Autowired
    ItemAssemblyProductMappingRepo itemAssemblyProductMappingRepo;
    private final ItemAssemblyRepo itemAssemblyRepo;

    public ItemAssemblyController(ItemAssemblyServiceImp service,
                                  ItemAssemblyRepo itemAssemblyRepo) {
        super(service);
        this.itemAssemblyRepo = itemAssemblyRepo;
    }
   @PostMapping("/itemGroupSave")
    public GenericDataDTO save(@RequestBody ItemAssemblyDto entityDTO) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            if (entityDTO.getItemAssemblyName().length() > 50) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Input size is Exceeded");
            } else {
                    ItemAssemblyDto itemAssemblyDto = itemAssemblyServiceImp.saveEntity(entityDTO);
                    genericDataDTO.setData(itemAssemblyDto);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                }

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }


    @Override
    public GenericDataDTO delete(@RequestBody ItemAssemblyDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = itemAssemblyServiceImp.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            itemAssemblyRepo.deleteById(entityDTO.getId());
            dataDTO.setResponseMessage("Deleted SuccessFully");
            dataDTO.setResponseCode(HttpStatus.OK.value());

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.ITEM_ASSEMBLY_EXIST);
        }
        return dataDTO;

    }



    @PostMapping("/getAllItemGroup")
    public GenericDataDTO getAllItemGroup(@RequestBody PaginationRequestDTO requestDTO) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO = itemAssemblyServiceImp.getAllItemProduct(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            return genericDataDTO;

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    @PostMapping("/searchByNameItemGroup")
    public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = itemAssemblyServiceImp.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
        } catch (Exception ex) {
            throw ex;
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
