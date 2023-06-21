package com.yyk.spi.selector;

import com.yyk.spi.selector.annotation.SpiInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class SpiSelectorScanner extends ClassPathBeanDefinitionScanner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public SpiSelectorScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("Spi interface not scanned,Please check your configuration.packages:{}", Arrays.toString(basePackages));
        } else {
            logger.info("find total {} spi interface", beanDefinitions.size());
            beanDefinitions.forEach(this::assemblyBeanDefinition); /*注册bean*/
        }
        return beanDefinitions;
    }

    private void assemblyBeanDefinition(BeanDefinitionHolder holder) {
        //转为GenericBeanDefinition类型
        GenericBeanDefinition definition = GenericBeanDefinition.class.cast(holder.getBeanDefinition());
        String spiInterface = definition.getBeanClassName();
        logger.info("register the spi interface is {}", spiInterface);
        /*设置工厂类的class*/
        definition.setBeanClass(SpiSelectorProxyFactory.class);
        /*设置构造参数的class*/
        definition.getConstructorArgumentValues().addGenericArgumentValue(spiInterface);
        /*设置自动注入的类型，根据Type*/
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        /*设置Primary属性，相当于@Primary注解*/
        definition.setPrimary(true);
        logger.info("register the spi interface {} success", spiInterface);
    }

    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(SpiInterface.class));
        addIncludeFilter(new AssignableTypeFilter(SpiCodeAspect.class));
//        addExcludeFilter((reader, factory) -> reader.getClassMetadata().getClassName().endsWith("package-info"));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }
}
