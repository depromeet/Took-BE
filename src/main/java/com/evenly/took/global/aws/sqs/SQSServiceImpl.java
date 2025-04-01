package com.evenly.took.global.aws.sqs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.evenly.took.global.aws.sqs.exception.SQSErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSServiceImpl implements SQSService {

	private final SqsClient sqsClient;

	@Override
	public String sendMessage(String messageBody, String queueName) {
		return sendMessage(messageBody, new HashMap<>(), queueName);
	}

	@Override
	public String sendMessage(String messageBody, Map<String, String> attributes, String queueName) {
		try {
			String queueUrl = getQueueUrl(queueName);

			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();

			// 메시지 속성 설정
			attributes.forEach((key, value) -> messageAttributes.put(key,
				MessageAttributeValue.builder()
					.dataType("String")
					.stringValue(value)
					.build()));

			SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
				.queueUrl(queueUrl)
				.messageBody(messageBody)
				.messageAttributes(messageAttributes)
				.build();

			SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

			log.info("Sent message to queue: {}, messageId: {}", queueName, response.messageId());
			return response.messageId();
		} catch (Exception e) {
			log.error("Error sending message to queue: {}", queueName, e);
			throw new TookException(SQSErrorCode.MESSAGE_SEND_ERROR);
		}
	}

	private String getQueueUrl(String queueName) {
		try {
			GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
				.queueName(queueName)
				.build();

			return sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();
		} catch (Exception e) {
			log.error("Error getting queue URL for queue: {}", queueName, e);
			throw new TookException(SQSErrorCode.QUEUE_URL_FETCH_ERROR);
		}
	}
}
