package agilementor.sprint.dto;

import java.time.LocalDate;

public class CompletedSprintData {

    private long sprintId;
    private LocalDate startDate; // 스프린트 시작 날짜
    private LocalDate endDate; // 스프린트 종료 날짜
    private long remainingBacklogs; // 종료일 기준 프로젝트 전체 남은 백로그 수
    private long completedInSprint; // 해당 스프린트에서 완료된 백로그 수
    private boolean isActive; // 활성 스프린트 여부

    public CompletedSprintData(long sprintId, LocalDate startDate, LocalDate endDate,
        long remainingBacklogs, long completedInSprint, boolean isActive) {
        this.sprintId = sprintId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingBacklogs = remainingBacklogs;
        this.completedInSprint = completedInSprint;
        this.isActive = isActive;
    }

    // Getters
    public long getSprintId() {
        return sprintId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public long getRemainingBacklogs() {
        return remainingBacklogs;
    }

    public long getCompletedInSprint() {
        return completedInSprint;
    }

    public boolean isActive() {
        return isActive;
    }
}