package com.banson.healthtagram.controller;

import com.banson.healthtagram.aop.annotation.Timer;
import com.banson.healthtagram.dto.ReplyMemberResponseDto;
import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.dto.ReplyStateDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import com.banson.healthtagram.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReplyRestController {
    private final ReplyService replyService;
    private final MemberService memberService;
    private final PostService postService;

    private Member findUser(String nickname) { //내 Member 정보
        return memberService.findByNickname(nickname);
    }

    @GetMapping("{postId}/reply")   //댓글 보기
    public ResponseEntity reply(@PathVariable(name = "postId") Long postId, @PageableDefault(size = 5) Pageable pageable, @RequestParam(name = "lastReplyId") Long lastReplyId,
                                @RequestParam(name = "storedNickname") String storedNickname) {
        Member member = findUser(storedNickname);
        List<ReplyStateDto> replyStateDtoList = replyService.findReply(member, postId, lastReplyId, pageable);
        Post post = postService.findById(postId);

        //포스트 사진들
        List<String> updatedFilePaths = new ArrayList<>();
        for (String filePath : post.getFilePath()) {
            String fileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(filePath).getName();
            updatedFilePaths.add(fileUrl);
        }
        post.setFilePath(updatedFilePaths);

        //포스트 게시자 프로필 사진
        String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(member.getProfilePicture()).getName();

        //댓글과 댓글 쓴 사람 정보
        List<ReplyMemberResponseDto> replyMemberResponseDtos = new ArrayList<>();
        for (ReplyStateDto replyStateDto : replyStateDtoList) {
            Member replyMember = findUser(replyStateDto.getReply().getNickname());
            String replyMemberProfile = "http://localhost:8080/api/study/healthtagramImage/" + new File(replyMember.getProfilePicture()).getName();

            ReplyMemberResponseDto replyMemberResponseDto = ReplyMemberResponseDto.builder()
                    .reply(replyStateDto.getReply())
                    .profilePicture(replyMemberProfile)
                    .state(replyStateDto.isState())
                    .build();
            replyMemberResponseDtos.add(replyMemberResponseDto);
        }

        log.info("{}", post.getFilePath());

        ReplyResponseDto replyResponseDto = ReplyResponseDto.builder()
                .replyMemberResponseDtoList(replyMemberResponseDtos)
                .post(post)
                .profilePicture(profileFileUrl)
                .build();

        return ResponseEntity.ok(replyResponseDto);
    }

    @PostMapping("/{postId}/replyUp")   //댓글 쓰기
    public ResponseEntity replyUp(@PathVariable(name = "postId") Long postId, @Valid @RequestBody String reply,
                                  @RequestParam(name = "storedNickname") String storedNickname) {
        Member member = findUser(storedNickname);

        String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(member.getProfilePicture()).getName();

        Reply reply1 = null;
        if (findUser(storedNickname) != null) {
            reply1 = replyService.replyUp(postId, reply, member.getNickname());
        }

        ReplyMemberResponseDto replyMemberResponseDto = ReplyMemberResponseDto.builder()
                .reply(reply1)
                .nickname(storedNickname)
                .profilePicture(profileFileUrl)
                .build();

        return ResponseEntity.status(201).body(replyMemberResponseDto);
    }

    @DeleteMapping("/{postId}/replyDown/{replyId}") //댓글 삭제
    public ResponseEntity replyDown(@PathVariable(name = "replyId") Long replyId) {
        replyService.replyDown(replyId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/likeReply/{replyId}")    //댓글 좋아요
    public void likeReply(@PathVariable(name = "replyId") Long replyId, @RequestParam(name = "storedNickname") String storedNickname) {
        replyService.likeReply(findUser(storedNickname), replyId);
    }

    @DeleteMapping("/{postId}/cancelLikeReply/{replyId}")   //댓글 좋아요 취소
    public void cancelLikeReply(@PathVariable(name = "replyId") Long replyId, @RequestParam(name = "storedNickname") String storedNickname) {
        replyService.cancelLikeReply(replyId, findUser(storedNickname));
    }
}
