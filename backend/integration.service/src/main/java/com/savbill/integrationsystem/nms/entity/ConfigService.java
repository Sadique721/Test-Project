package com.savbill.integrationsystem.nms.entity;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.UpdateDiffFinder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Service
public class ConfigService extends ExBaseAbstractService<ConnfigurationDTO, Connfiguration, Long> {
    @Autowired
    ConfigRepocitory configRepocitory;
    @Autowired
    ConnfigurationMapper connfigurationMapper;
    public PageRequest pageRequest = null;

    private static final Logger logger = LoggerFactory.getLogger("ConfigService.class");
    public ConfigService(JpaRepository<Connfiguration, Long> repository, IBaseMapper<ConnfigurationDTO, Connfiguration> mapper) {
        super(repository, mapper);
    }

    public String createConfiguration(Connfiguration connfiguration) {
        try{
            connfiguration.setMvnoId(getMvnoIdFromCurrentStaff());
            connfiguration.setIsdeleted(false);
            configRepocitory.save(connfiguration);
        }catch (Exception e){
            e.getMessage();
        }
        return  "Success";
    }

    public Boolean duplicateVerifyName(String name) {
        Boolean flag=true;
        Integer connfiguration=0;
        try{
            connfiguration= configRepocitory.countByName(name);
           if(connfiguration==1){
               flag=false;
           }
        }catch (Exception e){
            e.getMessage();
        }
        return  flag;
    }

    public String updateConfiguration(Connfiguration connfiguration, HttpServletRequest request) {
        try{
            connfiguration.setIsdeleted(false);
           Connfiguration  connfiguration1=configRepocitory.findById(connfiguration.getId().longValue()).orElse(null);
           String updated=UpdateDiffFinder.getUpdatedDiff(connfiguration1,connfiguration);
           if(!Objects.isNull(connfiguration1)){
                connfiguration1.setPort(connfiguration.getPort());
                connfiguration1.setName(connfiguration.getName());
                connfiguration1.setBaseurl(connfiguration.getBaseurl());
                connfiguration1.setUsername(connfiguration.getUsername());
                connfiguration1.setPassword(connfiguration.getPassword());
                configRepocitory.save(connfiguration1);
            }
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + " Updated Configuration , updated values: "+updated+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            e.getMessage();
        }
        return  null;
    }

    public GenericDataDTO fetchConfiguration(PaginationRequestDTO paginationRequestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            Page<Connfiguration> paginationList = configRepocitory.findByIsdeletedIsFalse(pageRequest);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            if (0 < paginationList.getSize()) {
                genericDataDTO.setResponseMessage("ConfigurationList Fetched Successfully");
                makeGenericResponse(genericDataDTO, paginationList);
            }
        }catch (Exception e){
            genericDataDTO.setResponseMessage("Unable to fetch ConfigurationList ");
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            e.getMessage();
        }
        return genericDataDTO;
    }


    @Override
    public String getModuleNameForLog() {
        return "ConfigService";
    }

    public GenericDataDTO findById(Long id) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            genericDataDTO.setData(configRepocitory.findById(id).get());
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage(" Configuration by Id "+id+"fetched Successfull");
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage("Unable to fetch Configuration by Id "+id);
            e.getMessage();
        }

        return genericDataDTO;
    }

    public GenericDataDTO deleConfig(Long id) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            Connfiguration connfiguration=configRepocitory.findById(id).orElse(null);
            if(!Objects.isNull(connfiguration)){
                connfiguration.setIsdeleted(true);
                configRepocitory.save(connfiguration);
                genericDataDTO.setResponseMessage("Configuration with name "+connfiguration.getName()+"is deleted Successfully");
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            }

        }catch (Exception e){
            genericDataDTO.setResponseMessage("Unble to deleteConfiguration with id"+id);
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            e.getMessage();
        }
        return genericDataDTO;
    }

    public Page<Connfiguration> fetchConfiguration(Integer page, Integer pageSize,  Integer sortOrder, List<GenericSearchModel> filters) {
        Page<Connfiguration> connfigurations=null;
        PageRequest pageRequest = generatePageRequest(page, pageSize, "id", 1);
        QConnfiguration qConnfiguration=QConnfiguration.connfiguration;
        BooleanExpression booleanExpression=qConnfiguration.isNotNull().and(qConnfiguration.isdeleted.eq(false));
        for (GenericSearchModel searchModel : filters) {

        if(searchModel.getFilterColumn().contains("baseurl")) {
            booleanExpression = booleanExpression.and(qConnfiguration.baseurl.containsIgnoreCase(searchModel.getFilterValue()));
        }
            if(searchModel.getFilterColumn().contains("port")) {
                booleanExpression = booleanExpression.and(qConnfiguration.port.like(searchModel.getFilterValue()));
            }

            if(searchModel.getFilterColumn().contains("username")) {
                booleanExpression = booleanExpression.and(qConnfiguration.username.containsIgnoreCase(searchModel.getFilterValue()));
            }

        }
        return configRepocitory.findAll(booleanExpression, pageRequest);

    }
    public Connfiguration findByname(String name) {
        Connfiguration connfiguration=null;
        try{
            connfiguration= configRepocitory.findByName(name);
        }catch (Exception e){
            e.getMessage();
        }
        return  connfiguration;
    }
}
