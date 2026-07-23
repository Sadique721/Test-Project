package com.savbill.cpm.modules.Alert.communication.config;

import org.springframework.stereotype.Component;


@Component
public class SmsWorkerThread implements Runnable {
    @Override
    public void run() {
        System.out.println("");
    }
//    private static final String MODULE = " [SMS Config Thread] ";
//    private final Communication communication;
//    private final RestTemplate restTemplate;
//
//    public SmsWorkerThread(Communication communication, RestTemplateBuilder restTemplateBuilder) {
//        this.communication = communication;
//        this.restTemplate = restTemplateBuilder.build();
//    }
//
//    @Override
//    public void run() {
//        CommunicationRepository communicationRepository = SpringContext.getBean(CommunicationRepository.class);
//
//        String callurl = CommunicationConstant.SMSURL.replace("{destination}", communication.getDestination())
//                .replace("{msg}", communication.getSmsMessage()).replace("{templateid}", communication.getTemplateId());
//        try {
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(100, TimeUnit.SECONDS)
//                    .writeTimeout(100, TimeUnit.SECONDS)
//                    .readTimeout(100, TimeUnit.SECONDS)
//                    .build();
//            Request httpRequest = new Request.Builder()
//                    .url(callurl)
//                    .build();
//
//            System.out.println("Req url : " + httpRequest.url().toString());
//            Response content = client.newCall(httpRequest).execute();
//            if (content.toString().contains("code=200")) {
//                communicationRepository.updateCommunication(communication.getUuid(), true, null);
//            } else {
//                communicationRepository.updateCommunication(communication.getUuid(), false, content.toString());
//            }
//            content.close();
//        } catch (IOException e) {
//            communicationRepository.updateCommunication(communication.getUuid(), false, e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
