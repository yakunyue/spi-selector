package com.yyk.spi.selector.annotation;

import com.yyk.spi.selector.SpiSelectorRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Import(SpiSelectorRegister.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSpiSelector {
    String[] value() default "com.yyk";
}
