package com.evenly.took.global.aws.sqs;

import java.util.Map;

public interface SQSService {
	/**
	 * 메시지를 SQS 큐로 전송합니다.
	 *
	 * @param messageBody 전송할 메시지 본문
	 * @param queueName 대상 큐 이름
	 * @return 메시지 ID
	 */
	String sendMessage(String messageBody, String queueName);

	/**
	 * 메시지를 SQS 큐로 전송하고 속성을 함께 설정합니다.
	 *
	 * @param messageBody 전송할 메시지 본문
	 * @param attributes 메시지 속성
	 * @param queueName 대상 큐 이름
	 * @return 메시지 ID
	 */
	String sendMessage(String messageBody, Map<String, String> attributes, String queueName);

}
