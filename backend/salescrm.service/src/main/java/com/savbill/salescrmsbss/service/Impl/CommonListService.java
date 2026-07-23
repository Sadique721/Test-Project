package com.savbill.salescrmsbss.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.CommonList;
import com.savbill.salescrmsbss.entity.pojo.CommonListDTO;
import com.savbill.salescrmsbss.repository.CommonListRepository;
import com.savbill.salescrmsbss.utils.CommonConstants;

@Service
public class CommonListService {

	@Autowired
	private CommonListRepository commonListRepository;

	public List<CommonListDTO> getCommonListByTypeWithoutCaching(String type) {
		return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS).stream()
				.map(domain -> domainToDTO(domain))
				.collect(Collectors.toList());
	}


	@Cacheable(cacheNames = "commonTypes", key = "#type")
	public List<CommonListDTO> getCommonListByType(String type) {
		return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS).stream()
				.map(domain -> domainToDTO(domain))
				.collect(Collectors.toList());
	}

//	@Cacheable(cacheNames = "commonTypes", key = "#type")
//	public List<CommonListDTO> getCommonListForAudit(String type) {
//		String SUBMODULE = getModuleNameForLog() + " [getCommonListForAudit()] ";
//		try {
//			List<CommonListDTO> auditForList = getCommonListByType(type);
//			if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//				return auditForList.stream()
//						.filter(dto -> !dto.getValue().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER))
//						.collect(Collectors.toList());
//			}
//			return auditForList;
//		} catch (Exception ex) {
//			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//			throw ex;
//		}
//	}

	@Cacheable(cacheNames = "allCommonTypes")
	public List<CommonListDTO> getAllEntities() throws Exception {
		return commonListRepository.findAllByStatus(CommonConstants.ACTIVE_STATUS).stream()
				.map(domain -> domainToDTO(domain))
				.collect(Collectors.toList());
	}

	@CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
	public CommonListDTO saveEntity(CommonListDTO commonListDTO) throws Exception {
		return null;
	}

	@CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
	public CommonListDTO updateEntity(CommonListDTO commonListDTO) throws Exception {
		return null;
	}

	@CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
	public void deleteEntity(CommonListDTO commonListDTO) throws Exception {
		
	}
	
	 public CommonListDTO domainToDTO(CommonList data) {
	        if ( data == null ) {
	            return null;
	        }

	        CommonListDTO commonListDTO = new CommonListDTO();

	        if ( data.getId() != null ) {
	            commonListDTO.setDisplayId( data.getId().intValue() );
	        }
	        commonListDTO.setDisplayName( data.getText() );
	        commonListDTO.setId( data.getId() );
	        commonListDTO.setText( data.getText() );
	        commonListDTO.setValue( data.getValue() );
	        commonListDTO.setType( data.getType() );
	        commonListDTO.setStatus( data.getStatus() );
	        commonListDTO.setMvnoId( data.getMvnoId() );

	        return commonListDTO;
	    }

}
