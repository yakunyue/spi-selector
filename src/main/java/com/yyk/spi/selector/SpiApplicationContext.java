package com.yyk.spi.selector;

import org.springframework.util.StringUtils;

import java.util.concurrent.Callable;

/**
 * spi bean的上下文和ThreadLocal
 */
public class SpiApplicationContext {

    /**
     * 使用InheritableThreadLocal,复制父线程的ThreadLocal
     */
    private static ThreadLocal<String> spiCodeLocal = new InheritableThreadLocal<>();

    public static String getSpiCode() {
        return spiCodeLocal.get();
    }

    public static void setSpiCode(String spiCode) {
        spiCodeLocal.set(spiCode);
    }

    public static String cleanCode() {
        String code = getSpiCode();
        spiCodeLocal.remove();
        return code;
    }

    /**
     * 构建多线程的spiRunnable
     *
     * @param delegete 可执行的Runnable
     * @return
     */
    public static Runnable spiWrap(Runnable delegete) {
        /*先获取spiCode*/
        String spiCode = getSpiCode();
        return () -> {
            try {
                /*切换线程执行先设置spiCode*/
                if (!StringUtils.isEmpty(spiCode)) {
                    setSpiCode(spiCode);
                }
                delegete.run();
            } finally {
                /*执行完成之后清除spiCode，避免内存溢出*/
                cleanCode();
            }
        };
    }

    /**
     * 构建多线程的spiCallable
     *
     * @param delegete 原callable
     * @param <V>      返回参数的泛型规约
     * @return spi的Callable
     */
    public <V> Callable<V> spiWrap(Callable<V> delegete) {
        String spiCode = getSpiCode();
        return () -> {
            try {
                /*切换线程执行先设置spiCode*/
                if (!StringUtils.isEmpty(spiCode)) {
                    setSpiCode(spiCode);
                }
                return delegete.call();
            } finally {
                /*执行完成之后清除spiCode，避免内存溢出*/
                cleanCode();
            }
        };
    }
}
