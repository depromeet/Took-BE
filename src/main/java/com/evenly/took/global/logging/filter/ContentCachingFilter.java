package com.evenly.took.global.logging.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContentCachingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		if (!(request instanceof ContentCachingRequestWrapper)) {
			request = new ContentCachingRequestWrapper(request);
		}

		if (!(response instanceof ContentCachingResponseWrapper)) {
			response = new ContentCachingResponseWrapper(response);
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			if (response instanceof ContentCachingResponseWrapper) {
				((ContentCachingResponseWrapper)response).copyBodyToResponse();
			}
		}
	}
}
