package agilementor.project.controller;

import agilementor.project.dto.request.ProjectInviteRequest;
import agilementor.project.dto.response.ProejctMemberResponse;
import agilementor.project.service.ProjectMemberService;
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
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/projects/{projectId}")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping("/members")
    public List<ProejctMemberResponse> getProjectMemberList(
        @SessionAttribute("memberId") Long memberId,
        @PathVariable Long projectId) {

        return projectMemberService.getProjectMemberList(memberId, projectId);
    }

    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickMember(@SessionAttribute("memberId") Long loginMemberId,
        @PathVariable Long projectId, @PathVariable Long memberId) {

        projectMemberService.kickMember(loginMemberId, projectId, memberId);
    }

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.OK)
    public void inviteMember(@SessionAttribute("memberId") Long loginMemberId,
        @PathVariable Long projectId, @RequestBody ProjectInviteRequest projectInviteRequest) {

        projectMemberService.inviteMember(loginMemberId, projectId, projectInviteRequest);
    }
}
