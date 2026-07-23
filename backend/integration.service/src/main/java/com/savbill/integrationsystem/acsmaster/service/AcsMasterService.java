package com.savbill.integrationsystem.acsmaster.service;

import com.savbill.integrationsystem.acsmaster.entity.AcsMaster;
import com.savbill.integrationsystem.acsmaster.entity.AcsMasterAPIMapping;
import com.savbill.integrationsystem.acsmaster.entity.QAcsMaster;
import com.savbill.integrationsystem.acsmaster.mapper.AcsMasterMapper;
import com.savbill.integrationsystem.acsmaster.model.AcsMasterDTO;
import com.savbill.integrationsystem.acsmaster.repository.AcsMasterRepository;
import com.savbill.integrationsystem.acsmaster.repository.AcsMasterUrlParamMappingRepo;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AcsMasterService extends ExBaseAbstractService<AcsMasterDTO, AcsMaster, Long> {


    @Autowired
    private AcsMasterRepository acsMasterRepository;

    @Autowired
    private AcsMasterUrlParamMappingRepo acsMasterUrlParamMappingRepo;

    @Autowired
    private AcsMasterMapper acsMasterMapper;

    public AcsMasterService(AcsMasterRepository acsMasterRepository, AcsMasterMapper acsMasterMapper) {
        super(acsMasterRepository, acsMasterMapper);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public AcsMasterDTO saveEntity(AcsMasterDTO entity) throws Exception {
        return super.saveEntity(entity);
//        AcsMasterUrlParamMapping acsMasterUrlParamMapping= new AcsMasterUrlParamMapping();
//
//        AcsMasterDTO acsMaster = super.saveEntity(entity);
//        acsMasterUrlParamMapping.setAcsmasterid(acsMaster.getId());
//        return  acsMaster;
    }


    public GenericDataDTO getMacAddress(Long vendorId, String serialNumber, String apiName, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            AcsMaster acsMaster = acsMasterRepository.getAcsMasterByVendorIdAndMvnoId(vendorId, mvnoId);
            String url = "";
            for (AcsMasterAPIMapping acsMasterAPIMapping : acsMaster.getAcsMasterAPIMappings()) {
                if (acsMasterAPIMapping.getApiName().equalsIgnoreCase(apiName)) {
                    url = acsMasterAPIMapping.getEndpoint();
                }
            }
            genericDataDTO.setData(sendHTTPRequestGet(acsMaster.getUrl() + url + serialNumber, acsMaster));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setData("No MAC Found");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

        }
        return genericDataDTO;
    }

    public String sendHTTPRequestGet(String url, AcsMaster acsMaster) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
//            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(navMaster.getUserName(), navMaster.getPwd());
            Header[] headers = new Header[acsMaster.getAcsMasterUrlParamMappingList().size()];
            for (int i = 0; i < acsMaster.getAcsMasterUrlParamMappingList().size(); i++) {
                headers[i] = new BasicHeader(acsMaster.getAcsMasterUrlParamMappingList().get(i).getParamName(), acsMaster.getAcsMasterUrlParamMappingList().get(i).getParamValue());
            }
            HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(url).build();
            httpUriRequest.setHeaders(headers);
            CloseableHttpResponse result = null;
            result = client.execute(httpUriRequest);
            HttpEntity entity = result.getEntity();
            String content = EntityUtils.toString(entity);
            JSONObject response = new JSONObject(content);
            System.out.println("======================================================Request.================================================================\n");
            System.out.println("Request URL : " + url);
            System.out.println("======================================================Request End.================================================================\n");
            System.out.println("======================================================Response.================================================================\n");
            System.out.println("Response : " + response);
            System.out.println("======================================================Response End.================================================================\n");
            client.close();
            return content;

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);

        }
    }

    public GenericDataDTO getAcsMasterByVendorId(Long vendorId, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        AcsMaster acsMaster = acsMasterRepository.getAcsMasterByVendorIdAndMvnoId(vendorId, mvnoId);
        genericDataDTO.setData(acsMaster);
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        return genericDataDTO;
    }

    public GenericDataDTO getAll() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<AcsMaster> list = acsMasterRepository.findAll();
        genericDataDTO.setDataList(list);
        return genericDataDTO;
    }

    @Override
    public AcsMasterDTO getEntityById(Long id, Long mvnoId) {
        try {
            AcsMaster acsMaster = acsMasterRepository.getAcsMasterByIdAndMvnoIdAndIsdeleteFalse(id, mvnoId);
            return getMapper().domainToDTO(acsMaster, new CycleAvoidingMappingContext());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    @Override
    public GenericDataDTO getListByPagination(PageRequest pageRequest, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QAcsMaster acsMaster = QAcsMaster.acsMaster;
            String authTokenHeader = request.getHeader("Authorization");
            Page<AcsMaster> paginationList = acsMasterRepository.findAll(acsMaster.mvnoId.in(getMvnoId(authTokenHeader), 1, 2).and(acsMaster.isdelete.eq(false)), pageRequest);
            if (0 < paginationList.getSize()) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;

    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, HttpServletRequest request) {
        try {
            QAcsMaster acsMaster = QAcsMaster.acsMaster;
            String authTokenHeader = request.getHeader("Authorization");
            PageRequest pageRequest = generatePageRequest(page, pageSize, "id", 0);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            BooleanExpression booleanExpression = acsMaster.isNotNull().and(acsMaster.isdelete.eq(false));
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().contains("any")) {
                    if (!searchModel.getFilterValue().isEmpty()) {
                        booleanExpression = booleanExpression.and(acsMaster.name.likeIgnoreCase(searchModel.getFilterValue()));
                    }
                }
            }
            if (getMvnoId(authTokenHeader) != 1 || getMvnoId(authTokenHeader) != 2) {
                booleanExpression = booleanExpression.and(acsMaster.mvnoId.in(getMvnoId(authTokenHeader), 1, 2));
            }

            Page<AcsMaster> acsMasters = acsMasterRepository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(new ArrayList<>(acsMasters.getContent()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(acsMasters.getTotalElements());
            genericDataDTO.setPageRecords(acsMasters.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(acsMasters.getNumber() + 1);
            genericDataDTO.setTotalPages(acsMasters.getTotalPages());
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public AcsMaster getAcsMasterByIdAndMvnoIdAndIsdeleteFalse(Long aLong, Long mvnoId) {
        try {
            return acsMasterRepository.getAcsMasterByIdAndMvnoIdAndIsdeleteFalse(aLong, mvnoId);
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }
}

