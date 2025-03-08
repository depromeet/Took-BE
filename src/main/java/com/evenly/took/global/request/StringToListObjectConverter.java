package com.evenly.took.global.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StringToListObjectConverter implements GenericConverter {

	private final ObjectMapper objectMapper;

	public StringToListObjectConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, List.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		String stringValue = (String)source;
		if (stringValue == null || stringValue.isEmpty()) {
			return Collections.emptyList();
		}

		try {
			// Check if the string already looks like a JSON array
			if (stringValue.trim().startsWith("[") && stringValue.trim().endsWith("]")) {
				// List의 제네릭 타입 가져오기
				Class<?> elementType = targetType.getElementTypeDescriptor().getType();

				// TypeFactory를 사용하여 제네릭 타입 생성
				JavaType javaType = objectMapper.getTypeFactory()
					.constructCollectionType(List.class, elementType);

				// JSON 문자열을 List<T>로 변환
				return objectMapper.readValue(stringValue, javaType);
			} else {
				// Handle comma-separated values
				Class<?> elementType = targetType.getElementTypeDescriptor().getType();

				if (elementType == String.class) {
					// If the target is List<String>, simply split by comma
					return Arrays.stream(stringValue.split(","))
						.map(String::trim)
						.collect(Collectors.toList());
				} else {
					// For other types, first split by comma, then convert each value using ObjectMapper
					JavaType elementJavaType = objectMapper.getTypeFactory().constructType(elementType);

					return Arrays.stream(stringValue.split(","))
						.map(String::trim)
						.map(item -> {
							try {
								return objectMapper.readValue("\"" + item + "\"", elementJavaType);
							} catch (Exception e) {
								throw new IllegalArgumentException(
									"Failed to convert item '" + item + "' to " + elementType.getSimpleName(), e);
							}
						})
						.collect(Collectors.toList());
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"Failed to convert String to List<" +
					targetType.getElementTypeDescriptor().getType().getSimpleName() + ">: " + source, e);
		}
	}
}
