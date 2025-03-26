package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.SearchResponseDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class FollowingRestController {

    private final FollowService followService;
    private final MemberService memberService;

    private Member findUser(String nickname) { //내 Member 정보
        return memberService.findByNickname(nickname);
    }

    @PostMapping("/follow/{nickname}")   //팔로우 하기
    public ResponseEntity following(@PathVariable(name = "nickname") String nickname, @RequestParam(name = "storedNickname") String storedNickname) {
        Member following = memberService.findByNickname(nickname);
        Member follower = findUser(storedNickname);
        followService.following(follower, following);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{nickname}")   //언팔하기
    public ResponseEntity unfollowing(@PathVariable(name = "nickname") String nickname, @RequestParam(name = "storedNickname") String storedNickname) {
        Member following = memberService.findByNickname(nickname);
        Member follower = findUser(storedNickname);

        followService.unfollowing(following, follower);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/followingList")   //팔로잉 리스트
    public ResponseEntity followingList(@PageableDefault(size = 3) Pageable pageable, @RequestParam(name = "nickname") String nickname) {
        Member member = findUser(nickname);
        List<SearchResponseDto> searchResponseDtoList = followService.followingList(member, pageable);
        log.info("{}", searchResponseDtoList.get(0).getNickname());
        return ResponseEntity.ok(searchResponseDtoList);
    }

    @GetMapping("/followerList")    //날 팔로우 하는 사람 리스트
    public ResponseEntity followerList(@PageableDefault(size = 3) Pageable pageable, @RequestParam(name = "nickname") String nickname) {
        Member member = findUser(nickname);
        List<SearchResponseDto> searchResponseDtoList = followService.followerList(member, pageable);
        log.info("{}", searchResponseDtoList.get(0).getNickname());
        return ResponseEntity.ok(searchResponseDtoList);
    }
}
