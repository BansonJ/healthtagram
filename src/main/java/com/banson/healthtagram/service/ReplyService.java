package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.entity.*;
import com.banson.healthtagram.repository.ReplyHeartRepository;
import com.banson.healthtagram.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final PostService postService;
    private final ReplyRepository replyRepository;
    private final ReplyHeartRepository replyHeartRepository;

    @Transactional
    public Reply replyUp(Long postId, ReplyRequestDto replyRequestDto, String nickname) {
        Post post = postService.findById(postId);
        Reply reply = Reply.builder()
                .reply(replyRequestDto.getReply())
                .heartCount(0L)
                .nickname(nickname)
                .build();
        reply.updatePost(post);

        return replyRepository.save(reply);
    }

    @Transactional
    public void replyDown(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    public List<ReplyResponseDto> findReply(Long postId, Long lastReplyId, Pageable pageable) {
        Post post = postService.findById(postId);
        List<Reply> replyList = replyRepository.findByPostAndIdLessThan(post, lastReplyId, pageable);
        
        List<ReplyResponseDto> replyResponseDtoList = new ArrayList<>();
        for (Reply reply : replyList) {
            ReplyResponseDto replyResponseDto = ReplyResponseDto.builder()
                    .reply(reply.getReply())
                    .nickname(reply.getNickname())
                    .replyId(reply.getId())
                    .createdAt(reply.getReplyDate())
                    .heartCount(reply.getHeartCount())
                    .build();
            replyResponseDtoList.add(replyResponseDto);
        }

        return replyResponseDtoList;
    }

    @Transactional
    public ReplyHeart likeReply(Member member, Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        ReplyHeart exist = replyHeartRepository.findByMemberAndReply(member, reply);
        if (exist != null) {
            return exist;
        }

        reply.plusHeartCount();

        ReplyHeart replyHeart = ReplyHeart.builder()
                .reply(reply)
                .member(member)
                .build();
        ReplyHeart saved = replyHeartRepository.save(replyHeart);
        return saved;
    }

    @Transactional
    public void cancelLikeReply(Long replyId, Member member) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        ReplyHeart exist = replyHeartRepository.findByMemberAndReply(member, reply);
        if (!(exist == null)) {
            reply.minusHeartCount();
            replyHeartRepository.deleteByReplyAndMember(reply, member);
        }
    }
}
