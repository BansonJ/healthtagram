package com.banson.heathtagram.controller;

import com.banson.heathtagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.SourceVersion;
import javax.swing.filechooser.FileSystemView;
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
