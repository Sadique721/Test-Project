package com.savbill.radius.repository;


import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Component
public class CustomRepository<T> {
    @Autowired
    private EntityManager em;

    private ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);


    public boolean updateMvnoStatusForCustomer(List<Long> mvnoIds, String status, Boolean mvnoDeactivationFlag) {
        String query = "UPDATE tblcustomers SET cstatus = :status, mvno_deactivation_flag = :mvnoDeactivationFlag WHERE custid IN (:mvnoIds)";
        return  updateQueryNativeForCustomer(query, "status", status, "mvnoDeactivationFlag", mvnoDeactivationFlag, "mvnoIds", mvnoIds);
    }

    public boolean updateQueryNativeForCustomer(String query, String statusParam, String statusValue, String mvnoboolflag, Boolean flag, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(mvnoboolflag,flag)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return  true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }



}

