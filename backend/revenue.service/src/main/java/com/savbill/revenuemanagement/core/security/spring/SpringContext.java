package com.savbill.revenuemanagement.core.security.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Returns the Spring managed bean instance of the given class type if it exists.
     * Returns null otherwise.
     * @param beanClass
     * @return
     */
//    public <T extends Object> T getBean(Class<T> beanClass) {
//        return context.getBean(beanClass);
//    }

    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {

        // store ApplicationContext reference to access required beans later on
    	SpringContext.applicationContext = context;
    }

    public static ApplicationContext getApplicationContext()
    {
    	return applicationContext;
    }
}
