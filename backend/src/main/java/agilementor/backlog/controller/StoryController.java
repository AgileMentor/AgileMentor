package agilementor.backlog.controller;

import agilementor.backlog.dto.request.StoryCreateRequest;
import agilementor.backlog.dto.request.StoryUpdateRequest;
import agilementor.backlog.dto.response.StoryCreateResponse;
import agilementor.backlog.dto.response.StoryGetResponse;
import agilementor.backlog.dto.response.StoryUpdateResponse;
import agilementor.backlog.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "스토리", description = "스토리 관련 api입니다.")
@RestController
@RequestMapping("/api/projects/{projectId}/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "스토리 생성", description = "프로젝트에 새로운 스토리를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "스토리 생성 성공")
    public StoryCreateResponse createStory(
        @Valid @RequestBody StoryCreateRequest storyCreateRequest, @PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.createStory(memberId, projectId, storyCreateRequest);
    }

    @GetMapping
    @Operation(summary = "스토리 목록 조회", description = "프로젝트의 스토리 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "스토리 목록 조회 성공")
    public List<StoryGetResponse> getStoryList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.getStoryList(memberId, projectId);
    }

    @GetMapping("/{storyId}")
    @Operation(summary = "스토리 조회", description = "프로젝트의 스토리 하나를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "스토리 조회 성공")
    public StoryGetResponse getStory(@PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.getStory(memberId, projectId, storyId);
    }

    @PutMapping("/{storyId}")
    @Operation(summary = "스토리 수정", description = "프로젝트의 스토리 하나를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "스토리 수정 성공")
    public StoryUpdateResponse updateStory(
        @Valid @RequestBody StoryUpdateRequest storyUpdateRequest,
        @PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        return storyService.updateStory(memberId, projectId, storyId, storyUpdateRequest);
    }

    @DeleteMapping("/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "스토리 삭제", description = "프로젝트의 스토리 하나를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "스토리 삭제 성공")
    public void deleteStory(@PathVariable Long projectId, @PathVariable Long storyId,
        @SessionAttribute("memberId") Long memberId) {

        storyService.deleteStory(memberId, projectId, storyId);
    }
}
