package com.evenly.took.global.monitoring.slack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@Profile("prod")
@Aspect
@Component
@RequiredArgsConstructor
public class SlackErrorAlertAspect {

	private final SlackService slackService;

	@AfterThrowing(
		pointcut = "within(@com.evenly.took.global.monitoring.slack.SlackErrorAlert *) " +
			"|| @annotation(com.evenly.took.global.monitoring.slack.SlackErrorAlert)",
		throwing = "ex"
	)
	public void sendErrorAlert(JoinPoint joinPoint, Throwable ex) {

		if (!isInternalServerError(ex)) {
			return;
		}

		String controllerClass = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String controllerMethod = joinPoint.getSignature().getName();

		StackTraceElement origin = Arrays.stream(ex.getStackTrace())
			.filter(it -> it.getClassName().startsWith("com.evenly"))
			.findFirst()
			.orElse(ex.getStackTrace()[0]);
		String errorLocation = origin.toString();

		String title = "🚨 [TOOK 서비스에서 에러가 발생했습니다!!] 🚨";

		Map<String, String> errorDetails = new LinkedHashMap<>();
		errorDetails.put("============================[ 에러 발생 ]============================", "");
		errorDetails.put("Location", errorLocation);
		errorDetails.put("Message", ex.getMessage());
		errorDetails.put("=========================[ 요청 Controller ]=========================", "");
		errorDetails.put("Class", controllerClass);
		errorDetails.put("Method", controllerMethod + "(..)");
		errorDetails.put("========================[ 추가 Exception 정보 ]========================", "");
		errorDetails.put("요약", ex.getClass().getSimpleName());
		errorDetails.put("전체", ex.getClass().getName());

		slackService.sendMessage(title, errorDetails);
	}

	private boolean isInternalServerError(Throwable ex) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

		if (ex instanceof TookException) {
			httpStatus = ((TookException)ex).getErrorCode().getStatus();
		} else {
			ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
			if (responseStatus != null) {
				httpStatus = responseStatus.value();
			}
		}

		return httpStatus == HttpStatus.INTERNAL_SERVER_ERROR;
	}

}
