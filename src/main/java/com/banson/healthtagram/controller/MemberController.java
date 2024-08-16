package com.banson.healthtagram.controller;

import com.banson.healthtagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @DeleteMapping("/cancelAccount")
    public String deleteUser(Principal principal) {
        memberService.cancelAccount(principal);
        log.info("principal = {}", principal.getName());

        return "redirect:/api/home";
    }
}
