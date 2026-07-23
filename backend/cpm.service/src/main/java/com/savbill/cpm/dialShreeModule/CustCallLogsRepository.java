package com.savbill.cpm.dialShreeModule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustCallLogsRepository extends JpaRepository<CustCallLogs,Long> {

     @Query("SELECT new com.savbill.cpm.dialShreeModule.CustCallDTO(c.user, c.phoneCode, c.phoneNumber, c.entryDate, c.callStartTime, c.callEndTime, c.uniqueId) " +
        "FROM CustCallLogs c WHERE c.phoneNumber = :phoneNum")
     Page<CustCallDTO> findAllByPhoneNum(@Param("phoneNum") String phoneNum, Pageable pageable);

    @Query(value = "select * from tblmcalllogs t where t.uniqueid =:uniqueid",nativeQuery = true)
    CustCallLogs findAllByuniqueid(@Param("uniqueid") String uniqueid);
}
