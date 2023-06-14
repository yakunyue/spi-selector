package com.yyk.spi.selector;

import com.yyk.spi.selector.annotation.SpiInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * SpiInterface的FactoryBean
 */
class SpiSelectorProxyFactory<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Class<T> spiInterface;
    private ApplicationContext applicationContext;
    private Map<String, T> beans;
    private SpiInterface spiInterfaceAnnotation;

    SpiSelectorProxyFactory(Class<T> spiInterface) {
        this.spiInterface = spiInterface;
        spiInterfaceAnnotation = spiInterface.getAnnotation(SpiInterface.class);
    }

    @Override
    public T getObject() throws Exception {
        long start = System.currentTimeMillis();
        logger.info("SpiSelectorProxyFactory create the JDK Dynamic Proxy class ,{}", spiInterface.getName());
        T obj = new JdkSpiProxy<>(spiInterface, beans).getSpiProxy();
        logger.info("{} create JDK Dynamic Proxy class use {} ms", spiInterface.getName(), System.currentTimeMillis() - start);
        return obj;
    }

    @Override
    public Class<?> getObjectType() {
        return spiInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        beans = new HashMap<>();
        logger.info("current spi service is {}", spiInterface.getName());
        applicationContext.getBeansOfType(spiInterface).values().forEach(bean -> {
            Class<?> clazz = assemblySpiClass(bean);
            if (clazz != null) {
                SpiInterface spiInterface = clazz.getAnnotation(SpiInterface.class);
                if (spiInterface != null) {
                    Arrays.stream(spiInterface.code()).forEach(code -> beans.put(code, bean));
                    beans.put(clazz.getName(), bean);
                    logger.info("mapping code is {}, implement is {}", Arrays.toString(spiInterface.code()), clazz.getName());
                }
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取SpiInterface实现类
     */
    private Class<?> assemblySpiClass(Object obj) {
        Class<?> clazz = obj.getClass();
        do {
            SpiInterface spiInterface = clazz.getAnnotation(SpiInterface.class);
            if (spiInterface != null) {
                return clazz;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && !clazz.equals(Object.class));
        return null;
    }
}
