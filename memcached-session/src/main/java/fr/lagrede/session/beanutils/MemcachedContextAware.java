package fr.lagrede.session.beanutils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class MemcachedContextAware implements ApplicationContextAware {

    private static ApplicationContext context = null;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext; 
    }

    public static ApplicationContext getContext() {
        return context;
    }
    
}
