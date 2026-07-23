package com.savbill.ticketmanagement.core.modules.Partner.service;

import com.savbill.ticketmanagement.core.modules.Partner.domain.Partner;
import com.savbill.ticketmanagement.core.modules.Partner.dto.PartnerPojo;
import com.savbill.ticketmanagement.core.modules.Partner.repository.PartnerRepository;
import com.savbill.ticketmanagement.core.modules.Region.service.RegionService;
import com.savbill.ticketmanagement.core.service.AbstractService;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.SavePartnerSharedDataMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdatePartnerSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerService extends AbstractService<Partner, PartnerPojo, Integer> {


    @Autowired
    private PartnerRepository entityRepository;
    @Override
    protected JpaRepository<Partner, Integer> getRepository() {
        return entityRepository;
    }
    private static Log log = LogFactory.getLog(RegionService.class);
    @Override
    public Partner get(Integer id) {
        Partner partner = super.get(id);
        if(getBUIdsFromCurrentStaff() != null && getMvnoIdFromCurrentStaff() != null) {
            if (getMvnoIdFromCurrentStaff() == 1 || (partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || partner.getMvnoId() == 1) && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1))
                return partner;
        } else {
            return partner;
        }
        return null;
    }


@Transactional
    public void savePartnerService (SavePartnerSharedDataMessage message){
        try {
            Partner partner = new Partner();

            partner.setId(message.getId());
            partner.setBranch(message.getBranch());
            partner.setBuId(message.getBuId());
            partner.setServiceAreaList(message.getServiceAreaList());
            partner.setCity(message.getCity());
            partner.setCountry(message.getCountry());
            partner.setState(message.getState());
            partner.setMvnoId(message.getMvnoId());
            partner.setName(message.getName());
            partner.setStatus(message.getStatus());
            partner.setMobile(message.getMobile());
            partner.setEmail(message.getEmail());

            entityRepository.save(partner);
        }catch (Exception e){
           log.error("Unable to create Partner Service"+e.getMessage());
        }

    }



    public void updatePartnerService(UpdatePartnerSharedDataMessage message){
try {
    Partner partner = new Partner();

    partner = entityRepository.findById(message.getId()).orElse(null);

    if (partner != null) {
        partner.setBranch(message.getBranch());
        partner.setBuId(message.getBuId());
        partner.setServiceAreaList(message.getServiceAreaList());
        partner.setCity(message.getCity());
        partner.setCountry(message.getCountry());
        partner.setState(message.getState());
        partner.setMvnoId(message.getMvnoId());
        partner.setName(message.getName());
        partner.setStatus(message.getStatus());
        partner.setMobile(message.getMobile());
        partner.setEmail(message.getEmail());

        entityRepository.save(partner);
    }
}catch (Exception e){
log.error("Unable to Update Update Partner service"+e.getMessage());
}

    }
}
