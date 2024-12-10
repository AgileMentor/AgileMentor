package agilementor.chatgpt.dto;

import agilementor.sprint.entity.Sprint;
import java.time.LocalDate;

public class SprintResponseDTO {

    private Long id;
    private Long projectId;
    private String title;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isDone;
    private boolean isActivate;

    public SprintResponseDTO(Long id, Long projectId, String title, String goal) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.goal = goal;
        this.startDate = null;
        this.endDate = null;
        this.isDone = false;
        this.isActivate = false;
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

    public String getGoal() {
        return goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isActivate() {
        return isActivate;
    }

    public static SprintResponseDTO from(Sprint sprint) {
        return new SprintResponseDTO(
            sprint.getId(),
            sprint.getProject().getProjectId(),
            sprint.getTitle(),
            sprint.getGoal()
        );
    }
}
