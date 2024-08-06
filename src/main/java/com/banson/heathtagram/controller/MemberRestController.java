package com.banson.heathtagram.controller;

import com.banson.heathtagram.dto.LoginDto;
import com.banson.heathtagram.dto.MemberDto;
import com.banson.heathtagram.dto.SignupDto;
import com.banson.heathtagram.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupDto signupDto) {
        if (!signupDto.getPassword().equals(signupDto.getCheckPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupDto.getEmail());
        }

        memberService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody LoginDto loginDto) {
        String token = memberService.signin(loginDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer" + token).build();
    }

    @GetMapping("/success")
    public ResponseEntity seccess() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/myPage")
    public ResponseEntity myPage(Principal principal) {
        return ResponseEntity.ok(memberService.myPage(principal));
    }

    @GetMapping("/memberPage")
    public ResponseEntity memberPage(@RequestBody Map<String ,String> nicknameMap) {
        MemberDto memberDto = memberService.memberPage(nicknameMap.get("nickname"));
        return ResponseEntity.ok(memberDto);
    }
}
