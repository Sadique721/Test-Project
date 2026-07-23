package com.savbill.ticketmanagement.core.modules.Plan.service;

import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Plan.domain.PostpaidPlan;
import com.savbill.ticketmanagement.core.modules.Plan.dto.PlanPojo;
import com.savbill.ticketmanagement.core.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.SavePlanSharedDataMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdatePlanSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostPaidPlanService extends ExBaseAbstractService<PlanPojo, PostpaidPlan,Integer> {
    public PostPaidPlanService(JpaRepository<PostpaidPlan, Integer> repository, IBaseMapper<PlanPojo, PostpaidPlan> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    private static Log log = LogFactory.getLog(PostPaidPlanService.class);

@Transactional
    public void savePostpaidPlan(SavePlanSharedDataMessage message){
        try {

            PostpaidPlan postpaidPlan = new PostpaidPlan();

            postpaidPlan.setId(message.getId());
            postpaidPlan.setServiceId(message.getServiceId());
            postpaidPlan.setPlanStatus(message.getPlanStatus());
            postpaidPlan.setName(message.getName());
            postpaidPlan.setBuId(message.getBuId());
            postpaidPlan.setMvnoId(message.getMvnoId());
            postpaidPlan.setIsDelete(message.getIsDelete());
            postpaidPlan.setStatus(message.getStatus());
            postpaidPlan.setPlantype(message.getPlantype());
            postpaidPlan.setMode(message.getMode());
            postpaidPlan.setCategory(message.getCategory());

            postpaidPlanRepo.save(postpaidPlan);
        }catch (Exception e){
          log.error("Error While Creating Postpaid Plan, "+e.getMessage());
        }

    }


@Transactional
    public void updatePostPaidPlan(UpdatePlanSharedDataMessage message){
        try {
            PostpaidPlan postpaidPlan = new PostpaidPlan();

            postpaidPlan = postpaidPlanRepo.findById(message.getId()).orElse(null);

            postpaidPlan.setServiceId(message.getServiceId());
            postpaidPlan.setPlanStatus(message.getPlanStatus());
            postpaidPlan.setName(message.getName());
            postpaidPlan.setBuId(message.getBuId());
            postpaidPlan.setMvnoId(message.getMvnoId());
            postpaidPlan.setIsDelete(message.getIsDelete());
            postpaidPlan.setStatus(message.getStatus());
            postpaidPlan.setPlantype(message.getPlantype());
            postpaidPlan.setMode(message.getMode());
            postpaidPlan.setCategory(message.getCategory());

            postpaidPlanRepo.save(postpaidPlan);
        }catch (Exception e){
           log.error("error while Updating Postpaid plan"+e.getMessage());
        }

    }
}
