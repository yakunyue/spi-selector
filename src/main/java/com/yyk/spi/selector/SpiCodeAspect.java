/**
 * 佛祖镇楼                  BUG辟易
 * 佛曰:
 * 写字楼里写字间，写字间里程序员；
 * 程序人员写程序，又拿程序换酒钱。
 * 酒醒只在网上坐，酒醉还来网下眠；
 * 酒醉酒醒日复日，网上网下年复年。
 * 但愿老死电脑间，不愿鞠躬老板前；
 * 奔驰宝马贵者趣，公交自行程序员。
 * 别人笑我忒疯癫，我笑自己命太贱；
 * 不见满街漂亮妹，哪个归得程序员？
 */
package com.yyk.spi.selector;

import com.yyk.spi.selector.annotation.SpiCode;
import com.yyk.spi.selector.utils.SpELParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * SpiCode的注解切面
 */
@Aspect
public class SpiCodeAspect implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 方法上
     */
    @Before("@annotation(spiCode)")
    public void annotationBefore(SpiCode spiCode, ProceedingJoinPoint joinPoint) {
        logger.info("method join point,set spi code is {}", spiCode.value());
        String code = SpELParser.parsString(spiCode.value(),joinPoint);
        SpiApplicationContext.setSpiCode(code);
    }

    @After("@annotation(spiCode)")
    public void annotationAfter(SpiCode spiCode) {
        logger.info("method join point,clean spi cod,code is {}", spiCode.value());
        SpiApplicationContext.cleanCode();
    }

    /**
     * 类上
     */
    @Before("@within(spiCode)")
    public void within(SpiCode spiCode) {
        logger.info("set spi code is {}", spiCode.value());
        SpiApplicationContext.setSpiCode(spiCode.value());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("init the SpiCodeAspect success");
    }
}
