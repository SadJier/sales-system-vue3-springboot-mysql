package com.sadjier.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import java.lang.reflect.Method;

public class SpelUtil {
    /// <summary>spel解析器</summary>
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    /// <summary>参数名称解析器</summary>
    private static final ParameterNameDiscoverer PARAM_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /// <summary>解析Spel表达式</summary>
    public static String parse(ProceedingJoinPoint joinPoint, String spel) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, PARAM_NAME_DISCOVERER);

        return PARSER.parseExpression(spel).getValue(context, String.class);
    }
}