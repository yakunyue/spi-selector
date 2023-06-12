package com.yyk.spi.selector;

import com.yyk.spi.selector.annotation.EnableSpiSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class SpiSelectorRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String[] packages = new String[]{"com.yyk"};
        try {
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
                    metadata.getAnnotationAttributes(EnableSpiSelector.class.getName(), true));
            /*获取spi扫描的包路径*/
            packages = annoAttrs.getStringArray("value");
        } catch (Exception e) {
            logger.warn("spi-selector:failed to get the scan path!", e);
        }
        Arrays.stream(packages).forEach(str -> logger.info("spi will scan package {}", str));
        logger.info("scan the spi interface auto proxy");
        SpiSelectorScanner scanner = new SpiSelectorScanner(registry);
        scanner.setResourceLoader(this.resourceLoader);
        try {
            scanner.registerFilters();
            scanner.doScan(packages);
        } catch (Exception ex) {
            logger.error("Could not determine auto-configuration package, spi interface scanning disabled.", ex);
        }
    }
}
