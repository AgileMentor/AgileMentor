package agilementor.member.controller;

import agilementor.member.service.AuthClientService;
import agilementor.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthClientService authClientService;
    private final MemberService memberService;

    public AuthController(AuthClientService authClientService, MemberService memberService) {
        this.authClientService = authClientService;
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String redirectGoogleLogin() {
        return "redirect:" + authClientService.getAuthUrl();
    }

    @GetMapping("/login/code/google")
    public String getGoogleAuthCode(@RequestParam(name = "code") String code,
        HttpSession httpSession) {
        String idToken;

        try {
            idToken = authClientService.requestIdToken(code);
        } catch (Exception e) {
            // todo: 로그인 실패 페이지 추가
            return "redirect:https://www.agilementor.kr/";
        }

        Long memberId = memberService.registerOrUpdateMember(idToken);
        httpSession.setAttribute("memberId", memberId);

        return "redirect:https://www.agilementor.kr/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:https://www.agilementor.kr/";
    }
}
