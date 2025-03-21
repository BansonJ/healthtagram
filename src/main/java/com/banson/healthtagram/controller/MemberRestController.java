package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.LoginRequestDto;
import com.banson.healthtagram.dto.MemberResponseDto;
import com.banson.healthtagram.dto.SearchPageResponseDto;
import com.banson.healthtagram.dto.SignupRequestDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberRestController {

    private final MemberService memberService;
    private final PostService postService;
    private final FollowService followService;
    private final BCryptPasswordEncoder passwordEncoder;

    private Member findUser() { //내 Member 정보
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/signup") //회원가입
    public ResponseEntity signup(@Valid @RequestPart(name = "signupDto") SignupRequestDto signupDto, @RequestPart(name = "multipartFile") MultipartFile multipartFile) throws IOException {
        Member member = memberService.signup(signupDto, multipartFile);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupDto);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/profile")
    public ResponseEntity profile(@RequestParam(name = "nickname") String nickname) throws IOException {
        Member member = memberService.findByNickname(nickname);
        Resource resource = null;
        HttpHeaders header = new HttpHeaders();

        if (member.getProfilePicture() != null) {
            resource = new FileSystemResource(member.getProfilePicture());
            Path filePath = Paths.get(member.getProfilePicture());
            header.add("Content-Type", Files.probeContentType(filePath));
        }

        return new ResponseEntity(resource, header, HttpStatus.OK);
    }

    @PostMapping("/signin") //로그인
    public ResponseEntity signin(@Valid @RequestBody LoginRequestDto loginDto) {
        String token = memberService.signin(loginDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer" + token).build();
    }

    @GetMapping("/memberPage/{nickname}")  //유저 정보 보기
    public ResponseEntity memberPage(@PathVariable(name = "nickname") String nickname, @PageableDefault(size = 3) Pageable pageable,
                                     HttpServletRequest request) {
        Member me = findUser();
        Member member = memberService.findByNickname(nickname);
        List<Post> post = postService.findByNickname(member.getNickname(), pageable);

        ArrayList<String> filePath = new ArrayList<>();
        for (Post post1 : post) {
            filePath.add(post1.getFilePath().get(0));
        }

        String followState;

        if (me != null && me.equals(member)) {
            followState = "me";
        } else {
            followState = followService.followState(me, member);
        }

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .nickname(member.getNickname())
                .profilePicture(member.getProfilePicture())
                .filePath(filePath)
                .followState(followState).build();
        return ResponseEntity.ok(memberResponseDto);
    }

    @GetMapping("/nicknameSearching")   //유저 닉네임 검색  ;검색 시 팔로우 관련 정보 필요;
    public ResponseEntity nicknameSearch(@RequestParam(name = "search") String search, @PageableDefault(size = 5) Pageable pageable, HttpServletRequest request) {
        SearchPageResponseDto searchPageDto = memberService.search(search, pageable, findUser());

        return ResponseEntity.ok(searchPageDto);
    }

    @GetMapping("/insertData")  //데이터 30개 삽입
    public ResponseEntity insertData() {
        List<Member> members = new ArrayList<>();

        log.info("data 만들기 시작");
        for(long i=1; i<=1000; i++){
            members.add(Member.builder()
                    .id(i)
                    .email("wjdtmdgus@naver.com" + i)
                    .password(passwordEncoder.encode("asdfdsfsaf" + i))
                    .name("정승현" + i)
                    .nickname("banson" + i)
                    .profilePicture("profilePic" + i)
                    .build());
        }
        log.info("data 만들기 끝");

        memberService.insertData(members);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jmeterTest")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("test ok");
    }
}
