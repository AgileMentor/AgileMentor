package agilementor.project.controller;

import agilementor.common.annotation.LoginMemberId;
import agilementor.project.dto.request.ProjectCreateRequest;
import agilementor.project.dto.request.ProjectUpdateRequest;
import agilementor.project.dto.response.ProjectResponse;
import agilementor.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "프로젝트", description = "프로젝트 관련 api입니다.")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공")
    public ProjectResponse createProject(@RequestBody ProjectCreateRequest projectCreateRequest,
        @LoginMemberId Long memberId) {

        return projectService.createProject(memberId, projectCreateRequest);
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록 조회", description = "참가한 프로젝트 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공")
    public List<ProjectResponse> getProjectList(@LoginMemberId Long memberId) {

        return projectService.getProjectList(memberId);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 조회", description = "참가한 프로젝트 하나를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 조회 성공")
    public ProjectResponse getProject(@LoginMemberId Long memberId,
        @PathVariable Long projectId) {

        return projectService.getProject(memberId, projectId);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 수정", description = "참가한 프로젝트 하나를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 수정 성공")
    public ProjectResponse updateProject(@LoginMemberId Long memberId, @PathVariable Long projectId,
        @RequestBody ProjectUpdateRequest projectUpdateRequest) {

        return projectService.updateProject(memberId, projectId, projectUpdateRequest);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "프로젝트 삭제", description = "참가한 프로젝트 하나를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공")
    public void deleteProject(@LoginMemberId Long memberId, @PathVariable Long projectId) {

        projectService.deleteProject(memberId, projectId);
    }

    @PostMapping("/{projectId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "프로젝트 탈퇴", description = "참가한 프로젝트에서 탈퇴합니다.")
    @ApiResponse(responseCode = "204", description = "프로젝트 탈퇴 성공")
    public void leaveProject(@LoginMemberId Long memberId, @PathVariable Long projectId) {

        projectService.leaveProject(memberId, projectId);
    }
}
