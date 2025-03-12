package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyRestController {
    private final ReplyService replyService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    private Member findUser() { //내 Member 정보
        return memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("{postId}/reply")   //댓글 보기
    public ResponseEntity reply(@PathVariable(name = "postId") Long postId, @PageableDefault(size = 5) Pageable pageable, @RequestParam(name = "lastReplyId") Long lastReplyId) {
        List<ReplyResponseDto> replyResponseDtoList = replyService.findReply(postId, lastReplyId, pageable);

        return ResponseEntity.ok(replyResponseDtoList);
    }

    @PostMapping("/{postId}/replyUp")   //댓글 쓰기
    public ResponseEntity replyUp(@PathVariable(name = "postId") Long postId, @Valid @RequestBody ReplyRequestDto replyRequestDto,
                                  HttpServletRequest request) {
        Reply reply = null;
        if (findUser() != null) {
            reply = replyService.replyUp(postId, replyRequestDto, findUser().getNickname());
        }

        return ResponseEntity.status(201).body(reply);
    }

    @DeleteMapping("/{postId}/replyDown/{replyId}") //댓글 삭제
    public ResponseEntity replyDown(@PathVariable(name = "replyId") Long replyId) {
        replyService.replyDown(replyId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/likeReply/{replyId}")    //댓글 좋아요
    public void likeReply(Principal principal, @PathVariable(name = "replyId") Long replyId, HttpServletRequest request) {
        replyService.likeReply(findUser(), replyId);
    }

    @DeleteMapping("/{postId}/cancelLikeReply/{replyId}")   //댓글 좋아요 취소
    public void cancelLikeReply(@PathVariable(name = "replyId") Long replyId, HttpServletRequest request) {
        replyService.cancelLikeReply(replyId, findUser());
    }
}
