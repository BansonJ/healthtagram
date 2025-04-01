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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    @Value("${file.path}")
    private String getFilePath;

    private Member findUser(String nickname) { //내 Member 정보
        return memberService.findByNickname(nickname);
    }

    @PostMapping("/signup") //회원가입
    public ResponseEntity signup(@Valid @RequestPart(name = "signupDto") SignupRequestDto signupDto, @RequestPart(name = "multipartFile") MultipartFile multipartFile) throws IOException {
        Member member = memberService.signup(signupDto, multipartFile);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupDto);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin") //로그인
    @CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
    public ResponseEntity signin(@Valid @RequestBody LoginRequestDto loginDto) {
        String token = memberService.signin(loginDto);
        String nickname = memberService.findByEmail(loginDto.getEmail()).getNickname();
        log.info(token);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(nickname);
    }

    @GetMapping("/memberPage/{nickname}")  //유저 프로필 화면 보기
    public ResponseEntity<?> memberPage(@PathVariable(name = "nickname") String nickname,
                                        @RequestParam(name = "storedNickname") String storedNickname,
                                        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE+"") Long lastPostId,
                                        @PageableDefault(size = 3) Pageable pageable) {
        try {
            Member me = findUser(storedNickname);

            // 사용자 정보 찾기
            Member member = memberService.findByNickname(nickname);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("유저를 찾을 수 없습니다.");
            }

            // 유저의 게시물 찾기
            List<Post> post = postService.findPostInMember(member.getNickname(), lastPostId, pageable);

            // 게시물 파일 경로 생성
            ArrayList<String> filePath = new ArrayList<>();
            for (Post post1 : post) {
                String postFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(post1.getFilePath().get(0)).getName();
                filePath.add(postFileUrl);
            }

            // 팔로우 상태 확인
            String followState = "unfollow";
            if (me != null && me.equals(member)) {
                followState = "me";
            } else {
                followState = followService.followState(me, member);
            }

            log.info("{}", followState);

            // 프로필 사진 경로 생성
            String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(member.getProfilePicture()).getName();

            // 응답 객체 생성
            MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                    .nickname(member.getNickname())
                    .profilePicture(profileFileUrl)
                    .filePath(filePath)
                    .postList(post)
                    .followState(followState)
                    .follower((long) member.getFollowerList().size())
                    .following((long) member.getFollowingList().size())
                    .build();

            return ResponseEntity.ok(memberResponseDto);
        } catch (Exception e) {
            // 예외 발생 시 오류 로그와 함께 500 서버 오류 응답
            log.error("유저 프로필 로딩 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }


    @GetMapping("/nicknameSearching")   //유저 닉네임 검색
    public ResponseEntity nicknameSearch(@RequestParam(name = "search") String search, @RequestParam(name = "storedNickname") String storedNickname,
                                         @PageableDefault(size = 5) Pageable pageable) {
        Member me = findUser(storedNickname);
        SearchPageResponseDto searchPageDto = memberService.search(search, pageable, me);

        return ResponseEntity.ok(searchPageDto);
    }

    @GetMapping("/study/healthtagramImage/{imageName}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable(value = "imageName") String imageName) {
        try {
            Path filePath = Paths.get(getFilePath).resolve(imageName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                log.info("로드 성공!!!!!!");
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 또는 다른 이미지 형식
                        .body(resource);
            } else {
                throw new RuntimeException("이미지를 찾을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("이미지 로드 오류", e);
        }
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
    public ResponseEntity jmetertest() {
        return ResponseEntity.ok().body("test ok");
    }

    @GetMapping("/hello")
    public String test() {
        return "Hello, world!";
    }
}
