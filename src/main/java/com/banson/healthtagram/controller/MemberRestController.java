package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;
    private final PostService postService;
    private final FollowService followService;

    private Member findUser() {
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/signup") //회원가입
    public ResponseEntity signup(@Valid @RequestPart(name = "signupDto") SignupRequest signupDto, @RequestPart(name = "multipartFile") MultipartFile multipartFile) {
        Member member = memberService.signup(signupDto, multipartFile);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupDto);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin") //로그인
    public ResponseEntity signin(@RequestBody LoginRequest loginDto) {
        String token = memberService.signin(loginDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer" + token).build();
    }

    @GetMapping("/success") //성공 여부 확인용 컨트롤러
    public ResponseEntity success() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/myPage")  //내 정보 보기
    public ResponseEntity myPage(@PageableDefault(size = 3) Pageable pageable) {
        Member member = findUser();
        List<Post> post = postService.findByNickname(findUser().getNickname(), pageable);

        ArrayList<String> filePath = new ArrayList<>();
        for (Post post1 : post) {
            filePath.add(post1.getFilePath().get(0));
        }

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .nickname(member.getNickname())
                .profilePicture(member.getProfilePicture())
                .filePath(filePath)
                .build();

        return ResponseEntity.ok(memberResponseDto);
    }

    @GetMapping("/memberPage/{nickname}")  //유저 정보 보기   ;빈 포스트일 시 괜찮은지;
    public ResponseEntity memberPage(@PathVariable(name = "nickname") String nickname, @PageableDefault(size = 3) Pageable pageable) {
        Member me = findUser();
        Member friend = memberService.findByNickname(nickname);
        List<Post> post = postService.findByNickname(friend.getNickname(), pageable);

        ArrayList<String> filePath = new ArrayList<>();
        for (Post post1 : post) {
            filePath.add(post1.getFilePath().get(0));
        }

        boolean followState = followService.followState(me, friend);
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .nickname(friend.getNickname())
                .profilePicture(friend.getProfilePicture())
                .filePath(filePath)
                .followState(followState).build();
        return ResponseEntity.ok(memberResponseDto);
    }

    @GetMapping("/nicknameSearching")   //유저 닉네임 검색  ;검색 시 팔로우 관련 정보 필요;
    public ResponseEntity nicknameSearch(@RequestParam(name = "search") String search, @PageableDefault Pageable pageable) {
        SearchPageDto searchPageDto = memberService.search(search, pageable, findUser());

        return ResponseEntity.ok(searchPageDto);
    }

    @GetMapping("/insertData")  //데이터 30개 삽입
    public ResponseEntity insertData() {
        memberService.insertData();
        return ResponseEntity.ok().build();
    }
}
