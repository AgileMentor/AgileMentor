package agilementor.chatgpt.dto;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;

public class BacklogResponseDTO {

    private Long id;
    private Long projectId;
    private Long sprintId;
    private Long storyId;
    private Long memberId;
    private String title;
    private String description;
    private Status status;
    private Priority priority;

    public BacklogResponseDTO(Long id, Long projectId, Long sprintId, Long storyId, String title,
        String description, Status status, Priority priority) {
        this.id = id;
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.storyId = storyId;
        this.memberId = null;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getDescription() {
        return description;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public Long getStoryId() {
        return storyId;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public static BacklogResponseDTO from(Backlog backlog) {
        return new BacklogResponseDTO(
            backlog.getBacklogId(),
            backlog.getProject().getProjectId(),
            backlog.getSprint().getId(),
            backlog.getStory().getStoryId(),
            backlog.getTitle(),
            backlog.getDescription(),
            backlog.getStatus(),
            backlog.getPriority()
        );
    }
}