package com.evenly.took.feature.card.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "여러 개의 받은 명함에 한줄 메모를 추가하기 위한 요청", requiredMode = Schema.RequiredMode.REQUIRED)
public record SetReceivedCardsMemoRequest(
    @NotEmpty
    @Schema(description = "메모를 추가할 명함 정보 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, message = "최소 하나 이상의 명함 정보가 필요합니다")
    List<@Valid CardMemo> cardMemos
) {
    @Schema(description = "명함과 메모 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    public record CardMemo(
        @NotNull
        @Schema(description = "명함 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long cardId,

        @Schema(description = "메모 내용", example = "디프만에서 만난 개발자")
        String memo
    ) {
    }
}
