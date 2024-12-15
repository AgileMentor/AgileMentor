package agilementor.backlog.controller;

import agilementor.backlog.dto.request.BacklogCreateRequest;
import agilementor.backlog.dto.request.BacklogUpdateRequest;
import agilementor.backlog.dto.response.BacklogCreateResponse;
import agilementor.backlog.dto.response.BacklogGetResponse;
import agilementor.backlog.dto.response.BacklogUpdateResponse;
import agilementor.backlog.service.BacklogService;
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

@RestController
@RequestMapping("/api/projects/{projectId}/backlogs")
public class BacklogController {

    private final BacklogService backlogService;

    public BacklogController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BacklogCreateResponse createBacklog(
        @Valid @RequestBody BacklogCreateRequest backlogCreateRequest, @PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.createBacklog(memberId, projectId, backlogCreateRequest);
    }

    @GetMapping
    public List<BacklogGetResponse> getBacklogList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.getBacklogList(memberId, projectId);
    }

    @GetMapping("/{backlogId}")
    public BacklogGetResponse getBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId) {

        return backlogService.getBacklog(memberId, projectId, backlogId);
    }

    @PutMapping("/{backlogId}")
    public BacklogUpdateResponse updateBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId,
        @Valid @RequestBody BacklogUpdateRequest backlogUpdateRequest) {

        return backlogService.updateBacklog(memberId, projectId, backlogId, backlogUpdateRequest);
    }

    @DeleteMapping("/{backlogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId) {

        backlogService.deleteBacklog(memberId, projectId, backlogId);
    }

    @GetMapping("/active")
    public List<BacklogGetResponse> getActiveBacklogList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.getActiveBacklogList(memberId, projectId);
    }
}
