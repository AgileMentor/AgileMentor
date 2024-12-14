package agilementor.backlog.dto.request;

import agilementor.backlog.entity.Priority;
import jakarta.validation.constraints.NotNull;

public record BacklogCreateRequest(
    @NotNull String title,
    @NotNull String description,
    @NotNull Priority priority,
    Long sprintId,
    Long storyId,
    Long memberId
) {

}
