package com.savbill.integrationsystem.configuration;

import java.util.concurrent.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig  {

    @Bean (name = "sendBillThreadPoolExecutor")
    public ThreadPoolExecutor sendBillThreadPoolExecutor() {
    	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 100, 100, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
    	return threadPoolExecutor;
    }
    
    @Bean (name = "sendCreditNoteThreadPoolExecutor")
    public ThreadPoolExecutor sendCreditNoteThreadPoolExecutor() {
    	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 100, 100, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
    	return threadPoolExecutor;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
