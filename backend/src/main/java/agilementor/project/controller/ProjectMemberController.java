package agilementor.project.controller;

import agilementor.common.annotation.LoginMemberId;
import agilementor.project.dto.request.ProjectInviteRequest;
import agilementor.project.dto.response.ProejctMemberResponse;
import agilementor.project.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트 회원", description = "프로젝트 회원 관련 api입니다.")
@RestController
@RequestMapping("/api/projects/{projectId}")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping("/members")
    @Operation(summary = "프로젝트 회원 목록 조회", description = "프로젝트에 참가한 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 회원 목록 조회 성공")
    public List<ProejctMemberResponse> getProjectMemberList(@LoginMemberId Long memberId,
        @PathVariable Long projectId) {

        return projectMemberService.getProjectMemberList(memberId, projectId);
    }

    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "프로젝트 회원 추방", description = "프로젝트에 참가한 회원 한 명을 추방합니다.")
    @ApiResponse(responseCode = "204", description = "프로젝트 회원 추방 성공")
    public void kickMember(@LoginMemberId Long loginMemberId, @PathVariable Long projectId,
        @PathVariable Long memberId) {

        projectMemberService.kickMember(loginMemberId, projectId, memberId);
    }

    @PostMapping("/invitations")
    @Operation(summary = "프로젝트 회원 초대", description = "프로젝트에 회원 한 명을 초대합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 회원 초대 성공")
    public void inviteMember(@LoginMemberId Long loginMemberId, @PathVariable Long projectId,
        @RequestBody ProjectInviteRequest projectInviteRequest) {

        projectMemberService.inviteMember(loginMemberId, projectId, projectInviteRequest);
    }
}
