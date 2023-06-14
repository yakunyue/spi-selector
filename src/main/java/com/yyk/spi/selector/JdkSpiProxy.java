package com.yyk.spi.selector;

import com.yyk.spi.selector.annotation.SpiInterface;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * spi service的动态代理
 */
public class JdkSpiProxy<T> implements InvocationHandler {

    /**
     * spi的代理接口
     */
    private Class<T> spiInterface;
    /**
     * spring里面不同spi的实现的类
     */
    private Map<String, T> beans;

    private SpiInterface spiInterfaceAnna;

    public JdkSpiProxy(Class<T> spiInterface, Map<String, T> beans) {
        this.beans = beans;
        this.spiInterface = spiInterface;
        this.spiInterfaceAnna = spiInterface.getAnnotation(SpiInterface.class);
    }

    /**
     * 动态代理方法调用
     *
     * @param proxy  代理类
     * @param method 当前方法
     * @param args   参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String code = SpiApplicationContext.getSpiCode();
        if (StringUtils.isEmpty(code)) {
            code = spiInterfaceAnna.defaultCode();
        }
        final T bean = beans.get(code);
        if (bean == null)
            throw new RuntimeException(String.format("Spi select failed,code=%s,spiInterface=%s", code, spiInterface.getName()));
        return method.invoke(bean, args);
    }

    /**
     * 获取代理对象呢
     *
     * @return Spi的动态代理
     */
    public T getSpiProxy() {
        return spiInterface.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{spiInterface}, this));
    }
}
