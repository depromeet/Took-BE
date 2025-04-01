package com.evenly.took.global.logging.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivacySensitiveLogging {

	String reason() default "개인정보 또는 민감한 데이터 포함";

	boolean logRequestBody() default false;

	boolean logResponseBody() default false;
}
