package agilementor.chatgpt.dto;

import agilementor.backlog.entity.Status;
import agilementor.backlog.entity.Story;

public class StoryResponseDTO {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private Status status;

    public StoryResponseDTO(Long id, Long projectId, String title, String description,
        Status status) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public static StoryResponseDTO from(Story story) {
        return new StoryResponseDTO(
            story.getStoryId(),
            story.getProject().getProjectId(),
            story.getTitle(),
            story.getDescription(),
            Status.IN_PROGRESS
        );
    }
}
