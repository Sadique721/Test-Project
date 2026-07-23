package com.savbill.cpm.modules.childcustomer.service;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.modules.childcustomer.dto.ChangePasswordPojo;
import com.savbill.cpm.modules.childcustomer.dto.ChildCustPojo;
import com.savbill.cpm.modules.childcustomer.entity.ChildCustomer;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface ChildCustomerService {
    ResponseEntity<?> create(ChildCustPojo pojo, HttpServletRequest req);

    List<ChildCustomer> getChildCustomer();

    void delete(Long id, HttpServletRequest req);

    GenericDataDTO getchildCustByParentID(Long parentId);
    ResponseEntity<?> updatechildCustByParentID(ChildCustPojo pojo);

    Page<ChildCustomer> getAllChildCustomer(Integer page,Integer pagesize);

    ChildCustomer getchildCustByID(Long id);

    Page<ChildCustomer> getchildCustByID(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String status);

    Page<ChildCustomer> getChildByParentCustId(Long id,Integer page , Integer pageSize);

    List<ChildCustomer> getchildCustByMobileNumber(String mobileNumber,Integer parentId,Long mvnoId);


    ResponseEntity<?> updateChildPassword(ChangePasswordPojo pojo);

    GenericDataDTO  getChildCustomerByMobileNumberAndUserName(String username,String mobileNumber);
}
