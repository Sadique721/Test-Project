package com.savbill.integrationsystem.Services;

import com.savbill.integrationsystem.rabbitmq.PlanServiceForIntegrationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServicesService {

    @Autowired
    private ServicesRepository servicesRepository;

    public Services save(PlanServiceForIntegrationMessage message){
        try {
            if (message.getPlanServiceData() != null) {

                String str = message.getPlanServiceData().substring(1, message.getPlanServiceData().length() - 1);

                //Split the string by , to get key-value pairs
                String[] keyValuePairs = str.split(",");

                Map<String, String> map = new HashMap<>();
                //Iterate over the pairs
                for (String pair : keyValuePairs)
                {
                    //Split the pairs to get key and value
                    String[] entry = pair.split("=");

                    //Add them to the hashmap and trim whitespaces
                    map.put(entry[0].trim(), entry[1].trim());
                }
                Services services = new Services(map);
              //  Services services1 = servicesRepository.save(services);
                return services;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
