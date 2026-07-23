package com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.repository;

import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.domain.MvnoDocDetails;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.model.MvnoDocDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MvnoDocDetailsRepository extends JpaRepository<MvnoDocDetails, Long> {


    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.model.MvnoDocDetailsDTO(m.docId, m.mvno.id, m.docType, m.docSubType, m.mode, m.remark, m.docStatus, m.filename, m.uniquename, m.isDelete, m.startDate, m.endDate, m.nextTeamHierarchyMappingId, m.nextStaff,m.uniquename) FROM MvnoDocDetails m WHERE m.isDelete = false AND m.mvno.id = :mvnoId")
    List<MvnoDocDetailsDTO> findAllByMvnoIdAndIsDeleteFalse(Long mvnoId);


}
