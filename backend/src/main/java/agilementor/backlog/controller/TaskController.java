package agilementor.backlog.controller;

import agilementor.backlog.dto.response.BacklogGetResponse;
import agilementor.backlog.service.BacklogService;
import agilementor.common.annotation.LoginMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "진행중인 작업", description = "진행중인 작업 관련 api입니다.")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final BacklogService backlogService;

    public TaskController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @GetMapping
    @Operation(summary = "진행중인 작업 조회", description = "전체 프로젝트에서 사용자에게 할당된 진행중인 백로그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "진행중인 작업 조회 성공")
    public List<BacklogGetResponse> getTasks(@LoginMemberId Long memberId) {
        return backlogService.getTasks(memberId);
    }
}
