package agilementor.backlog.dto.request;

import jakarta.validation.constraints.NotNull;

public record StoryUpdateRequest(
    @NotNull String title,
    @NotNull String description
) {
}
