package com.savbill.radius.repository;

import java.util.List;

import com.savbill.radius.helper.ClientDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>,QuerydslPredicateExecutor<Client> 
{
	@Query(value="select * from tbltclients where clientip=?",nativeQuery = true)
	Client findClientByIpAddress(String ipAddress);
	
	@Query(value = "select clientid,clientip from tbltclients c where c.clientip=:clientip",nativeQuery = true)
	List<Object[]> checkForUniqueClientIp(@Param("clientip") String clientip);
	
	@Query(value = "select clientid,clientip from tbltclients c where c.clientip=:clientip AND clientid!=:clientid",nativeQuery = true)
	List<Object[]> checkForUniqueClientIpOnUpdate(@Param("clientid") Long clientid, @Param("clientip") String clientip);
	
	@Query(value = "select clientid,clientip from tbltclients c where c.clientgroupid=:clientgroupid",nativeQuery = true)
	List<Object[]> checkForClientGroupIp(@Param("clientgroupid") Long clientgroupid);
	
	List<Client> findAll();

	List<Client> findByClientIdIn(List<Long> ids);

	Client findByClientId(Long id);

	@Query("SELECT new com.savbill.radius.helper.ClientDto(c.clientId, c.clientIpAddress) " +
			"FROM Client c where c.deviceId IS NULL")
	List<ClientDto> findAllClientList();

	@Query("SELECT new com.savbill.radius.helper.ClientDto(c.clientId, c.clientIpAddress) " +
			"FROM Client c where c.deviceId IS NULL and c.mvnoId in :mvnoIds")
	List<ClientDto> findAllClientListByMvnoId(@Param("mvnoIds") List<Integer> mvnoIds);

	List<Client> findAllByDeviceId(Long id);
	@Query(value = "SELECT * FROM tbltclients  WHERE clientip = :ipaddress", nativeQuery = true)
	Client findByClientIP(@Param("ipaddress") String ipAddress);


}
