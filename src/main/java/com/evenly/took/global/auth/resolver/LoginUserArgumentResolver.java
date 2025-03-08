package com.evenly.took.global.auth.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final TokenProvider tokenProvider;
	private final HeaderHandler headerHandler;
	private final UserRepository userRepository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUser.class)
			&& parameter.getParameterType().equals(User.class);
	}

	@Override
	public User resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String accessToken = headerHandler.resolveAccessToken(request);

		if (accessToken == null) {
			return null;
		}

		try {
			tokenProvider.validateAccessToken(accessToken);
			String userId = tokenProvider.getUserIdFromAccessToken(accessToken);
			return userRepository.findById(Long.valueOf(userId)).orElse(null);
		} catch (Exception e) {
			return null;
		}
	}
}
