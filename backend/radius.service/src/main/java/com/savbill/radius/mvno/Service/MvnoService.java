package com.savbill.radius.mvno.Service;

import com.savbill.radius.kafka.message.SaveMvnoSharedDataMessage;
import com.savbill.radius.kafka.message.UpdateMvnoSharedDataMessage;
import com.savbill.radius.mvno.Entity.Mvno;
import com.savbill.radius.mvno.Repository.MvnoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MvnoService {

    @Autowired
    MvnoRepository mvnoRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(MvnoService.class);


    // Shared MVNO Data from Common APIGW to CMS
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setId(mvnoSharedDataMessage.getId());
            mvno.setName(mvnoSharedDataMessage.getName());
            mvno.setUsername(mvnoSharedDataMessage.getUsername());
            mvno.setPassword(mvnoSharedDataMessage.getPassword());
            mvno.setSuffix(mvnoSharedDataMessage.getSuffix());
            mvno.setDescription(mvnoSharedDataMessage.getDescription());
            mvno.setEmail(mvnoSharedDataMessage.getEmail());
            mvno.setPhone(mvnoSharedDataMessage.getPhone());
            mvno.setStatus(mvnoSharedDataMessage.getStatus());
            mvno.setLogfile(mvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(mvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(mvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(mvnoSharedDataMessage.getIsDelete());
            mvno.setCreatedById(mvnoSharedDataMessage.getCreatedById());
            mvno.setLastModifiedById(mvnoSharedDataMessage.getLastModifiedById());
            mvno.setLogo_file_name(mvnoSharedDataMessage.getLogo_file_name());
            //mvno.setProfileImage(mvnoSharedDataMessage.getProfileImage());
            mvno.setMvnoPaymentDueDays(mvnoSharedDataMessage.getMvnoPaymentDueDays());
            mvno = mvnoRepository.save(mvno);
            //numberSequenceUtil.createSequenceNumberFunctionForMVNO(mvno);
            //To add default path for mvno
            //clientServiceSrv.addDefaultPathWhenMvnoCreated(mvno);
            //CustomersPojo customersPojo = customersService.saveDefaultCustomerForMvno(mvno);
            mvno = mvnoRepository.save(mvno);
//            if(customersPojo != null) {
//                mvno.setCustInvoiceRefId(customersPojo.getId());
//            }
            logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (Exception e) {
            logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName());
            logger.error(e.getMessage());
        }
    }

    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            if (mvno != null) {
                mvno.setId(updateMvnoSharedDataMessage.getId());
                mvno.setName(updateMvnoSharedDataMessage.getName());
                mvno.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno.setPhone(updateMvnoSharedDataMessage.getPhone());
                if(!mvno.getStatus().equalsIgnoreCase(updateMvnoSharedDataMessage.getStatus())){
                    Set<Long> mvnoId = new HashSet<>();
                    mvnoId.add(mvno.getId());
                    //changeMvnoStatus(mvnoId,updateMvnoSharedDataMessage.getStatus());
                    mvno.setStatus(updateMvnoSharedDataMessage.getStatus());
                }
                mvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                mvno.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvno.setProfileImage(updateMvnoSharedDataMessage.getProfileImage());
                mvno.setLogo_file_name(updateMvnoSharedDataMessage.getLogo_file_name());
                mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());

                mvnoRepository.save(mvno);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            } else {
                Mvno mvno1 = new Mvno();
                mvno1.setId(updateMvnoSharedDataMessage.getId());
                mvno1.setName(updateMvnoSharedDataMessage.getName());
                mvno1.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno1.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno1.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno1.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno1.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno1.setPhone(updateMvnoSharedDataMessage.getPhone());
                if(!mvno.getStatus().equalsIgnoreCase(updateMvnoSharedDataMessage.getStatus())){
                    Set<Long> mvnoId = new HashSet<>();
                    mvnoId.add(mvno.getId());
                    //changeMvnoStatus(mvnoId,updateMvnoSharedDataMessage.getStatus());
                    mvno1.setStatus(updateMvnoSharedDataMessage.getStatus());
                }
                mvno1.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno1.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno1.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno1.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                mvno1.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno1.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
                mvnoRepository.save(mvno1);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            }
        } catch (Exception e) {
            logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName());
            logger.error(e.getMessage());
        }
    }


}
