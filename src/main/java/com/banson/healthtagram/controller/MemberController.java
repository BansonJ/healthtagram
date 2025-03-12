package com.banson.healthtagram.controller;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private Member findUser() { //내 Member 정보
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/cancelAccount")
    public String deleteUser(HttpServletRequest request) {
        Member user = findUser();

        memberService.cancelAccount(user);
        log.info("principal = {}", user.getName());

        return "redirect:/api/signup";
    }
}
