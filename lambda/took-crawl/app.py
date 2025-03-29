import json

def lambda_handler(event, context):
    try:
        # SQS 이벤트에서 레코드 추출
        records = event.get('Records', [])
        logger.info(f"Received {len(records)} records from SQS")

        responses = []
        for record in records:
            # 각 메시지의 body 파싱
            try:
                message_body = json.loads(record['body'])
                message_id = record['messageId']
                logger.info(f"Processing message ID: {message_id}")

                # 여기에 메시지 처리 로직 추가
                # 예: 크롤링 작업 수행
                process_message(message_body)

                responses.append({
                    'messageId': message_id,
                    'status': 'processed'
                })

            except Exception as e:
                logger.error(f"Error processing message: {str(e)}")
                responses.append({
                    'messageId': record.get('messageId', 'unknown'),
                    'status': 'failed',
                    'error': str(e)
                })

        return {
            "statusCode": 200,
            "body": json.dumps({
                "message": f"Processed {len(responses)} messages",
                "results": responses
            })
        }

    except Exception as e:
        logger.error(f"Error in lambda handler: {str(e)}")
        return {
            "statusCode": 500,
            "body": json.dumps({
                "message": "Error processing SQS messages",
                "error": str(e)
            })
        }

def process_message(message):
    """
    SQS 메시지를 처리하는 함수
    여기에 크롤링 로직을 구현하세요
    """
    logger.info(f"Processing message content: {message}")
    # 크롤링 작업 예시:
    # - 웹 페이지 요청
    # - 데이터 추출
    # - 결과 저장

    # 예시 구현 (실제 크롤링 로직으로 대체 필요)
    logger.info("Message processing completed")
    return True
