package com.savbill.integrationsystem.NewNMSIntegration.service;

import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import com.savbill.integrationsystem.NewNMSIntegration.dto.IntegrationSpecificParamDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.WifiConfigGetDetailDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.WifiConfigRequestDTO;
import com.savbill.integrationsystem.NewNMSIntegration.entity.IntegrationParameters;
import com.savbill.integrationsystem.NewNMSIntegration.entity.NmsIntegration;
import com.savbill.integrationsystem.NewNMSIntegration.message.NMSIntegrationMessage;
import com.savbill.integrationsystem.NewNMSIntegration.repository.IntegrationParametersRepository;
import com.savbill.integrationsystem.NewNMSIntegration.repository.NnmIntegrationRepository;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NmsIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NmsIntegrationService.class);
    @Autowired
    private NnmIntegrationRepository repository;

    @Autowired
    private IntegrationParametersRepository integrationParametersRepository;

    @Autowired
    private APIIntegrationService apiIntegrationService;

    @Autowired
    NnmIntegrationRepository nnmIntegrationRepository;

    public List<NmsIntegration> getAllIntegrations() {
        return repository.findAll();
    }

    public Optional<NmsIntegration> getIntegrationById(Long id) {
        return repository.findById(id);
    }

    public NmsIntegration saveIntegration(NmsIntegration integration) {
        return repository.save(integration);
    }

    public void deleteIntegration(Long id) {
        repository.deleteById(id);
    }

    public List<NmsIntegration> getByCustomerId(Long customerId) {
        List<NmsIntegration> nmsIntegrations = repository.findAllByCustomerId(customerId);
        return nmsIntegrations;
    }

    public String acknowledgeInIntegration(Long id) {
        try {
            Optional<NmsIntegration> integrationOptional = repository.findById(id);
            if (!integrationOptional.isPresent()) {
                throw new ResourceNotFoundException("Integration not found with id: " + id);
            }

            List<IntegrationParameters> integrationParameters = integrationParametersRepository.findByIntegrationId(id);
            if (integrationParameters.isEmpty()) {
                return "No integration parameters found for id: " + id;
            }

            List<IntegrationSpecificParamDTO> integrationSpecificParamDTOS = integrationParameters.stream()
                    .map(param -> new IntegrationSpecificParamDTO(param.getParamName(), param.getParamValue()))
                    .collect(Collectors.toList());

            NmsIntegration integration = integrationOptional.get();
            NMSIntegrationMessage nmsIntegrationMessage = new NMSIntegrationMessage();
            nmsIntegrationMessage.setList(integrationSpecificParamDTOS);
            nmsIntegrationMessage.setCustomerId(integration.getCustomerId());
            nmsIntegrationMessage.setConfigName(integration.getConfigName());
            nmsIntegrationMessage.setItemId(integration.getItemId());
            nmsIntegrationMessage.setOperation(integration.getOperation());
            nmsIntegrationMessage.setMvnoId(integration.getMvnoId());
            nmsIntegrationMessage.setSerialNumber(integration.getSerialNumber());
            nmsIntegrationMessage.setCustInvenId(integration.getCustInvenId());

            String operation = integration.getOperation();
            if (NMSIntegrationConstant.API_CONSTANT.ADD_ONU.equalsIgnoreCase(operation)) {
                return apiIntegrationService.addONUIntegration(nmsIntegrationMessage);
            } else if (NMSIntegrationConstant.API_CONSTANT.DELETE_ONU.equalsIgnoreCase(operation)) {
                return apiIntegrationService.delONUIntegration(nmsIntegrationMessage);
            } else {
                return "Unsupported operation: " + operation;
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            // Log the exception and return a meaningful message
            LOGGER.error("Error processing integration for id: {}", id, e);
            return "An error occurred while processing the integration. Please try again later.";
        }
    }

    public WifiConfigGetDetailDTO getDetailDTO(WifiConfigRequestDTO wifiConfigRequestDTO) {
        // Fetch NmsIntegration list based on filtering conditions
        List<NmsIntegration> nmsIntegrationList = nnmIntegrationRepository.findByItemIdAndCustomerIdAndCustInvenIdAndOperationAndStatusAndSerialNumberAndMvnoId(
                wifiConfigRequestDTO.getItemId(),
                wifiConfigRequestDTO.getCustomerId(),
                wifiConfigRequestDTO.getCustInvenId(),
                NMSIntegrationConstant.API_CONSTANT.WIFI_CONFIG,
                NMSIntegrationConstant.API_CONSTANT.COMPLETED,
                wifiConfigRequestDTO.getSerialNumber(),
                getMvnoIdFromCurrentStaff().longValue());
        // Initialize DTO with request parameters
        WifiConfigGetDetailDTO wifiConfigGetDetailDTO = new WifiConfigGetDetailDTO();
        wifiConfigGetDetailDTO.setCustomerId(wifiConfigRequestDTO.getCustomerId());
        wifiConfigGetDetailDTO.setItemId(wifiConfigRequestDTO.getItemId());
        wifiConfigGetDetailDTO.setCustInvenId(wifiConfigRequestDTO.getCustInvenId());
        wifiConfigGetDetailDTO.setSerialNumber(wifiConfigRequestDTO.getSerialNumber());
        if (!nmsIntegrationList.isEmpty()) {
            // Get the last element in the list
            NmsIntegration nmsIntegration = nmsIntegrationList.get(nmsIntegrationList.size() - 1);
            // Fetch integration parameters
            List<IntegrationParameters> integrationParameters = integrationParametersRepository.findByIntegrationId(nmsIntegration.getId());
            // Map parameter values using streams
            integrationParameters.forEach(param -> {
                if (param.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.SSIDNAME)) {
                    wifiConfigGetDetailDTO.setSsidUsername(param.getParamValue());
                } else if (param.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.PRESHAREDKEY)) {
                    wifiConfigGetDetailDTO.setSsidPassword(param.getParamValue());
                } else if (param.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.WORKINGFREQUENCY)) {
                    Integer paramValue = Integer.valueOf(param.getParamValue());
                    if (paramValue == 0) {
                        wifiConfigGetDetailDTO.setWorkingFrequency("0");
                    } else if (paramValue == 1) {
                        wifiConfigGetDetailDTO.setWorkingFrequency("1");
                    }
                }
            });
        }
        return wifiConfigGetDetailDTO;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

}

