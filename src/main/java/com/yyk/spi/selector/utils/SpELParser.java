package com.yyk.spi.selector.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class SpELParser {


    private static final Pattern SPEL_PATTERN = Pattern.compile("^[$#].*");
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static  <T> T pars(String spelStr, JoinPoint joinPoint, Class<T> clazz) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        final Expression expression = parser.parseExpression(spelStr);
        final StandardEvaluationContext context = new StandardEvaluationContext();
        final Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return expression.getValue(context, clazz);
    }

    public static String parsString(String spelStr, JoinPoint joinPoint) {
        if (SPEL_PATTERN.matcher(spelStr).matches())
            return pars(spelStr, joinPoint, String.class);
        return spelStr;
    }

    public static void main(String[] args) {
        System.out.println(SPEL_PATTERN.matcher("$param.a").matches());
    }
}
