package com.nexapay.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Aspect
@Component
public class SensitiveDataMaskingAspect {

    private static final Pattern PAN_PATTERN = Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b");

    @Around("execution(* com.nexapay.ai.tools..*(..))")
    public Object maskSensitiveToolOutputs(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result == null) {
            return null;
        }
        return maskString(result);
    }

    private Object maskString(Object obj) {
        if (obj instanceof String str) {
            return PAN_PATTERN.matcher(str).replaceAll(match -> {
                String digits = match.group().replaceAll("[^0-9]", "");
                return "**** **** **** " + digits.substring(digits.length() - 4);
            });
        }
        return obj;
    }
}
