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

@Tag(name = "백로그", description = "백로그 관련 api입니다.")
@RestController
@RequestMapping("/api/projects/{projectId}/backlogs")
public class BacklogController {

    private final BacklogService backlogService;

    public BacklogController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "백로그 생성", description = "프로젝트에 새로운 백로그를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "백로그 생성 성공")
    public BacklogCreateResponse createBacklog(
        @Valid @RequestBody BacklogCreateRequest backlogCreateRequest, @PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.createBacklog(memberId, projectId, backlogCreateRequest);
    }

    @GetMapping
    @Operation(summary = "백로그 목록 조회", description = "프로젝트의 백로그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "백로그 목록 조회 성공")
    public List<BacklogGetResponse> getBacklogList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.getBacklogList(memberId, projectId);
    }

    @GetMapping("/{backlogId}")
    @Operation(summary = "백로그 조회", description = "프로젝트의 백로그 하나를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "백로그 조회 성공")
    public BacklogGetResponse getBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId) {

        return backlogService.getBacklog(memberId, projectId, backlogId);
    }

    @PutMapping("/{backlogId}")
    @Operation(summary = "백로그 수정", description = "프로젝트의 백로그 하나를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "백로그 수정 성공")
    public BacklogUpdateResponse updateBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId,
        @Valid @RequestBody BacklogUpdateRequest backlogUpdateRequest) {

        return backlogService.updateBacklog(memberId, projectId, backlogId, backlogUpdateRequest);
    }

    @DeleteMapping("/{backlogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "백로그 삭제", description = "프로젝트의 백로그 하나를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "백로그 삭제 성공")
    public void deleteBacklog(@PathVariable Long projectId,
        @PathVariable Long backlogId, @SessionAttribute("memberId") Long memberId) {

        backlogService.deleteBacklog(memberId, projectId, backlogId);
    }

    @GetMapping("/active")
    @Operation(summary = "활성 스프린트의 백로그 목록 조회", description = "프로젝트에서 시작된 스프린트의 백로그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "활성 스프린트의 백로그 목록 조회 성공")
    public List<BacklogGetResponse> getActiveBacklogList(@PathVariable Long projectId,
        @SessionAttribute("memberId") Long memberId) {

        return backlogService.getActiveBacklogList(memberId, projectId);
    }
}
