package com.savbill.commonGateway.MicroSeviceDataShare.SharedServices;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.*;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.*;
import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.kafka.KafkaConstant;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain.BankManagement;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.model.BankManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Department.dto.DepartmentPojo;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.domain.PlanService;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.Region;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain.SubBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Domain.SubBusinessVertical;
import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.CustAccountProfile;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.UpdateMvnoData;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.Teams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;

@Service
public class CreateDataSharedService {

    @Autowired
    KafkaMessageSender kafkaMessageSender;


    //SAVE ENTITY COMMON SERVICE
    public void sendEntitySaveDataForAllMicroService(Object object) {

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {
            //All data of State entity while saving
            SaveStateSharedDataMessage saveStateSharedDataMessage = new SaveStateSharedDataMessage();
            saveStateSharedDataMessage.setId(((State) object).getId());
            saveStateSharedDataMessage.setStatus(((State) object).getStatus());
            saveStateSharedDataMessage.setCountry(((State) object).getCountry());
            saveStateSharedDataMessage.setName(((State) object).getName());
            saveStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            saveStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            saveStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            saveStateSharedDataMessage.setCountry(((State) object).getCountry());
            saveStateSharedDataMessage.setCreatedByName(((State) object).getCreatedByName());
            saveStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            saveStateSharedDataMessage.setLastModifiedByName(((State) object).getLastModifiedByName());
            saveStateSharedDataMessage.getCountry().setUpdatedate(null);
            saveStateSharedDataMessage.getCountry().setCreatedate(null);

            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveStateSharedDataMessage,SaveStateSharedDataMessage.class.getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {
            //All data of Country entity while saving
            SaveCountrySharedDataMessage saveCountrySharedDataMessage = new SaveCountrySharedDataMessage();
            saveCountrySharedDataMessage.setId(((Country) object).getId());
            saveCountrySharedDataMessage.setName(((Country) object).getName());
            saveCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            saveCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            saveCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            saveCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());
            saveCountrySharedDataMessage.setCreatedByName(((Country) object).getCreatedByName());
            saveCountrySharedDataMessage.setLastModifiedByName(((Country) object).getLastModifiedByName());

            //messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveCountrySharedDataMessage,SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(saveCountrySharedDataMessage, RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_PARTNER_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveCountrySharedDataMessage,SaveCountrySharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(City.class)) {
            //All data of City entity while saving
            SaveCitySharedDataMessage saveCitySharedDataMessage = new SaveCitySharedDataMessage();
            saveCitySharedDataMessage.setId(((City) object).getId());
            saveCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            saveCitySharedDataMessage.setStatus(((City) object).getStatus());
            if(((City) object).getState() != null)
                saveCitySharedDataMessage.setState(new State(((City) object).getState()));
            saveCitySharedDataMessage.setName(((City) object).getName());
            saveCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            saveCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            saveCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            saveCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());
            saveCitySharedDataMessage.setCreatedByName(((City) object).getCreatedByName());
            saveCitySharedDataMessage.setLastModifiedByName(((City) object).getLastModifiedByName());

            //messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send( saveCitySharedDataMessage,  SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveCitySharedDataMessage,SaveCitySharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)) {
            //All data of Pincode entity while saving
            SavePincodeSharedDataMessage savePincodeSharedDataMessage = new SavePincodeSharedDataMessage();
            savePincodeSharedDataMessage.setId(((Pincode) object).getId());
            savePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            savePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            savePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            savePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            savePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            savePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            savePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            savePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            savePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());
            savePincodeSharedDataMessage.setCreatedByName(((Pincode) object).getCreatedByName());
            savePincodeSharedDataMessage.setLastModifiedByName(((Pincode) object).getLastModifiedByName());

            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM);
            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(savePincodeSharedDataMessage,savePincodeSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Area.class)) {
            //All data of Area entity while saving
            SaveAreaSharedDataMessage saveAreaSharedDataMessage = new SaveAreaSharedDataMessage();
            saveAreaSharedDataMessage.setId(((Area) object).getId());
            saveAreaSharedDataMessage.setName(((Area) object).getName());
            saveAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            saveAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            saveAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            saveAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            saveAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            saveAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            saveAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            saveAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            saveAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());
            saveAreaSharedDataMessage.setCreatedByName(((Area) object).getCreatedByName());
            saveAreaSharedDataMessage.setLastModifiedByName(((Area) object).getLastModifiedByName());
            saveAreaSharedDataMessage.getPincode().setUpdatedate(null);
            saveAreaSharedDataMessage.getPincode().setCreatedate(null);

            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveAreaSharedDataMessage,saveAreaSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)) {
            //All data of BusinessUnit entity while saving
            SaveBusinessUnitSharedDataMessage saveBusinessUnitSharedDataMessge = new SaveBusinessUnitSharedDataMessage();
            saveBusinessUnitSharedDataMessge.setId(((BusinessUnit) object).getId());
            saveBusinessUnitSharedDataMessge.setBuname(((BusinessUnit) object).getBuname());
            saveBusinessUnitSharedDataMessge.setBucode(((BusinessUnit) object).getBucode());
            saveBusinessUnitSharedDataMessge.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            saveBusinessUnitSharedDataMessge.setMvnoId(((BusinessUnit) object).getMvnoId());
            saveBusinessUnitSharedDataMessge.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            saveBusinessUnitSharedDataMessge.setStatus(((BusinessUnit) object).getStatus());
            saveBusinessUnitSharedDataMessge.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            saveBusinessUnitSharedDataMessge.setCreatedById(((BusinessUnit) object).getCreatedById());
            saveBusinessUnitSharedDataMessge.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());
            saveBusinessUnitSharedDataMessge.setCreatedByName(((BusinessUnit) object).getCreatedByName());
            saveBusinessUnitSharedDataMessge.setLastModifiedByName(((BusinessUnit) object).getLastModifiedByName());
            if(saveBusinessUnitSharedDataMessge.getInvestmentCodeid()!=null && !saveBusinessUnitSharedDataMessge.getInvestmentCodeid().isEmpty())
            {
                saveBusinessUnitSharedDataMessge.getInvestmentCodeid().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }


            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_RADIUS);
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_NOTIFICATION);

            kafkaMessageSender.send(new KafkaMessageData(saveBusinessUnitSharedDataMessge,saveBusinessUnitSharedDataMessge.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Branch.class)) {
            //All this data for branch saving
            SaveBranchSharedDataMessage saveBranchSharedDataMessage = new SaveBranchSharedDataMessage();
            saveBranchSharedDataMessage.setId(((Branch) object).getId());
            saveBranchSharedDataMessage.setName(((Branch) object).getName());
            saveBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            saveBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            saveBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            saveBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            Set<ServiceArea> serviceAreaList=new HashSet<>();
            if (!CollectionUtils.isEmpty(((Branch) object).getServiceAreaNameList())) {
                for (ServiceArea area : ((Branch) object).getServiceAreaNameList()) {
                    ServiceArea newServicerea = new ServiceArea(area);
                    newServicerea.getPincodeList().stream().forEach(x->{
                        x.setCreatedate(null);
                        x.setUpdatedate(null);
                    });
                    newServicerea.setCreatedate(null);
                    newServicerea.setUpdatedate(null);
                    serviceAreaList.add(newServicerea);
                }
            }
            saveBranchSharedDataMessage.setServiceAreaNameList(serviceAreaList);
            saveBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            saveBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            saveBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            saveBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            saveBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            saveBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            saveBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());
            saveBranchSharedDataMessage.setCreatedByName(((Branch) object).getCreatedByName());
            saveBranchSharedDataMessage.setLastModifiedByName(((Branch) object).getLastModifiedByName());

            //messageSender.send(saveBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_PARTNER_MICROSERVICE);
            //messageSender.send(saveBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(saveBranchSharedDataMessage,saveBranchSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Teams.class)) {
            //All this data for teams saving
            SaveTeamsSharedSharedData saveTeamsSharedSharedData = new SaveTeamsSharedSharedData();
            saveTeamsSharedSharedData.setId(((Teams) object).getId());
            saveTeamsSharedSharedData.setName(((Teams) object).getName());
            saveTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            saveTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            saveTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            saveTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            saveTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            saveTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            saveTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            saveTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            saveTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());
            if (((Teams) object).getTeamType() != null) {
                saveTeamsSharedSharedData.setTeamType(((Teams) object).getTeamType());
            }

            if(saveTeamsSharedSharedData.getStaffUser()!=null && !saveTeamsSharedSharedData.getStaffUser().isEmpty()) {
                saveTeamsSharedSharedData.getStaffUser().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }
            if(saveTeamsSharedSharedData.getParentTeams()!=null) {
                saveTeamsSharedSharedData.getParentTeams().setUpdatedate(null);
                saveTeamsSharedSharedData.getParentTeams().setCreatedate(null);
            }

            //messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS);
            //messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(saveTeamsSharedSharedData,saveTeamsSharedSharedData.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(CustAccountProfile.class)) {
