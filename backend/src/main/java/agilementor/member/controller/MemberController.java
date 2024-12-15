package agilementor.member.controller;

import agilementor.member.dto.response.MemberGetResponse;
import agilementor.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Tag(name = "회원", description = "회원 관련 api입니다.")
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "회원 프로필 조회", description = "현재 로그인된 회원 본인의 프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "구글 프로필 정보 조회 성공")
    public MemberGetResponse getLoginMemberInfo(@SessionAttribute("memberId") Long memberId) {

        return memberService.getMember(memberId);
    }
}
