package com.savbill.integrationsystem.PostpaidPlan;

import com.savbill.integrationsystem.rabbitmq.PostpaidPlanMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostpaidPlanServiceImpl {

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;


    public PostpaidPlan save(PostpaidPlanMessage message){
        try {
            if (message.getData() != null) {
                PostpaidPlan postpaidPlan = new PostpaidPlan(message.getData());
                postpaidPlan.setPlanId(postpaidPlan.getId());
                PostpaidPlan plan = postpaidPlanRepository.save(postpaidPlan);
                return plan;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
//        try {
//            if (message.getp() != null) {
//
//                String str = message.getCustServiceMappingData().substring(1, message.getCustServiceMappingData().length() - 1);
//
//                //Split the string by , to get key-value pairs
//                String[] keyValuePairs = str.split(",");
//
//                Map<String, String> map = new HashMap<>();
//                //Iterate over the pairs
//                for (String pair : keyValuePairs)
//                {
//                    //Split the pairs to get key and value
//                    String[] entry = pair.split("=");
//
//                    //Add them to the hashmap and trim whitespaces
//                    map.put(entry[0].trim(), entry[1].trim());
//                }
//                CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping(map);
//                customerServiceMapping.setCustServiceMappingId(customerServiceMapping.getId());
//                CustomerServiceMapping customerServiceMapping1 = customerServiceMappingRepository.save(customerServiceMapping);
//                return customerServiceMapping1;
//            } else {
//                throw new RuntimeException("INVALID_DATA");
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
}
