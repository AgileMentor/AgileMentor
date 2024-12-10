package agilementor.chatgpt.dto;

import java.util.List;

public class ProjectResponseDTO {

    private List<StoryResponseDTO> stories;
    private List<SprintResponseDTO> sprints;
    private List<BacklogResponseDTO> backlogs;

    public ProjectResponseDTO(List<StoryResponseDTO> stories, List<SprintResponseDTO> sprints,
        List<BacklogResponseDTO> backlogs) {

        this.stories = stories;
        this.sprints = sprints;
        this.backlogs = backlogs;
    }

    public List<StoryResponseDTO> getStories() {
        return stories;
    }

    public List<SprintResponseDTO> getSprints() {
        return sprints;
    }

    public List<BacklogResponseDTO> getBacklogs() {
        return backlogs;
    }
}