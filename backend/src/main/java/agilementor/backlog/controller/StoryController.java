package agilementor.backlog.controller;

import agilementor.backlog.dto.request.StoryCreateRequest;
import agilementor.backlog.dto.request.StoryUpdateRequest;
import agilementor.backlog.dto.response.StoryCreateResponse;
import agilementor.backlog.dto.response.StoryGetResponse;
import agilementor.backlog.dto.response.StoryUpdateResponse;
import agilementor.backlog.service.StoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/projects/{projectId}/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryCreateResponse createStory(
        @Valid @RequestBody StoryCreateRequest storyCreateRequest, @PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.createStory(memberId, projectId, storyCreateRequest);
    }

    @GetMapping
    public List<StoryGetResponse> getStoryList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.getStoryList(memberId, projectId);
    }

    @GetMapping("/{storyId}")
    public StoryGetResponse getStory(@PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.getStory(memberId, projectId, storyId);
    }

    @PutMapping("/{storyId}")
    public StoryUpdateResponse updateStory(
        @Valid @RequestBody StoryUpdateRequest storyUpdateRequest,
        @PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.updateStory(memberId, projectId, storyId, storyUpdateRequest);
    }

    @DeleteMapping("/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        storyService.deleteStory(memberId, projectId, storyId);
    }
}
