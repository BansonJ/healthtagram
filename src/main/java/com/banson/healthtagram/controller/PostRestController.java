package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.PostRequestDto;
import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final MemberService memberService;

    private Member findUser() { //내 Member 정보
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/post")   //포스트 저장
    public ResponseEntity savePost(@RequestPart(value = "multipartFile") List<MultipartFile> multipartFile, @Valid @RequestPart(name = "postRequestDto") PostRequestDto postRequestDto,
                                   HttpServletRequest request) {
        PostResponseDto postResponseDto = postService.savePost(postRequestDto, multipartFile, findUser());


        return ResponseEntity.status(201).body(postResponseDto);
    }

    @GetMapping("/home")    //기본 페이지
    public ResponseEntity home(@RequestParam(name = "lastPostId") Long lastPostId, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest request) {
        Member member = findUser();
        List<Member> id = new ArrayList<>();

        if (member != null) {
            id.add(member);
            for (Follow follow : member.getFollowingList()) {
                id.add(follow.getFollowing());
            }
        }

        List<PostResponseDto> postList = postService.findPostInMember(lastPostId, member, id, pageable);

        return ResponseEntity.ok(postList);
    }

    @PutMapping("/likePost/{postId}")   //포스트 좋아요
    public ResponseEntity likePost(@PathVariable(name = "postId") Long postId, HttpServletRequest request) {
        postService.likePost(postId, findUser());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancelLikePost/{postId}")   //포스트 좋아요 취소
    public ResponseEntity cancelLikePost(@PathVariable(name = "postId") Long postId, HttpServletRequest request) {
        postService.cancelLikePost(postId, findUser());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/memberPage/{nickname}/post")  //특정 멤버 포스트
    public ResponseEntity memberPost(@PathVariable(name = "nickname") String nickname, @RequestParam(name = "lastPostId") Long lastPostId,
                                     @PageableDefault(size = 3) Pageable pageable, HttpServletRequest request) {
        List<Member> memberList = new ArrayList<>();
        memberList.add(memberService.findByNickname(nickname));
        List<PostResponseDto> postList = postService.findPostInMember(lastPostId, findUser(), memberList, pageable);

        return ResponseEntity.ok(postList);
    }

    @GetMapping("/tagSearching")    //태그로 검색하기
    public ResponseEntity tagSearch(@RequestParam(name = "tagSearching") String tagSearching, @RequestParam(name = "lastPostId") Long latPostId,
                                    @PageableDefault(size = 3) Pageable pageable, HttpServletRequest request) {
        List<PostResponseDto> postResponseDtoList = postService.tagSearching(tagSearching, latPostId, findUser(), pageable);

        return ResponseEntity.ok(postResponseDtoList);
    }

    @PostMapping("/insertPost")
    public ResponseEntity insertPost() {
        postService.insertPost();
        return ResponseEntity.status(201).build();
    }
}
