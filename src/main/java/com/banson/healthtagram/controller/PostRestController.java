package com.banson.healthtagram.controller;

import com.banson.healthtagram.aop.annotation.Timer;
import com.banson.healthtagram.dto.PostRequestDto;
import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import com.banson.healthtagram.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final MemberService memberService;
    private final TagService tagService;

    private Member findUser(String nickname) { //내 Member 정보
        return memberService.findByNickname(nickname);
    }

    @PostMapping("/post")   //포스트 저장
    @Timer
    public ResponseEntity savePost(@RequestPart(value = "multipartFile") List<MultipartFile> multipartFile, @RequestParam(name = "storedNickname") String storedNickname,
                                   @Valid @RequestPart(name = "postRequestDto") PostRequestDto postRequestDto) {
        PostResponseDto postResponseDto = postService.savePost(postRequestDto, multipartFile, findUser(storedNickname));


        return ResponseEntity.status(201).body(postResponseDto);
    }

    @GetMapping("/home")    //기본 페이지
    @Timer
    public ResponseEntity home(@RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE+"") Long lastPostId, @RequestParam(name = "storedNickname") String storedNickname,
                               @PageableDefault(size = 3) Pageable pageable) {
        Member member = findUser(storedNickname);
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
    public ResponseEntity likePost(@PathVariable(name = "postId") Long postId, @RequestParam(name = "storedNickname") String storedNickname) {
        postService.likePost(postId, findUser(storedNickname));

        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancelLikePost/{postId}")   //포스트 좋아요 취소
    public ResponseEntity cancelLikePost(@PathVariable(name = "postId") Long postId, @RequestParam(name = "storedNickname") String storedNickname) {
        postService.cancelLikePost(postId, findUser(storedNickname));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/memberPage/{nickname}/post")  //특정 멤버 포스트 상세 보기
    public ResponseEntity memberPage(@PathVariable(name = "nickname") String nickname, @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE+"") Long lastPostId,
                                     @RequestParam(name = "storedNickname") String storedNickname, @PageableDefault(size = 3) Pageable pageable) {
        Member me = findUser(storedNickname);
        Member member = memberService.findByNickname(nickname);
        List<PostResponseDto> postResponseDto = postService.findPostInMember(lastPostId, me, Collections.singletonList(member), pageable);

        return ResponseEntity.ok(postResponseDto);
    }

    @GetMapping("/tagSearchResult")    //태그 검색 결과
    public ResponseEntity tagSearchResult(@RequestParam(name = "tagSearching") String tagSearching, @RequestParam(name = "storedNickname") String storedNickname,
                                          @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE+"") Long latPostId,
                                    @PageableDefault(size = 3) Pageable pageable) {
        List<PostResponseDto> postResponseDtoList = null;
        log.info("{}", latPostId);
        try {
            postResponseDtoList = postService.tagSearching(tagSearching, latPostId, findUser(storedNickname), pageable);
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(postResponseDtoList);
    }

    @GetMapping("/tagSearching")   //tag 검색
    public ResponseEntity tagSearching(@RequestParam(name = "search") String search, @PageableDefault(size = 50) Pageable pageable) {
        List<String> searchList = tagService.tagSearching(search, pageable);

        return ResponseEntity.ok(searchList);
    }
}
