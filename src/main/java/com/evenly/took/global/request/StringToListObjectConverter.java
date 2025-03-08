package com.evenly.took.global.request;

import org.springframework.core.MethodParameter;
import org.springframework.http.@Component
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
		if (source == null) {
			return null;
		}

		String stringValue = (String) source;
		if (stringValue.isEmpty()) {
			return Collections.emptyList();
		}

		try {
			// List의 제네릭 타입 가져오기
			Class<?> elementType = targetType.getElementTypeDescriptor().getType();

			// TypeFactory를 사용하여 제네릭 타입 생성
			JavaType javaType = objectMapper.getTypeFactory()
				.constructCollectionType(List.class, elementType);

			// JSON 문자열을 List<T>로 변환
			return objectMapper.readValue(stringValue, javaType);
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"Failed to convert String to List<" +
					targetType.getElementTypeDescriptor().getType().getSimpleName() + ">: " + source, e);
		}
	}
}
