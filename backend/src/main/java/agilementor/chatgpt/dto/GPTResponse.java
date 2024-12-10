package agilementor.chatgpt.dto;

import agilementor.backlog.entity.Priority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GPTResponse {

    // 기존 필드
    private List<StoryDTO> stories;
    private List<SprintDTO> sprints;
    private Choice[] choices;

    public GPTResponse() {

    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public List<SprintDTO> getSprints() {
        return sprints;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public void setChoices(Choice[] choices) {
        this.choices = choices;

    }

    public void setStories(List<StoryDTO> stories) {
        this.stories = stories;
    }

    public void setSprints(List<SprintDTO> sprints) {
        this.sprints = sprints;
    }

    // 기존 Inner Classes
    public static class StoryDTO {

        private Long id;
        private String title;
        private String description;
        private List<TaskDTO> tasks;

        // 기본 생성자 추가
        public StoryDTO() {
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<TaskDTO> getTasks() {
            return tasks;
        }
    }

    public static class TaskDTO {

        private String title;
        private String description;
        private Priority priority;
        private Long sprintId;

        public TaskDTO() {
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Priority getPriority() {
            return priority;
        }

        public Long getSprintId() {
            return sprintId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SprintDTO {

        private Long id;
        private String title;
        private String goal;

        public SprintDTO() {
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getGoal() {
            return goal;
        }
    }

    // 추가된 Inner Classes: OpenAI API 응답 구조 처리
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {

        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}