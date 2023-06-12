package com.yyk.spi.selector.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpiInterface {

    /**
     * spiCode
     *
     * @return
     */
    String[] code() default "";

    /**
     * 默认的spiCode
     *
     * @return
     */
    String defaultCode() default "";
}
