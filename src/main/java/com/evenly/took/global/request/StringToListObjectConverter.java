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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
		if (source == null) {
			return Collections.emptyList();
		}

		String stringValue = (String)source;
		if (stringValue.isEmpty()) {
			return Collections.emptyList();
		}

		try {
			stringValue = stringValue.trim();
			Class<?> elementType = targetType.getElementTypeDescriptor().getType();
			JavaType javaType = objectMapper.getTypeFactory()
				.constructCollectionType(List.class, elementType);

			if (stringValue.startsWith("\"[") && stringValue.endsWith("]\"")) {
				stringValue = stringValue.substring(1, stringValue.length() - 1)
					.replace("\\\"", "\"")
					.replace("\\n", "")
					.replace("\\\\", "\\");
			}

			if (stringValue.startsWith("[") && stringValue.endsWith("]")) {
				try {
					JsonNode arrayNode = objectMapper.readTree(stringValue);

					if (arrayNode.isArray()) {
						ArrayNode newArrayNode = objectMapper.createArrayNode();

						for (JsonNode node : arrayNode) {
							if (node.isTextual()) {
								String nodeText = node.asText();
								if (nodeText.trim().startsWith("{") && nodeText.trim().endsWith("}")) {
									try {
										JsonNode objNode = objectMapper.readTree(nodeText);
										newArrayNode.add(objNode);
									} catch (Exception e) {
										newArrayNode.add(node);
									}
								} else {
									newArrayNode.add(node);
								}
							} else {
								newArrayNode.add(node);
							}
						}

						return objectMapper.readValue(
							objectMapper.writeValueAsString(newArrayNode),
							javaType
						);
					}
				} catch (Exception e) {
					// 실패 시 계속 진행
				}
			} else if (stringValue.startsWith("{") && stringValue.endsWith("}")) {
				stringValue = "[" + stringValue + "]";
			} else if (stringValue.startsWith("{") && stringValue.contains("},{")) {
				stringValue = "[" + stringValue + "]";
			}

			try {
				return objectMapper.readValue(stringValue, javaType);
			} catch (Exception e) {
				if (stringValue.contains(",")) {
					if (elementType == String.class) {
						return Arrays.stream(stringValue.split(","))
							.map(String::trim)
							.filter(s -> !s.isEmpty())
							.collect(Collectors.toList());
					} else {
						JavaType elementJavaType = objectMapper.getTypeFactory().constructType(elementType);

						return Arrays.stream(stringValue.split(","))
							.map(String::trim)
							.filter(s -> !s.isEmpty())
							.map(item -> {
								try {
									if (!item.startsWith("\"") && !item.startsWith("{")) {
										item = "\"" + item + "\"";
									}
									return objectMapper.readValue(item, elementJavaType);
								} catch (Exception ex) {
									throw new IllegalArgumentException(
										"Failed to convert item '" + item + "' to " + elementType.getSimpleName(), ex);
								}
							})
							.collect(Collectors.toList());
					}
				}

				try {
					Object singleObject = objectMapper.readValue(stringValue, elementType);
					return Collections.singletonList(singleObject);
				} catch (Exception ex) {
					throw e;
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"Failed to convert String to List<" +
					targetType.getElementTypeDescriptor().getType().getSimpleName() + ">: " + source, e);
		}
	}
}
