package com.banson.healthtagram.controller;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowingRestController {

    private final FollowService followService;
    private final MemberService memberService;

    private Member findUser() { //내 Member 정보
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/follow/{nickname}")   //팔로우 하기
    public ResponseEntity following(@PathVariable(name = "nickname") String nickname, HttpServletRequest request) {
        Member following = memberService.findByNickname(nickname);
        Member follower = findUser();
        followService.following(following, follower);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/followingList")   //팔로잉 리스트
    public ResponseEntity followingList(HttpServletRequest request) {
        Member member = findUser();
        return ResponseEntity.ok(followService.followingList(member));
    }

    @GetMapping("/followerList")    //날 팔로우 하는 사람 리스트
    public ResponseEntity followerList(HttpServletRequest request) {
        Member member = findUser();
        return ResponseEntity.ok(followService.followerList(member));
    }

    @DeleteMapping("/unfollowing/{nickname}")   //언팔하기
    public ResponseEntity unfollowing(@PathVariable(name = "nickname") String nickname, HttpServletRequest request) {
        Member following = memberService.findByNickname(nickname);
        Member follower = findUser();

        followService.unfollowing(following, follower);

        return ResponseEntity.ok().build();
    }
}
