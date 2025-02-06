package com.evenly.blok.global.response;

import com.evenly.blok.global.exception.dto.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof ErrorResponse errorResponse) {
            response.setStatusCode(errorResponse.getStatus());
            return errorResponse;
        }
        if (body instanceof String) {
            return body;
        }
        SuccessResponse successResponse = SuccessResponse.of(body);
        response.setStatusCode(successResponse.getStatus());
        return successResponse;
    }
}