//            Data related cust account profile entity while saving
            SaveCustAccountProfileSharedDataMessage saveCustAccountProfileMessage = new SaveCustAccountProfileSharedDataMessage();
            saveCustAccountProfileMessage.setId(((CustAccountProfile) object).getId());
            saveCustAccountProfileMessage.setName(((CustAccountProfile)object).getName());
            saveCustAccountProfileMessage.setPrefix(((CustAccountProfile)object).getPrefix());
            saveCustAccountProfileMessage.setType(((CustAccountProfile)object).getType());
            saveCustAccountProfileMessage.setStartFrom(((CustAccountProfile)object).getStartFrom());
            saveCustAccountProfileMessage.setYear(((CustAccountProfile)object).isYear());
            saveCustAccountProfileMessage.setMonth(((CustAccountProfile)object).isMonth());
            saveCustAccountProfileMessage.setDay(((CustAccountProfile)object).isDay());
            saveCustAccountProfileMessage.setStatus(((CustAccountProfile)object).getStatus());
            saveCustAccountProfileMessage.setDelete(((CustAccountProfile)object).getIsDelete());
            saveCustAccountProfileMessage.setMvnoId(((CustAccountProfile)object).getMvnoId());
            saveCustAccountProfileMessage.setCreatedByName(((CustAccountProfile)object).getCreatedByName());
            saveCustAccountProfileMessage.setLastModifiedByName(((CustAccountProfile)object).getLastModifiedByName());
            saveCustAccountProfileMessage.setCreatedById(((CustAccountProfile)object).getCreatedById());
            saveCustAccountProfileMessage.setLastModifiedById(((CustAccountProfile)object).getLastModifiedById());

            kafkaMessageSender.send(new KafkaMessageData(saveCustAccountProfileMessage,saveCustAccountProfileMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {
            //All data of MVNO entity while saving
            SaveMvnoSharedDataMessage saveMvnoSharedDataMessage = new SaveMvnoSharedDataMessage();
            saveMvnoSharedDataMessage.setAddress(((Mvno) object).getAddress());
            saveMvnoSharedDataMessage.setFullName(((Mvno) object).getFullName());
            saveMvnoSharedDataMessage.setId(((Mvno) object).getId());
            saveMvnoSharedDataMessage.setName(((Mvno) object).getName());
            saveMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            saveMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            saveMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            saveMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            saveMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            saveMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            saveMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            saveMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            saveMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            saveMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            saveMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            saveMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            saveMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());
            saveMvnoSharedDataMessage.setLogo_file_name(((Mvno) object).getLogo_file_name());
            saveMvnoSharedDataMessage.setProfileImage(((Mvno) object).getProfileImage());
            saveMvnoSharedDataMessage.setMvnoPaymentDueDays(((Mvno) object).getMvnoPaymentDueDays() != null ? ((Mvno) object).getMvnoPaymentDueDays() : 10);
            saveMvnoSharedDataMessage.setIspBillDay(((Mvno) object).getIspBillDay() != null ? ((Mvno) object).getIspBillDay() : 1);
            saveMvnoSharedDataMessage.setIspCommissionPercentage(((Mvno) object).getIspCommissionPercentage() != null ? ((Mvno) object).getIspCommissionPercentage() : 100);
            saveMvnoSharedDataMessage.setBillType(((Mvno) object).getBillType());
            saveMvnoSharedDataMessage.setClientId(((Mvno) object).getClientId()!= null ? ((Mvno) object).getClientId() : " ");
            CustAccountProfile custAccountProfile =  ((Mvno)object).getCustAccountProfile();
            saveMvnoSharedDataMessage.setProfileId(custAccountProfile.getId());
            saveMvnoSharedDataMessage.setThreshold(((Mvno) object).getThreshold());
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS);
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE);
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_NOTIFICATION_MICROSERVICE);
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_RADIUS); new added

            kafkaMessageSender.send(new KafkaMessageData(saveMvnoSharedDataMessage,saveMvnoSharedDataMessage.getClass().getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {
            //All data of Role entity while saving
            SaveRoleSharedDataMessage saveRoleSharedDataMessage = new SaveRoleSharedDataMessage();
            saveRoleSharedDataMessage.setId(((Role) object).getId());
            saveRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            saveRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            saveRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
            saveRoleSharedDataMessage.setAclEntry(((Role) object).getRoleAclEntry());
            saveRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            saveRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            saveRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            saveRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            saveRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());

            //messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS);
            //messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_REVENUE);

//            kafkaMessageSender.send(new KafkaMessageData(saveRoleSharedDataMessage,saveRoleSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {
            //All data of Staff user entity while saving
            SaveStaffUserSharedDataMessage staffUserSharedDataMessage = new SaveStaffUserSharedDataMessage();
            staffUserSharedDataMessage.setId(((StaffUser) object).getId());
            staffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            staffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            staffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            staffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            staffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if (((StaffUser) object).getLast_login_time() != null) {
                staffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                staffUserSharedDataMessage.setLast_login_time(null);
            }
            staffUserSharedDataMessage.setTacacsAccessLevelGroup(((StaffUser) object).getTacacsAccessLevelGroup());
            staffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            staffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            staffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            staffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            staffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            staffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            staffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            staffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            staffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            staffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            staffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());
            if (((StaffUser) object).getStaffUserparent() != null) {
                staffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            staffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            staffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());
            SaveStaffUserSharedDataMessage staffUserCreateData = new SaveStaffUserSharedDataMessage((StaffUser) object);
            SaveStaffUserSharedDataMessage staffUserCreateDataForTicket = new SaveStaffUserSharedDataMessage((StaffUser) object, "");

            //messageSender.send(staffUserCreateDataForTicket, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(staffUserCreateData, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(staffUserCreateData, SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS);
            //messageSender.send(staffUserCreateData, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(staffUserCreateDataForTicket,staffUserCreateDataForTicket.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanService.class)) {
            //All data of Services entity while saving
            SaveServicesSharedDataMessage saveServicesSharedDataMessage = new SaveServicesSharedDataMessage();
            saveServicesSharedDataMessage.setId(((PlanService) object).getId());
            saveServicesSharedDataMessage.setName(((PlanService) object).getName());
            saveServicesSharedDataMessage.setIcname(((PlanService) object).getIcname());
            saveServicesSharedDataMessage.setIccode(((PlanService) object).getIccode());
            saveServicesSharedDataMessage.setMvnoId(((PlanService) object).getMvnoId());
            saveServicesSharedDataMessage.setBuId(((PlanService) object).getBuId());
            saveServicesSharedDataMessage.setIsQoSV(((PlanService) object).getIsQoSV());
            saveServicesSharedDataMessage.setExpiry(((PlanService) object).getExpiry());
            saveServicesSharedDataMessage.setLedgerId(((PlanService) object).getLedgerId());
            saveServicesSharedDataMessage.setIs_dtv(((PlanService) object).getIs_dtv());
            saveServicesSharedDataMessage.setInvestmentid(((PlanService) object).getInvestmentid());
            saveServicesSharedDataMessage.setFeasibility(((PlanService) object).getFeasibility());
            saveServicesSharedDataMessage.setPoc(((PlanService) object).getPoc());
            saveServicesSharedDataMessage.setInstallation(((PlanService) object).getInstallation());
            saveServicesSharedDataMessage.setProvisioning(((PlanService) object).getProvisioning());
            saveServicesSharedDataMessage.setIsPriceEditable(((PlanService) object).getIsPriceEditable());
            saveServicesSharedDataMessage.setFeasibilityTeamId(((PlanService) object).getFeasibilityTeamId());
            saveServicesSharedDataMessage.setPocTeamId(((PlanService) object).getPocTeamId());
            saveServicesSharedDataMessage.setInstallationTeamId(((PlanService) object).getInstallationTeamId());
            saveServicesSharedDataMessage.setProvisioningTeamId(((PlanService) object).getProvisioningTeamId());
            saveServicesSharedDataMessage.setIsDeleted(((PlanService) object).getIsDeleted());
            saveServicesSharedDataMessage.setCreatedById(((PlanService) object).getCreatedById());
            saveServicesSharedDataMessage.setLastModifiedById(((PlanService) object).getLastModifiedById());

            //messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET);

            kafkaMessageSender.send(new KafkaMessageData(saveServicesSharedDataMessage,saveServicesSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            //All data of Region entity while saving
            SaveRegionSharedDataMessage saveRegionSharedDataMessage = new SaveRegionSharedDataMessage();
            saveRegionSharedDataMessage.setId(((Region) object).getId());
            saveRegionSharedDataMessage.setRname(((Region) object).getRname());
            List<Branch> branches=new ArrayList<>();
            if (((Region) object).getBranchidList() != null) {
                for (Branch branch : ((Region) object).getBranchidList()) {
                    branches.add(new Branch(branch));
                }
            }
            saveRegionSharedDataMessage.setBranchidList(branches);
            saveRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            saveRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            saveRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            saveRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            saveRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            saveRegionSharedDataMessage.setCreatedByName(((Region) object).getCreatedByName());
            saveRegionSharedDataMessage.setLastModifiedByName(((Region) object).getLastModifiedByName());

            //messageSender.send(saveRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveRegionSharedDataMessage,SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveRegionSharedDataMessage,saveRegionSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {
            //All data of BusinessVerticals entity while saving
            SaveBusinessVerticalSharedDataMessage saveBusinessVerticalsSharedDataMessage = new SaveBusinessVerticalSharedDataMessage();
            saveBusinessVerticalsSharedDataMessage.setId(((BusinessVerticals) object).getId());
            saveBusinessVerticalsSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            List<Region> regionList=new ArrayList<>();
            if (!CollectionUtils.isEmpty(((BusinessVerticals) object).getBuregionidList())) {
                for (Region region : ((BusinessVerticals) object).getBuregionidList()) {
                    regionList.add(new Region(region));
                }
            }
            saveBusinessVerticalsSharedDataMessage.setBuregionidList(regionList);
            saveBusinessVerticalsSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            saveBusinessVerticalsSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            saveBusinessVerticalsSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            saveBusinessVerticalsSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            saveBusinessVerticalsSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());
            saveBusinessVerticalsSharedDataMessage.setCreatedByName(((BusinessVerticals) object).getCreatedByName());
            saveBusinessVerticalsSharedDataMessage.setLastModifiedByName(((BusinessVerticals) object).getLastModifiedByName());

            //messageSender.send(saveBusinessVerticalsSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_VERTICALS_DATA_CREATE_DATA_SHARE_PARTNER);
            //messageSender.send(saveBusinessVerticalsSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveBusinessVerticalsSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveBusinessVerticalsSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveBusinessVerticalsSharedDataMessage,saveBusinessVerticalsSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {
            SaveClientServMessge clientServMessge = new SaveClientServMessge();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());

            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,clientServMessge.getClass().getSimpleName(),KafkaConstant.CREATE_SERVICE_CONFIG));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(SubBusinessUnit.class)) {
            SaveSubBusinessUnitSharedDataMessage message = new SaveSubBusinessUnitSharedDataMessage();
            message.setId(((SubBusinessUnit) object).getId());
            message.setMvnoId(((SubBusinessUnit) object).getMvnoId());
            message.setStatus(((SubBusinessUnit) object).getStatus());
            message.setSubbucode(((SubBusinessUnit) object).getSubbucode());
            message.setBusinessunitid(((SubBusinessUnit) object).getBusinessunitid());
            message.setIsDeleted(((SubBusinessUnit) object).getIsDeleted());
            message.setSubbuname(((SubBusinessUnit) object).getSubbuname());
            message.setCreatedById(((SubBusinessUnit) object).getCreatedById());
            message.setLastModifiedById(((SubBusinessUnit) object).getLastModifiedById());
            message.setCreatedByName(((SubBusinessUnit) object).getCreatedByName());
            message.setLastModifiedByName(((SubBusinessUnit) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(SubBusinessVertical.class)){
            SaveSubBusinessVerticalsSharedDataMessage message = new SaveSubBusinessVerticalsSharedDataMessage();
            message.setId(((SubBusinessVertical) object).getId());
            message.setSbvname(((SubBusinessVertical) object).getSbvname());
            message.setBusinessVerticalId(((SubBusinessVertical) object).getBusinessVerticals().getId().intValue());
            message.setStatus(((SubBusinessVertical) object).getStatus());
            message.setMvnoId(((SubBusinessVertical) object).getMvnoId());
            message.setIsDeleted(((SubBusinessVertical) object).getIsDeleted());
            message.setCreatedById(((SubBusinessVertical) object).getCreatedById());
            message.setLastModifiedById(((SubBusinessVertical) object).getLastModifiedById());
            message.setCreatedByName(((SubBusinessVertical) object).getCreatedByName());
            message.setLastModifiedByName(((SubBusinessVertical) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(InvestmentCode.class)){
            SaveInvestmentCodeSharedDataMessage message = new SaveInvestmentCodeSharedDataMessage();
            message.setIccode(((InvestmentCode) object).getIccode());
            message.setIcname(((InvestmentCode) object).getIcname());
            message.setStatus(((InvestmentCode) object).getStatus());
            message.setMvnoId(((InvestmentCode) object).getMvnoId());
            message.setIsDeleted(((InvestmentCode) object).getIsDeleted());
            message.setId(((InvestmentCode) object).getId());
            message.setCreatedById(((InvestmentCode) object).getCreatedById());
            message.setLastModifiedById(((InvestmentCode) object).getLastModifiedById());
            message.setCreatedByName(((InvestmentCode) object).getCreatedByName());
            message.setLastModifiedByName(((InvestmentCode) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM);
            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(BankManagementDTO.class)){
            SaveBankManagementSharedDataMessage message = new SaveBankManagementSharedDataMessage();
            message.setId(((BankManagementDTO) object).getId());
            message.setBankcode(((BankManagementDTO) object).getBankcode());
            message.setAccountnum(((BankManagementDTO) object).getAccountnum());
            message.setBankcode(((BankManagementDTO) object).getBankcode());
            message.setBankname(((BankManagementDTO) object).getBankname());
            message.setBankholdername(((BankManagementDTO) object).getBankholdername());
            message.setIfsccode(((BankManagementDTO) object).getIfsccode());
            message.setIsDeleted(((BankManagementDTO) object).getIsDeleted());
            message.setStatus(((BankManagementDTO) object).getStatus());
            message.setMvnoId(((BankManagementDTO) object).getMvnoId());
            message.setCreatedById(((BankManagementDTO) object).getCreatedById());
            message.setLastModifiedById(((BankManagementDTO) object).getLastModifiedById());
            message.setCreatedByName(((BankManagementDTO) object).getCreatedByName());
            message.setLastModifiedByName(((BankManagementDTO) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM);
            //messageSender.send(message,SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(DepartmentPojo.class)){
            SaveDepartmentSharedDataMessage message = new SaveDepartmentSharedDataMessage();
            message.setId(((DepartmentPojo) object).getId());
            message.setIsDelete(((DepartmentPojo) object).getIsDelete());
            message.setStatus(((DepartmentPojo) object).getStatus());
            message.setName(((DepartmentPojo) object).getName());
            message.setMvnoId(((DepartmentPojo) object).getMvnoId());
            message.setPlanIds(((DepartmentPojo) object).getPlanIds());

            //messageSender.send(message,SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object)&& object.getClass().equals(SubAreaDTO.class)){
            SubAreaMessage subAreaMessage=new SubAreaMessage();
            subAreaMessage.setId(((SubAreaDTO) object).getId());
            subAreaMessage.setStatus(((SubAreaDTO) object).getStatus());
            subAreaMessage.setIsDeleted(((SubAreaDTO) object).getIsDeleted());
            subAreaMessage.setCityId(((SubAreaDTO) object).getCityId());
            subAreaMessage.setAreaId( ((SubAreaDTO) object).getAreaId());
            subAreaMessage.setName(((SubAreaDTO) object).getName());
            subAreaMessage.setMvnoId(((SubAreaDTO) object).getMvnoId());
            subAreaMessage.setStateId(((SubAreaDTO) object).getStateId());
            subAreaMessage.setCountryId(((SubAreaDTO) object).getCountryId());
            subAreaMessage.setBuId(((SubAreaDTO) object).getBuId());
            kafkaMessageSender.send(new KafkaMessageData(subAreaMessage,subAreaMessage.getClass().getSimpleName()));
        }

        else if(Objects.nonNull(object)&& object.getClass().equals(BuildingManagementDTO.class)){
            BuildingMgmtMessage buildingMgmtMessage = new BuildingMgmtMessage();
            buildingMgmtMessage.setBuildingMgmtId(((BuildingManagementDTO) object).getBuildingMgmtId());
            buildingMgmtMessage.setBuildingName(((BuildingManagementDTO) object).getBuildingName());
            buildingMgmtMessage.setBuid(((BuildingManagementDTO) object).getBuid());
            buildingMgmtMessage.setMvnoId(((BuildingManagementDTO) object).getMvnoId());
            buildingMgmtMessage.setAreaId(((BuildingManagementDTO) object).getAreaId());
            buildingMgmtMessage.setPincodeId(((BuildingManagementDTO) object).getPincodeId());
            buildingMgmtMessage.setSubAreaId(((BuildingManagementDTO) object).getSubAreaId());
            buildingMgmtMessage.setBuildingMappings(((BuildingManagementDTO) object).getBuildingMappings());
            buildingMgmtMessage.setIsDeleted(((BuildingManagementDTO) object).getIsDeleted());
            buildingMgmtMessage.setBuildingType(((BuildingManagementDTO) object).getBuildingType());
            kafkaMessageSender.send(new KafkaMessageData(buildingMgmtMessage,buildingMgmtMessage.getClass().getSimpleName(),KafkaConstant.BUILDING_MGMT_SAVE));
        }
    }


    //UPDATE ENTITY COMMON SERVICE
    public void updateEntityDataForAllMicroService(Object object) {

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {
            //All data of State entity while updating
            UpdateStateSharedDataMessage updateStateSharedDataMessage = new UpdateStateSharedDataMessage();
            updateStateSharedDataMessage.setId(((State) object).getId());
            updateStateSharedDataMessage.setStatus(((State) object).getStatus());
            updateStateSharedDataMessage.setCountry(((State) object).getCountry());
            updateStateSharedDataMessage.setName(((State) object).getName());
            updateStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            updateStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            updateStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            updateStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            updateStateSharedDataMessage.setCreatedByName(((State) object).getCreatedByName());
            updateStateSharedDataMessage.setLastModifiedByName(((State) object).getLastModifiedByName());
            updateStateSharedDataMessage.getCountry().setCreatedate(null);
            updateStateSharedDataMessage.getCountry().setUpdatedate(null);

            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateStateSharedDataMessage,updateStateSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {
            //All data of Country entity while updating
            UpdateCountrySharedDataMessage updateCountrySharedDataMessage = new UpdateCountrySharedDataMessage();
            updateCountrySharedDataMessage.setId(((Country) object).getId());
            updateCountrySharedDataMessage.setName(((Country) object).getName());
            updateCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            updateCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            updateCountrySharedDataMessage.setIsDelete(((Country) object).getIsDelete());
            updateCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            updateCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());

            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCountrySharedDataMessage,SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_PARTNER_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCountrySharedDataMessage,updateCountrySharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(City.class)) {
            //All data of City entity while updating
            UpdateCitySharedDataMessage updateCitySharedDataMessage = new UpdateCitySharedDataMessage();
            updateCitySharedDataMessage.setId(((City) object).getId());
            updateCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            updateCitySharedDataMessage.setStatus(((City) object).getStatus());
            if(((City) object).getState() != null)
                updateCitySharedDataMessage.setState(new State(((City) object).getState()));
            updateCitySharedDataMessage.setName(((City) object).getName());
            updateCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            updateCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            updateCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            updateCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());
            updateCitySharedDataMessage.setCreatedByName(((City) object).getCreatedByName());
            updateCitySharedDataMessage.setLastModifiedByName(((City) object).getLastModifiedByName());

            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateCitySharedDataMessage,updateCitySharedDataMessage.getClass().getSimpleName()));

        }
            else if (Objects.nonNull(object) && object.getClass().equals(CustAccountProfile.class)) {
            //All data of CustAccountProfile entity while updating
            UpdateCustAccountProfileSharedDataMessage updateCustAccountProfileMessage = new UpdateCustAccountProfileSharedDataMessage();
            updateCustAccountProfileMessage.setId(((CustAccountProfile) object).getId());
            updateCustAccountProfileMessage.setName(((CustAccountProfile)object).getName());
            updateCustAccountProfileMessage.setPrefix(((CustAccountProfile)object).getPrefix());
            updateCustAccountProfileMessage.setType(((CustAccountProfile)object).getType());
            updateCustAccountProfileMessage.setStartFrom(((CustAccountProfile)object).getStartFrom());
            updateCustAccountProfileMessage.setYear(((CustAccountProfile)object).isYear());
            updateCustAccountProfileMessage.setMonth(((CustAccountProfile)object).isMonth());
            updateCustAccountProfileMessage.setDay(((CustAccountProfile)object).isDay());
            updateCustAccountProfileMessage.setStatus(((CustAccountProfile)object).getStatus());
            updateCustAccountProfileMessage.setDelete(((CustAccountProfile)object).getIsDelete());
            updateCustAccountProfileMessage.setMvnoId(((CustAccountProfile)object).getMvnoId());
            updateCustAccountProfileMessage.setCreatedByName(((CustAccountProfile)object).getCreatedByName());
            updateCustAccountProfileMessage.setLastModifiedByName(((CustAccountProfile)object).getLastModifiedByName());
            updateCustAccountProfileMessage.setCreatedById(((CustAccountProfile)object).getCreatedById());
            updateCustAccountProfileMessage.setLastModifiedById(((CustAccountProfile)object).getLastModifiedById());

            kafkaMessageSender.send(new KafkaMessageData(updateCustAccountProfileMessage,updateCustAccountProfileMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {
            //All data of MVNO entity while updating
            UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage = new UpdateMvnoSharedDataMessage();
            updateMvnoSharedDataMessage.setId(((Mvno) object).getId());
            updateMvnoSharedDataMessage.setName(((Mvno) object).getName());
            updateMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            updateMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            updateMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            updateMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            updateMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            updateMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            updateMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            updateMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            updateMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            updateMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            updateMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            updateMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            updateMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());
            updateMvnoSharedDataMessage.setLogo_file_name(((Mvno) object).getLogo_file_name());
            updateMvnoSharedDataMessage.setProfileImage(((Mvno) object).getProfileImage());
            updateMvnoSharedDataMessage.setMvnoPaymentDueDays(((Mvno) object).getMvnoPaymentDueDays() != null ? ((Mvno) object).getMvnoPaymentDueDays() : 10);
            updateMvnoSharedDataMessage.setAddress(((Mvno) object).getAddress());
            updateMvnoSharedDataMessage.setIspBillDay(((Mvno) object).getIspBillDay() != null ? ((Mvno) object).getIspBillDay() : 1);
            updateMvnoSharedDataMessage.setBillType(((Mvno) object).getBillType());
            updateMvnoSharedDataMessage.setIspCommissionPercentage(((Mvno) object).getIspCommissionPercentage() != null ? ((Mvno) object).getIspCommissionPercentage() : 100);
            updateMvnoSharedDataMessage.setClientId(((Mvno) object).getClientId()!=null? ((Mvno) object).getClientId() : "");
            CustAccountProfile custAccountProfile = ((Mvno)object).getCustAccountProfile();
            updateMvnoSharedDataMessage.setProfileId(custAccountProfile.getId());
            updateMvnoSharedDataMessage.setFullName(((Mvno) object).getFullName());
            updateMvnoSharedDataMessage.setThreshold(((Mvno) object).getThreshold());
            // All the messages from microservies are to be sent from here
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET);
//            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDAT_MVNO_COMMON_APIGW_TO_REVENUE);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_PARTNER);

            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDAT_MVNO_COMMON_APIGW_TO_REVENUE);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS); new added

           kafkaMessageSender.send(new KafkaMessageData(updateMvnoSharedDataMessage,updateMvnoSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {
            //All data of Role entity while updating
            UpdateRoleSharedDataMessage updateRoleSharedDataMessage = new UpdateRoleSharedDataMessage();
            updateRoleSharedDataMessage.setId(((Role) object).getId());
            updateRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            updateRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            updateRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
            updateRoleSharedDataMessage.setAclEntry(((Role) object).getRoleAclEntry());
            updateRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            updateRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            updateRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            updateRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            updateRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());

            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_REVENUE);

//            kafkaMessageSender.send(new KafkaMessageData(updateRoleSharedDataMessage,updateRoleSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {
            //All data of Staff user entity while updating
            UpdateStaffUserSharedDataMessage updateStaffUserSharedDataMessage = new UpdateStaffUserSharedDataMessage();
            updateStaffUserSharedDataMessage.setId(((StaffUser) object).getId());
            updateStaffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            updateStaffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            updateStaffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            updateStaffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            updateStaffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if (((StaffUser) object).getLast_login_time() != null) {
                updateStaffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                updateStaffUserSharedDataMessage.setLast_login_time(null);
            }
            updateStaffUserSharedDataMessage.setTacacsAccessLevelGroup(((StaffUser) object).getTacacsAccessLevelGroup());
            updateStaffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            updateStaffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            updateStaffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            updateStaffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            updateStaffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            updateStaffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            updateStaffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            updateStaffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            updateStaffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            updateStaffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            updateStaffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());
            if (((StaffUser) object).getStaffUserparent() != null) {
                updateStaffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            updateStaffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            updateStaffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());

            UpdateStaffUserSharedDataMessage staffUserUpdateData = new UpdateStaffUserSharedDataMessage((StaffUser) object);
            UpdateStaffUserSharedDataMessage updateStaffUserMessage = new UpdateStaffUserSharedDataMessage((StaffUser) object, "");

            //messageSender.send(updateStaffUserMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(staffUserUpdateData, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(staffUserUpdateData, SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS);

            kafkaMessageSender.send(new KafkaMessageData(updateStaffUserMessage,updateStaffUserMessage.getClass().getSimpleName()));
        }
         else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)) {
             //All data of City entity while updating
            UpdatePincodeSharedDataMessage updatePincodeSharedDataMessage = new UpdatePincodeSharedDataMessage();
            updatePincodeSharedDataMessage.setId(((Pincode) object).getId());
            updatePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            updatePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            updatePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            updatePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            updatePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            updatePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            updatePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            updatePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            updatePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());
            updatePincodeSharedDataMessage.setCreatedByName(((Pincode) object).getCreatedByName());
            updatePincodeSharedDataMessage.setLastModifiedByName(((Pincode) object).getLastModifiedByName());

            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePincodeSharedDataMessage,updatePincodeSharedDataMessage.getClass().getSimpleName()));
        }
         else if (Objects.nonNull(object) && object.getClass().equals(Area.class)) {
             //All data of Area entity while updating
            UpdateAreaSharedDataMessage updateAreaSharedDataMessage = new UpdateAreaSharedDataMessage();
            updateAreaSharedDataMessage.setId(((Area) object).getId());
            updateAreaSharedDataMessage.setName(((Area) object).getName());
            updateAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            updateAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            updateAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            updateAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            updateAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            updateAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            updateAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            updateAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            updateAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());
            updateAreaSharedDataMessage.setCreatedByName(((Area) object).getCreatedByName());
            updateAreaSharedDataMessage.setLastModifiedByName(((Area) object).getLastModifiedByName());
            if(updateAreaSharedDataMessage.getPincode()!=null)
            {
                updateAreaSharedDataMessage.getPincode().setCreatedate(null);
                updateAreaSharedDataMessage.getPincode().setUpdatedate(null);
            }

            //messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateAreaSharedDataMessage,updateAreaSharedDataMessage.getClass().getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(ServiceArea.class)){
            //All data of ServiceArea entity while updating
            UpdateServiceAreaSharedDataMessage updateServiceAreaSharedDataMessage = new UpdateServiceAreaSharedDataMessage();
            updateServiceAreaSharedDataMessage.setId(((ServiceArea) object).getId());
            updateServiceAreaSharedDataMessage.setAreaId(((ServiceArea) object).getAreaId());
            updateServiceAreaSharedDataMessage.setCityid(((ServiceArea) object).getCityid());
            updateServiceAreaSharedDataMessage.setLongitude(((ServiceArea) object).getLongitude());
            updateServiceAreaSharedDataMessage.setLatitude(((ServiceArea) object).getLatitude());
            updateServiceAreaSharedDataMessage.setName(((ServiceArea) object).getName());
            updateServiceAreaSharedDataMessage.setIsDeleted(((ServiceArea) object).getIsDeleted());
            updateServiceAreaSharedDataMessage.setPincodeList(((ServiceArea) object).getPincodeList());
            updateServiceAreaSharedDataMessage.setMvnoId(((ServiceArea) object).getMvnoId());
            updateServiceAreaSharedDataMessage.setStatus(((ServiceArea) object).getStatus());
            updateServiceAreaSharedDataMessage.setUpdatedById(((ServiceArea) object).getLastModifiedById());
            updateServiceAreaSharedDataMessage.setCreatedById(((ServiceArea) object).getCreatedById());
            updateServiceAreaSharedDataMessage.setCreatedByName(((ServiceArea) object).getCreatedByName());
            updateServiceAreaSharedDataMessage.setLastModifiedByName(((ServiceArea) object).getLastModifiedByName());
            updateServiceAreaSharedDataMessage.setSiteName(((ServiceArea) object).getSiteName());
            if(updateServiceAreaSharedDataMessage.getPincodeList()!=null && !updateServiceAreaSharedDataMessage.getPincodeList().isEmpty())
            {
                updateServiceAreaSharedDataMessage.getPincodeList().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            //messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICEAREA_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_PARTNER);

           kafkaMessageSender.send(new KafkaMessageData(updateServiceAreaSharedDataMessage,updateServiceAreaSharedDataMessage.getClass().getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(Teams.class)){
            //All this data for branch saving
            UpdateTeamsSharedData updateTeamsSharedSharedData = new UpdateTeamsSharedData();
            updateTeamsSharedSharedData.setId(((Teams) object).getId());
            updateTeamsSharedSharedData.setName(((Teams) object).getName());
            updateTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            updateTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            updateTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            updateTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            updateTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            updateTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            updateTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            updateTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            updateTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());
            if (((Teams) object).getTeamType()!=null){
                updateTeamsSharedSharedData.setTeamType(((Teams) object).getTeamType());
            }

            //messageSender.send(updateTeamsSharedSharedData,SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(updateTeamsSharedSharedData,updateTeamsSharedSharedData.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)){
            //All data of BusinessUnit entity while updating
            UpdateBusinessUnitSharedDataMessage updateBusinessUnitSharedDataMessage = new UpdateBusinessUnitSharedDataMessage();
            updateBusinessUnitSharedDataMessage.setId(((BusinessUnit) object).getId());
            updateBusinessUnitSharedDataMessage.setBuname(((BusinessUnit) object).getBuname());
            updateBusinessUnitSharedDataMessage.setBucode(((BusinessUnit) object).getBucode());
            updateBusinessUnitSharedDataMessage.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            updateBusinessUnitSharedDataMessage.setMvnoId(((BusinessUnit) object).getMvnoId());
            updateBusinessUnitSharedDataMessage.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            updateBusinessUnitSharedDataMessage.setStatus(((BusinessUnit) object).getStatus());
            updateBusinessUnitSharedDataMessage.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            updateBusinessUnitSharedDataMessage.setCreatedById(((BusinessUnit) object).getCreatedById());
            updateBusinessUnitSharedDataMessage.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());
            if(updateBusinessUnitSharedDataMessage.getInvestmentCodeid()!=null && !updateBusinessUnitSharedDataMessage.getInvestmentCodeid().isEmpty())
            {
                updateBusinessUnitSharedDataMessage.getInvestmentCodeid().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_RADIUS);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessUnitSharedDataMessage,updateBusinessUnitSharedDataMessage.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(Branch.class)){
            //All this data for branch updating
            UpdateBranchSharedData updateBranchSharedDataMessage = new UpdateBranchSharedData();
            updateBranchSharedDataMessage.setId(((Branch) object).getId());
            updateBranchSharedDataMessage.setName(((Branch) object).getName());
            updateBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            updateBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            Set<ServiceArea> serviceAreaList=new HashSet<>();
            if (!CollectionUtils.isEmpty(((Branch) object).getServiceAreaNameList())) {
                for (ServiceArea area : ((Branch) object).getServiceAreaNameList()) {
                    ServiceArea newServicerea = new ServiceArea(area);
                    serviceAreaList.add(newServicerea);
                }
            }
            updateBranchSharedDataMessage.setServiceAreaNameList(serviceAreaList);
            updateBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            updateBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            updateBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            updateBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            updateBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());
            updateBranchSharedDataMessage.setCreatedByName(((Branch) object).getCreatedByName());
            updateBranchSharedDataMessage.setLastModifiedByName(((Branch) object).getLastModifiedByName());

            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBranchSharedDataMessage,updateBranchSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            // All data of Region entity while saving
            UpdateRegionSharedDataMessage updateRegionSharedDataMessage = new UpdateRegionSharedDataMessage();
            updateRegionSharedDataMessage.setId(((Region) object).getId());
            updateRegionSharedDataMessage.setRname(((Region) object).getRname());
            List<Branch> branches=new ArrayList<>();
            if (((Region) object).getBranchidList() != null) {
                for (Branch branch : ((Region) object).getBranchidList()) {
                    branches.add(new Branch(branch));
                }
            }
            updateRegionSharedDataMessage.setBranchidList(branches);
            updateRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            updateRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            updateRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            updateRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            updateRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            updateRegionSharedDataMessage.setCreatedByName(((Region) object).getCreatedByName());
            updateRegionSharedDataMessage.setLastModifiedByName(((Region) object).getLastModifiedByName());

            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateRegionSharedDataMessage,updateRegionSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {
            //All data of BusinessVerticals entity while saving
            UpdateBusinessVerticalSharedDataMessage updateBusinessVerticalSharedDataMessage = new UpdateBusinessVerticalSharedDataMessage();
            updateBusinessVerticalSharedDataMessage.setId(((BusinessVerticals) object).getId());
            updateBusinessVerticalSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            List<Region> regionList=new ArrayList<>();
            if (!CollectionUtils.isEmpty(((BusinessVerticals) object).getBuregionidList())) {
                for (Region region : ((BusinessVerticals) object).getBuregionidList()) {
                    regionList.add(new Region(region));
                }
            }
            updateBusinessVerticalSharedDataMessage.setBuregionidList(regionList);
            updateBusinessVerticalSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            updateBusinessVerticalSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            updateBusinessVerticalSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            updateBusinessVerticalSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            updateBusinessVerticalSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());
            updateBusinessVerticalSharedDataMessage.setCreatedByName(((BusinessVerticals) object).getCreatedByName());
            updateBusinessVerticalSharedDataMessage.setLastModifiedByName(((BusinessVerticals) object).getLastModifiedByName());

            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessVerticalSharedDataMessage,updateBusinessVerticalSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {
            UpdateClientServMessage clientServMessge = new UpdateClientServMessage();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());

            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM);
            //messageSender.send(clientServMessge, SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,clientServMessge.getClass().getSimpleName(),KafkaConstant.UPDATE_SERVICE_CONFIG));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(SubBusinessUnit.class)) {
            UpdateSubBusinessUnitSharedDataMessage message = new UpdateSubBusinessUnitSharedDataMessage();
            message.setId(((SubBusinessUnit) object).getId());
            message.setMvnoId(((SubBusinessUnit) object).getMvnoId());
            message.setStatus(((SubBusinessUnit) object).getStatus());
            message.setSubbucode(((SubBusinessUnit) object).getSubbucode());
            message.setBusinessunitid(((SubBusinessUnit) object).getBusinessunitid());
            message.setIsDeleted(((SubBusinessUnit) object).getIsDeleted());
            message.setSubbuname(((SubBusinessUnit) object).getSubbuname());
            message.setCreatedById(((SubBusinessUnit) object).getLastModifiedById());
            message.setLastModifiedById(((SubBusinessUnit) object).getLastModifiedById());
            message.setCreatedByName(((SubBusinessUnit) object).getLastModifiedByName());
            message.setLastModifiedByName(((SubBusinessUnit) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(SubBusinessVertical.class)){
            UpdateSubBusinessVerticalsSharedDataMessage message = new UpdateSubBusinessVerticalsSharedDataMessage();
            message.setId(((SubBusinessVertical) object).getId());
            message.setSbvname(((SubBusinessVertical) object).getSbvname());
            message.setBusinessVerticalId(((SubBusinessVertical) object).getBusinessVerticals().getId().intValue());
            message.setStatus(((SubBusinessVertical) object).getStatus());
            message.setMvnoId(((SubBusinessVertical) object).getMvnoId());
            message.setIsDeleted(((SubBusinessVertical) object).getIsDeleted());
            message.setCreatedById(((SubBusinessVertical) object).getCreatedById());
            message.setLastModifiedById(((SubBusinessVertical) object).getLastModifiedById());
            message.setCreatedByName(((SubBusinessVertical) object).getCreatedByName());
            message.setLastModifiedByName(((SubBusinessVertical) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(InvestmentCode.class)){
            UpdateInvestmentCodeSharedDataMessage message = new UpdateInvestmentCodeSharedDataMessage();
            message.setIccode(((InvestmentCode) object).getIccode());
            message.setIcname(((InvestmentCode) object).getIcname());
            message.setStatus(((InvestmentCode) object).getStatus());
            message.setMvnoId(((InvestmentCode) object).getMvnoId());
            message.setIsDeleted(((InvestmentCode) object).getIsDeleted());
            message.setId(((InvestmentCode) object).getId());
            message.setCreatedById(((InvestmentCode) object).getCreatedById());
            message.setLastModifiedById(((InvestmentCode) object).getLastModifiedById());
            message.setCreatedByName(((InvestmentCode) object).getCreatedByName());
            message.setLastModifiedByName(((InvestmentCode) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(BankManagement.class)){
            UpdateBankManagementSharedDataMessage message = new UpdateBankManagementSharedDataMessage();
            message.setId(((BankManagement) object).getId());
            message.setBankcode(((BankManagement) object).getBankcode());
            message.setAccountnum(((BankManagement) object).getAccountnum());
            message.setBankcode(((BankManagement) object).getBankcode());
            message.setBankname(((BankManagement) object).getBankname());
            message.setBankholdername(((BankManagement) object).getBankholdername());
            message.setIfsccode(((BankManagement) object).getIfsccode());
            message.setIsDeleted(((BankManagement) object).getIsDeleted());
            message.setStatus(((BankManagement) object).getStatus());
            message.setCreatedById(((BankManagement) object).getCreatedById());
            message.setLastModifiedById(((BankManagement) object).getLastModifiedById());
            message.setCreatedByName(((BankManagement) object).getCreatedByName());
            message.setLastModifiedByName(((BankManagement) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(message,SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(DepartmentPojo.class)){
            SaveDepartmentSharedDataMessage message = new SaveDepartmentSharedDataMessage();
            message.setId(((DepartmentPojo) object).getId());
            message.setIsDelete(((DepartmentPojo) object).getIsDelete());
            message.setStatus(((DepartmentPojo) object).getStatus());
            message.setName(((DepartmentPojo) object).getName());
            message.setPlanIds(((DepartmentPojo) object).getPlanIds());

            //messageSender.send(message,SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object)&& object.getClass().equals(SubAreaDTO.class)){
            SubAreaMessage subAreaMessage=new SubAreaMessage();
            subAreaMessage.setId(((SubAreaDTO) object).getId());
            subAreaMessage.setStatus(((SubAreaDTO) object).getStatus());
            subAreaMessage.setIsDeleted(((SubAreaDTO) object).getIsDeleted());
            subAreaMessage.setCityId(((SubAreaDTO) object).getCityId());
            subAreaMessage.setName(((SubAreaDTO) object).getName());
            subAreaMessage.setMvnoId(((SubAreaDTO) object).getMvnoId());
            subAreaMessage.setStateId(((SubAreaDTO) object).getStateId());
            subAreaMessage.setCountryId(((SubAreaDTO) object).getCountryId());
            subAreaMessage.setBuId(((SubAreaDTO) object).getBuId());
            subAreaMessage.setAreaId(((SubAreaDTO) object).getAreaId());
            kafkaMessageSender.send(new KafkaMessageData(subAreaMessage,subAreaMessage.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object)&& object.getClass().equals(BuildingManagementDTO.class)){
            BuildingMgmtMessage buildingMgmtMessage = new BuildingMgmtMessage();
            buildingMgmtMessage.setBuildingMgmtId(((BuildingManagementDTO) object).getBuildingMgmtId());
            buildingMgmtMessage.setBuildingName(((BuildingManagementDTO) object).getBuildingName());
            buildingMgmtMessage.setBuid(((BuildingManagementDTO) object).getBuid());
            buildingMgmtMessage.setMvnoId(((BuildingManagementDTO) object).getMvnoId());
            buildingMgmtMessage.setAreaId(((BuildingManagementDTO) object).getAreaId());
            buildingMgmtMessage.setPincodeId(((BuildingManagementDTO) object).getPincodeId());
            buildingMgmtMessage.setSubAreaId(((BuildingManagementDTO) object).getSubAreaId());
            buildingMgmtMessage.setBuildingMappings(((BuildingManagementDTO) object).getBuildingMappings());
            buildingMgmtMessage.setIsDeleted(((BuildingManagementDTO) object).getIsDeleted());
            buildingMgmtMessage.setBuildingType(((BuildingManagementDTO) object).getBuildingType());
            kafkaMessageSender.send(new KafkaMessageData(buildingMgmtMessage,buildingMgmtMessage.getClass().getSimpleName(),KafkaConstant.BUILDING_MGMT_UPDATE));
        }
    }


    //DELETE ENTITY COMMON SERVICE
    public void deleteEntityDataForAllMicroService(Object object){

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {
            //All data of State entity while deleting
            UpdateStateSharedDataMessage updateStateSharedDataMessage = new UpdateStateSharedDataMessage();
            updateStateSharedDataMessage.setId(((State) object).getId());
            updateStateSharedDataMessage.setStatus(((State) object).getStatus());
            updateStateSharedDataMessage.setCountry(((State) object).getCountry());
            updateStateSharedDataMessage.setName(((State) object).getName());
            updateStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            updateStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            updateStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            updateStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            updateStateSharedDataMessage.getCountry().setCreatedate(null);
            updateStateSharedDataMessage.getCountry().setUpdatedate(null);

            //messageSender.send(updateStateSharedDataMessage,SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateStateSharedDataMessage,updateStateSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {
            //All data of Country entity while deleting
            UpdateCountrySharedDataMessage updateCountrySharedDataMessage = new UpdateCountrySharedDataMessage();
            updateCountrySharedDataMessage.setId(((Country) object).getId());
            updateCountrySharedDataMessage.setName(((Country) object).getName());
            updateCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            updateCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            updateCountrySharedDataMessage.setIsDelete(((Country) object).getIsDelete());
            updateCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            updateCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());

            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCountrySharedDataMessage,updateCountrySharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(City.class)){
            //All data of City entity while deleting
            UpdateCitySharedDataMessage updateCitySharedDataMessage = new UpdateCitySharedDataMessage();
            updateCitySharedDataMessage.setId(((City) object).getId());
            updateCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            updateCitySharedDataMessage.setStatus(((City) object).getStatus());
            updateCitySharedDataMessage.setState(((City) object).getState());
            updateCitySharedDataMessage.setName(((City) object).getName());
            updateCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            updateCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            updateCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            updateCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());
            if(updateCitySharedDataMessage.getState()!=null)
            {
                updateCitySharedDataMessage.getState().setCreatedate(null);
                updateCitySharedDataMessage.getState().setUpdatedate(null);
            }

            //messageSender.send(updateCitySharedDataMessage,SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM);
            //messageSender.send( updateCitySharedDataMessage,  SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateCitySharedDataMessage,updateCitySharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {
            //All data of MVNO entity while deleting
            UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage = new UpdateMvnoSharedDataMessage();
            updateMvnoSharedDataMessage.setId(((Mvno) object).getId());
            updateMvnoSharedDataMessage.setName(((Mvno) object).getName());
            updateMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            updateMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            updateMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            updateMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            updateMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            updateMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            updateMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            updateMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            updateMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            updateMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            updateMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            updateMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            updateMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());
            updateMvnoSharedDataMessage.setMvnoPaymentDueDays(((Mvno) object).getMvnoPaymentDueDays() != null ? ((Mvno) object).getMvnoPaymentDueDays() : 10);
            updateMvnoSharedDataMessage.setThreshold(((Mvno) object).getThreshold());
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateMvnoSharedDataMessage,updateMvnoSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {
            //All data of Role entity while deleting
            UpdateRoleSharedDataMessage updateRoleSharedDataMessage = new UpdateRoleSharedDataMessage();
            updateRoleSharedDataMessage.setId(((Role) object).getId());
            updateRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            updateRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            updateRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
            updateRoleSharedDataMessage.setAclEntry(((Role) object).getRoleAclEntry());
            updateRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            updateRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            updateRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            updateRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            updateRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());

            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(updateRoleSharedDataMessage,updateRoleSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {
            //All data of Staff user entity while deleting
            UpdateStaffUserSharedDataMessage updateStaffUserSharedDataMessage = new UpdateStaffUserSharedDataMessage();
            updateStaffUserSharedDataMessage.setId(((StaffUser) object).getId());
            updateStaffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            updateStaffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            updateStaffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            updateStaffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            updateStaffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if(((StaffUser) object).getLast_login_time() != null) {
                updateStaffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                updateStaffUserSharedDataMessage.setLast_login_time(null);
            }
            updateStaffUserSharedDataMessage.setTacacsAccessLevelGroup(((StaffUser) object).getTacacsAccessLevelGroup());
            updateStaffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            updateStaffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            updateStaffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            updateStaffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            updateStaffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            updateStaffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            updateStaffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            updateStaffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            updateStaffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            updateStaffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            updateStaffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());

            if(((StaffUser) object).getStaffUserparent().getId()!=null){
                updateStaffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            updateStaffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            updateStaffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());
            UpdateStaffUserSharedDataMessage updateStaffUserMessage = new UpdateStaffUserSharedDataMessage((StaffUser) object, "");

            if(updateStaffUserMessage.getTeam()!=null && !updateStaffUserMessage.getTeam().isEmpty())
            {
                updateStaffUserMessage.getTeam().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            if(updateStaffUserMessage.getRoles()!=null && !updateStaffUserMessage.getRoles().isEmpty())
            {
                updateStaffUserMessage.getRoles().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            //messageSender.send(updateStaffUserSharedDataMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateStaffUserMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateStaffUserSharedDataMessage, SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS);

            kafkaMessageSender.send(new KafkaMessageData(updateStaffUserSharedDataMessage,updateStaffUserMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)){
            //All data of Pincode entity while deleting
            UpdatePincodeSharedDataMessage updatePincodeSharedDataMessage = new UpdatePincodeSharedDataMessage();
            updatePincodeSharedDataMessage.setId(((Pincode) object).getId());
            updatePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            updatePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            updatePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            updatePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            updatePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            updatePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            updatePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            updatePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            updatePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());

            //messageSender.send(updatePincodeSharedDataMessage,SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updatePincodeSharedDataMessage,SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatePincodeSharedDataMessage,SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePincodeSharedDataMessage,updatePincodeSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Area.class)){
            //All data of Area entity while deleting
            UpdateAreaSharedDataMessage updateAreaSharedDataMessage = new UpdateAreaSharedDataMessage();
            updateAreaSharedDataMessage.setId(((Area) object).getId());
            updateAreaSharedDataMessage.setName(((Area) object).getName());
            updateAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            updateAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            updateAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            updateAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            updateAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            updateAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            updateAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            updateAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            updateAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());
            UpdateStaffUserSharedDataMessage updatedStaffData = new UpdateStaffUserSharedDataMessage((StaffUser) object);
            updateAreaSharedDataMessage.getPincode().setUpdatedate(null);
            updateAreaSharedDataMessage.getPincode().setCreatedate(null);

            //messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatedStaffData,updatedStaffData.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(ServiceArea.class)){
            //All data of ServiceArea entity while deleting
            UpdateServiceAreaSharedDataMessage updateServiceAreaSharedDataMessage = new UpdateServiceAreaSharedDataMessage();
            updateServiceAreaSharedDataMessage.setId(((ServiceArea) object).getId());
            updateServiceAreaSharedDataMessage.setAreaId(((ServiceArea) object).getAreaId());
            updateServiceAreaSharedDataMessage.setCityid(((ServiceArea) object).getCityid());
            updateServiceAreaSharedDataMessage.setLongitude(((ServiceArea) object).getLongitude());
            updateServiceAreaSharedDataMessage.setLatitude(((ServiceArea) object).getLatitude());
            updateServiceAreaSharedDataMessage.setName(((ServiceArea) object).getName());
            updateServiceAreaSharedDataMessage.setIsDeleted(((ServiceArea) object).getIsDeleted());
            updateServiceAreaSharedDataMessage.setPincodeList(((ServiceArea) object).getPincodeList());
            updateServiceAreaSharedDataMessage.setMvnoId(((ServiceArea) object).getMvnoId());
            updateServiceAreaSharedDataMessage.setStatus(((ServiceArea) object).getStatus());
            updateServiceAreaSharedDataMessage.setUpdatedById(((ServiceArea) object).getLastModifiedById());
            updateServiceAreaSharedDataMessage.setCreatedById(((ServiceArea) object).getCreatedById());
            updateServiceAreaSharedDataMessage.setCreatedByName(((ServiceArea) object).getCreatedByName());
            updateServiceAreaSharedDataMessage.setLastModifiedByName(((ServiceArea) object).getLastModifiedByName());
            if(updateServiceAreaSharedDataMessage.getPincodeList()!=null && !updateServiceAreaSharedDataMessage.getPincodeList().isEmpty())
            {
                updateServiceAreaSharedDataMessage.getPincodeList().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });

            }

            //messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateServiceAreaSharedDataMessage,updateServiceAreaSharedDataMessage.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)){
            //All data of BusinessUnit entity while deleting
            UpdateBusinessUnitSharedDataMessage updateBusinessUnitSharedDataMessage = new UpdateBusinessUnitSharedDataMessage();
            updateBusinessUnitSharedDataMessage.setId(((BusinessUnit) object).getId());
            updateBusinessUnitSharedDataMessage.setBuname(((BusinessUnit) object).getBuname());
            updateBusinessUnitSharedDataMessage.setBucode(((BusinessUnit) object).getBucode());
            updateBusinessUnitSharedDataMessage.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            updateBusinessUnitSharedDataMessage.setMvnoId(((BusinessUnit) object).getMvnoId());
            updateBusinessUnitSharedDataMessage.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            updateBusinessUnitSharedDataMessage.setStatus(((BusinessUnit) object).getStatus());
            updateBusinessUnitSharedDataMessage.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            updateBusinessUnitSharedDataMessage.setCreatedById(((BusinessUnit) object).getCreatedById());
            updateBusinessUnitSharedDataMessage.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());
            updateBusinessUnitSharedDataMessage.setCreatedByName(((BusinessUnit) object).getCreatedByName());
            updateBusinessUnitSharedDataMessage.setLastModifiedByName(((BusinessUnit) object).getLastModifiedByName());
            if(updateBusinessUnitSharedDataMessage.getInvestmentCodeid()!=null && !updateBusinessUnitSharedDataMessage.getInvestmentCodeid().isEmpty())
            {
                updateBusinessUnitSharedDataMessage.getInvestmentCodeid().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            //messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_RADIUS);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessUnitSharedDataMessage,updateBusinessUnitSharedDataMessage.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(Branch.class)){
            //All this data for branch deleting
            UpdateBranchSharedData updateBranchSharedDataMessage = new UpdateBranchSharedData();
            updateBranchSharedDataMessage.setId(((Branch) object).getId());
            updateBranchSharedDataMessage.setName(((Branch) object).getName());
            updateBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            updateBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            Set<ServiceArea> serviceAreaList=new HashSet<>();
            if (!CollectionUtils.isEmpty(((Branch) object).getServiceAreaNameList())) {
                for (ServiceArea area : ((Branch) object).getServiceAreaNameList()) {
                    ServiceArea newServicerea = new ServiceArea(area);
                    serviceAreaList.add(newServicerea);
                }
            }

            if(serviceAreaList!=null && !serviceAreaList.isEmpty())
            {
                serviceAreaList.stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                    if(x.getPincodeList()!=null && !x.getPincodeList().isEmpty())
                    {
                        x.getPincodeList().stream().forEach(y->{
                            y.setCreatedate(null);
                            y.setUpdatedate(null);
                        });
                    }
                });
            }
            updateBranchSharedDataMessage.setServiceAreaNameList(serviceAreaList);
            updateBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            updateBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            updateBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            updateBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            updateBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());
            updateBranchSharedDataMessage.setCreatedByName(((Branch) object).getCreatedByName());
            updateBranchSharedDataMessage.setLastModifiedByName(((Branch) object).getLastModifiedByName());

            //messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBranchSharedDataMessage,updateBranchSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            // All data of Region entity while saving
            UpdateRegionSharedDataMessage updateRegionSharedDataMessage = new UpdateRegionSharedDataMessage();
            updateRegionSharedDataMessage.setId(((Region) object).getId());
            updateRegionSharedDataMessage.setRname(((Region) object).getRname());
            List<Branch> branches=new ArrayList<>();
            if (((Region) object).getBranchidList() != null) {
                for (Branch branch : ((Region) object).getBranchidList()) {
                    branch.setCreatedate(null);
                    branch.setUpdatedate(null);
                    branches.add(new Branch(branch));
                }
            }
            updateRegionSharedDataMessage.setBranchidList(branches);
            updateRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            updateRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            updateRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            updateRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            updateRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            updateRegionSharedDataMessage.setCreatedByName(((Region) object).getCreatedByName());
            updateRegionSharedDataMessage.setLastModifiedByName(((Region) object).getLastModifiedByName());

            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateRegionSharedDataMessage,updateRegionSharedDataMessage.getClass().getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {
            // All data of BusinessVerticals entity while saving
            UpdateBusinessVerticalSharedDataMessage updateBusinessVerticalSharedDataMessage = new UpdateBusinessVerticalSharedDataMessage();
            updateBusinessVerticalSharedDataMessage.setId(((BusinessVerticals) object).getId());
            updateBusinessVerticalSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            List<Region> regionList=new ArrayList<>();
            if (!CollectionUtils.isEmpty(((BusinessVerticals) object).getBuregionidList())) {
                for (Region region : ((BusinessVerticals) object).getBuregionidList()) {
                    region.setCreatedate(null);
                    region.setUpdatedate(null);
                    regionList.add(new Region(region));
                }
            }
            updateBusinessVerticalSharedDataMessage.setBuregionidList(regionList);;
            updateBusinessVerticalSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            updateBusinessVerticalSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            updateBusinessVerticalSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            updateBusinessVerticalSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            updateBusinessVerticalSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());

            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessVerticalSharedDataMessage,updateBusinessVerticalSharedDataMessage.getClass().getSimpleName()));

        }else if(Objects.nonNull(object) && object.getClass().equals(Teams.class)){
            //All this data for teams update
            UpdateTeamsSharedData updateTeamsSharedSharedData = new UpdateTeamsSharedData();
            updateTeamsSharedSharedData.setId(((Teams) object).getId());
            updateTeamsSharedSharedData.setName(((Teams) object).getName());
            updateTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            updateTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            updateTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            updateTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            updateTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            updateTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            updateTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            updateTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            updateTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());

            if(updateTeamsSharedSharedData.getStaffUser()!=null && !updateTeamsSharedSharedData.getStaffUser().isEmpty()) {
                updateTeamsSharedSharedData.getStaffUser().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }
            if(updateTeamsSharedSharedData.getParentTeams()!=null) {
                updateTeamsSharedSharedData.getParentTeams().setUpdatedate(null);
                updateTeamsSharedSharedData.getParentTeams().setCreatedate(null);
            }

            //messageSender.send(updateTeamsSharedSharedData,SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS);

            kafkaMessageSender.send(new KafkaMessageData(updateTeamsSharedSharedData,updateTeamsSharedSharedData.getClass().getSimpleName()) );
        }

        else if (Objects.nonNull(object) && object.getClass().equals(SubBusinessUnit.class)) {
            UpdateSubBusinessUnitSharedDataMessage message = new UpdateSubBusinessUnitSharedDataMessage();
            message.setId(((SubBusinessUnit) object).getId());
            message.setMvnoId(((SubBusinessUnit) object).getMvnoId());
            message.setStatus(((SubBusinessUnit) object).getStatus());
            message.setSubbucode(((SubBusinessUnit) object).getSubbucode());
            message.setBusinessunitid(((SubBusinessUnit) object).getBusinessunitid());
            message.setIsDeleted(((SubBusinessUnit) object).getIsDeleted());
            message.setSubbuname(((SubBusinessUnit) object).getSubbuname());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(SubBusinessVertical.class)){
            UpdateSubBusinessVerticalsSharedDataMessage message = new UpdateSubBusinessVerticalsSharedDataMessage();
            message.setId(((SubBusinessVertical) object).getId());
            message.setSbvname(((SubBusinessVertical) object).getSbvname());
            message.setBusinessVerticalId(((SubBusinessVertical) object).getBusinessVerticals().getId().intValue());
            message.setStatus(((SubBusinessVertical) object).getStatus());
            message.setMvnoId(((SubBusinessVertical) object).getMvnoId());
            message.setIsDeleted(((SubBusinessVertical) object).getIsDeleted());

            //messageSender.send(message,SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(InvestmentCode.class)){
            UpdateInvestmentCodeSharedDataMessage message = new UpdateInvestmentCodeSharedDataMessage();
            message.setIccode(((InvestmentCode) object).getIccode());
            message.setIcname(((InvestmentCode) object).getIcname());
            message.setStatus(((InvestmentCode) object).getStatus());
            message.setMvnoId(((InvestmentCode) object).getMvnoId());
            message.setIsDeleted(((InvestmentCode) object).getIsDeleted());
            message.setId(((InvestmentCode) object).getId());
            message.setCreatedById(((InvestmentCode) object).getCreatedById());
            message.setLastModifiedById(((InvestmentCode) object).getLastModifiedById());
            message.setCreatedByName(((InvestmentCode) object).getCreatedByName());
            message.setLastModifiedByName(((InvestmentCode) object).getLastModifiedByName());

            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM);
            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_REVENUE);
            //messageSender.send(message,SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(BankManagementDTO.class)){
            UpdateBankManagementSharedDataMessage message = new UpdateBankManagementSharedDataMessage();
            message.setId(((BankManagementDTO) object).getId());
            message.setBankcode(((BankManagementDTO) object).getBankcode());
            message.setAccountnum(((BankManagementDTO) object).getAccountnum());
            message.setBankcode(((BankManagementDTO) object).getBankcode());
            message.setBankname(((BankManagementDTO) object).getBankname());
            message.setBankholdername(((BankManagementDTO) object).getBankholdername());
            message.setIfsccode(((BankManagementDTO) object).getIfsccode());
            message.setIsDeleted(((BankManagementDTO) object).getIsDeleted());
            message.setStatus(((BankManagementDTO) object).getStatus());

            //messageSender.send(message,SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(DepartmentPojo.class)){
            UpdateDepartmentSharedDataMessage message = new UpdateDepartmentSharedDataMessage();
            message.setId(((DepartmentPojo) object).getId());
            message.setIsDelete(((DepartmentPojo) object).getIsDelete());
            message.setStatus(((DepartmentPojo) object).getStatus());
            message.setName(((DepartmentPojo) object).getName());
            message.setPlanIds(((DepartmentPojo) object).getPlanIds());
            //messageSender.send(message,SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM);

            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        }
    }

    public void sendCreatedServiceAreaData(Object object, boolean staffSAMap) {
        if (Objects.nonNull(object)) {
            //All data of ServiceArea entity while saving
            SaveServiceAreaSharedDataMessge saveServiceAreaSharedDataMessge = new SaveServiceAreaSharedDataMessge();
            saveServiceAreaSharedDataMessge.setId(((ServiceArea) object).getId());
            saveServiceAreaSharedDataMessge.setAreaId(((ServiceArea) object).getAreaId());
            saveServiceAreaSharedDataMessge.setCityid(((ServiceArea) object).getCityid());
            saveServiceAreaSharedDataMessge.setLongitude(((ServiceArea) object).getLongitude());
            saveServiceAreaSharedDataMessge.setLatitude(((ServiceArea) object).getLatitude());
            saveServiceAreaSharedDataMessge.setName(((ServiceArea) object).getName());
            saveServiceAreaSharedDataMessge.setIsDeleted(((ServiceArea) object).getIsDeleted());
            saveServiceAreaSharedDataMessge.setPincodeList(((ServiceArea) object).getPincodeList());

            if(saveServiceAreaSharedDataMessge.getPincodeList()!=null && !saveServiceAreaSharedDataMessge.getPincodeList().isEmpty())
            {
                saveServiceAreaSharedDataMessge.getPincodeList().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            saveServiceAreaSharedDataMessge.setLocationIdList(((ServiceArea) object).getLocationIdList());

            saveServiceAreaSharedDataMessge.setMvnoId(((ServiceArea) object).getMvnoId());
            saveServiceAreaSharedDataMessge.setStatus(((ServiceArea) object).getStatus());
            saveServiceAreaSharedDataMessge.setCreatedById(((ServiceArea) object).getCreatedById());
            saveServiceAreaSharedDataMessge.setStaffSAMap(staffSAMap);
            saveServiceAreaSharedDataMessge.setCreatedById(((ServiceArea) object).getCreatedById());
            saveServiceAreaSharedDataMessge.setCreatedByName(((ServiceArea) object).getCreatedByName());
            saveServiceAreaSharedDataMessge.setLastModifiedByName(((ServiceArea) object).getLastModifiedByName());
            saveServiceAreaSharedDataMessge.setSiteName(((ServiceArea) object).getSiteName());

            if(saveServiceAreaSharedDataMessge.getPincodeList()!=null && !saveServiceAreaSharedDataMessge.getPincodeList().isEmpty())
            {
                saveServiceAreaSharedDataMessge.getPincodeList().stream().forEach(x->{
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                });
            }

            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM);
            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICEAREA_CREATE_DATA_SHARE_REVENUE);
            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(saveServiceAreaSharedDataMessge,saveServiceAreaSharedDataMessge.getClass().getSimpleName()));
        }
    }

    public void updateMvnoISPData(int oldMvnoid, int newMvnoid) {
        UpdateMvnoData mvno= new UpdateMvnoData();
        mvno.setNewmvnoId(newMvnoid);
        mvno.setOldmvnoId(oldMvnoid);

        /*messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_Partner_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_INVENTORY_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_TICKET_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_NOTIFICATION_ISP);
        messageSender.send(mvno, SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_SALES_CRM_ISP);*/

        kafkaMessageSender.send(new KafkaMessageData(mvno,mvno.getClass().getSimpleName(), KafkaConstant.IPS_TO_ISP));
    }
}
