package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.SerializedItemData;

@Repository
public interface SerializedItemRepo extends JpaRepository<SerializedItemData, Long>
{
	List<SerializedItemData> findBySerialNumberAndIsDeleted(String serialNum, Integer isDeleted);
	
	Optional<SerializedItemData> findByIdAndIsDeleted(Long id, Integer isDeleted);

	SerializedItemData findTopByOrderByIdDesc();
}
