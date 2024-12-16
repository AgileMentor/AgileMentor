package agilementor.project.controller;

import agilementor.common.annotation.LoginMemberId;
import agilementor.project.dto.response.InvitationGetResponse;
import agilementor.project.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "초대", description = "초대 관련 api입니다.")
@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    @Operation(summary = "초대 알림 조회", description = "초대 알림 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "초대 알림 조회 성공")
    public List<InvitationGetResponse> getInvitationList(
        @LoginMemberId Long memberId) {

        return invitationService.getInvitationList(memberId);
    }

    @PostMapping("/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "초대 수락", description = "초대를 수락합니다.")
    @ApiResponse(responseCode = "204", description = "초대 수락 성공")
    public void acceptInvitation(@PathVariable Long invitationId,
        @LoginMemberId Long memberId) {

        invitationService.acceptInvitation(memberId, invitationId);
    }

    @PostMapping("/{invitationId}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "초대 거절", description = "초대를 거절합니다.")
    @ApiResponse(responseCode = "204", description = "초대 거절 성공")
    public void declineInvitation(@PathVariable Long invitationId,
        @LoginMemberId Long memberId) {

        invitationService.declineInvitation(memberId, invitationId);
    }
}
