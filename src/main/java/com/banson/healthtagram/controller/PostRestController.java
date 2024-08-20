package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.PostRequestDto;
import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.PostHeart;
import com.banson.healthtagram.repository.PostHeartRepository;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final MemberService memberService;
    private final PostHeartRepository postHeartRepository;

    @PostMapping("/post")   //포스트 작성
    public ResponseEntity post(@RequestPart(value = "multipartFile") List<MultipartFile> multipartFile, @RequestPart(name = "postRequestDto") PostRequestDto postRequestDto, Principal principal) {
        String nickname = memberService.findByEmail(principal.getName()).getNickname();
        postService.savePost(postRequestDto, multipartFile, nickname);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/home")    //잘되는지 확인   ;팔로워들의 포스트를 보여줘야해;
    public ResponseEntity home(@RequestParam(name = "lastPostId") Long lastPostId, Principal principal, @PageableDefault(size = 3) Pageable pageable) {
        Member member = memberService.findByEmail(principal.getName());
        List<PostResponseDto> postList = postService.findPostInMember(lastPostId, member, pageable);

        return ResponseEntity.ok(postList);
    }

    @PutMapping("/likePost/{postId}")   //포스트 좋아요
    public ResponseEntity likePost(@PathVariable(name = "postId") Long postId, Principal principal) {
        postService.likePost(postId);
        PostHeart postHeart = PostHeart.builder()
                .post(postService.findById(postId))
                .member(memberService.findByEmail(principal.getName())).build();
        postHeartRepository.save(postHeart);

        return ResponseEntity.ok().build();
    }
}
