package com.evenly.blok.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[0. 헬스체크]")
public interface HealthApi {

	@Operation(summary = "헬스 체크 API 입니다.")
	@ApiResponse(responseCode = "200", description = "서버가 정상적으로 동작할 시 ok를 응답합니다.",
		content = @Content(mediaType = "text/html;charset=UTF-8",
			schema = @Schema(type = "string", example = "ok")))
	String healthCheck();
}
