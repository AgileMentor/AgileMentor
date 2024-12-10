package agilementor.chatgpt.dto;

import jakarta.validation.constraints.NotNull;

// GPT 요청 데이터
public class GPTRequest {

    @NotNull
    private String projectDescription; // 프로젝트 설명
    @NotNull
    private Integer storyCount; // 유저 스토리 개수
    @NotNull
    private Integer sprintCount; // 스프린트 개수

    // 기본 생성자 (필수)
    public GPTRequest() {
    }

    // 생성자
    public GPTRequest(String projectDescription, Integer storyCount, Integer sprintCount) {
        this.projectDescription = projectDescription;
        this.storyCount = storyCount;
        this.sprintCount = sprintCount;
    }

    // Getter 메서드
    public String getProjectDescription() {
        return projectDescription;
    }

    public Integer getStoryCount() {
        return storyCount;
    }

    public Integer getSprintCount() {
        return sprintCount;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setStoryCount(Integer storyCount) {
        this.storyCount = storyCount;
    }

    public void setSprintCount(Integer sprintCount) {
        this.sprintCount = sprintCount;
    }
}