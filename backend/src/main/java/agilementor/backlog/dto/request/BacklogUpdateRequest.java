package agilementor.backlog.dto.request;

import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import jakarta.validation.constraints.NotNull;

public record BacklogUpdateRequest(
    @NotNull String title,
    @NotNull String description,
    @NotNull Status status,
    @NotNull Priority priority,
    Long sprintId,
    Long storyId,
    Long memberId
) {

}