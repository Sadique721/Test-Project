package com.savbill.commonGateway.core.repository;

import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Component
public class CustomRepository<T> {
    @Autowired
    private EntityManager em;

    private ObjectMapper mapper = new ObjectMapper();

    public List<T> getResultOfQuery(String argQueryString, Class<T> valueType) {
        try {
            Query query = em.createNativeQuery(argQueryString);
            NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
            nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            List<Map<String, Object>> result = nativeQuery.getResultList();
            List<T> resultList = result.stream()
                    .map(o -> {
                        try {
                            return mapper.readValue(mapper.writeValueAsString(o), valueType);
                        } catch (Exception e) {
                            ApplicationLogger.logger.error(e.getMessage(), e);
                        }
                        return null;
                    }).collect(Collectors.toList());
            return resultList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    public Boolean updateMvnoStatusForStaff(List<Long> staffid, String status, Boolean mvnoDeactivationFlag) {
        String query = "UPDATE tblmstaffuser SET sstatus = :status, mvno_deactivation_flag= :mvnoDeactivationFlag   WHERE staffid IN (:staffid)";
        return updateQueryNativeForStaff(query, "status", status,"mvnoDeactivationFlag", mvnoDeactivationFlag, "staffid", staffid);

    }

    public boolean updateQueryNativeForStaff(String query, String statusParam, String statusValue, String mvnoBoolFlag, Boolean flag, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(mvnoBoolFlag,flag)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return true;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }


    public boolean updateMvnoStatus(List<Long> mvnoIds, String status, Boolean mvnoDeactivationFlag) {
        String query = "UPDATE tblmmvno SET status = :status, mvno_deactivation_flag= :mvnoDeactivationFlag  WHERE MVNOID IN (:mvnoIds)";
        return  updateQueryNative(query, "status", status,"mvnoDeactivationFlag",mvnoDeactivationFlag, "mvnoIds", mvnoIds);

    }

    public boolean updateQueryNative(String query, String statusParam, String statusValue, String mvnoboolflag, Boolean flag, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(mvnoboolflag,flag)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return true;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }


    public boolean updateMvnoStatusForCustomer(List<Long> custid, String status, Boolean mvnoDeactivationFlag) {
        String query = "UPDATE tblcustomers SET cstatus = :status, mvno_deactivation_flag= :mvnoDeactivationFlag  WHERE custid IN (:custid)";
        return  updateQueryNativeForCustomer(query, "status", status, "mvnoDeactivationFlag",mvnoDeactivationFlag, "custid", custid);
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
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }






}

