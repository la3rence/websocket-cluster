package me.lawrenceli.websocket.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author lawrence
 * @since 2021/3/24
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext; // NOSONAR
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
