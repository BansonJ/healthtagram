package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/signup") //회원가입
    public ResponseEntity signup(@Valid @RequestBody SignupRequest signupDto) {
        if (!signupDto.getPassword().equals(signupDto.getCheckPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupDto);
        }

        memberService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin") //로그인
    public ResponseEntity signin(@RequestBody LoginRequest loginDto) {
        String token = memberService.signin(loginDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer" + token).build();
    }

    @GetMapping("/success") //성공 여부 확인용 컨트롤러
    public ResponseEntity seccess() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/myPage")  //내 정보 보기   ;포스트 나타낼 필요성;
    public ResponseEntity myPage(Principal principal) {
        return ResponseEntity.ok(memberService.myPage(principal));
    }

    @GetMapping("/memberPage/{nickname}")  //유저 정보 보기   ;포스트 나타낼 필요성;   ;팔로우 여부 확인;
    public ResponseEntity memberPage(@PathVariable(name = "nickname") String nickname) {
        MemberDto memberDto = memberService.memberPage(nickname);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/nicknameSearching")   //유저 닉네임 검색  ;검색 시 팔로우 관련 정보 필요;
    public ResponseEntity nicknameSearch(@RequestParam(name = "search") String search, @PageableDefault Pageable pageable) {
        SearchPageDto searchPageDto = memberService.search(pageable, search);

        return ResponseEntity.ok(searchPageDto);
    }

    @GetMapping("/insertData")  //데이터 30개 삽입
    public ResponseEntity insertData() {
        memberService.insertData();
        return ResponseEntity.ok().build();
    }
}
