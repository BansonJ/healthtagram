package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.ReplyStateDto;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.PostHeart;
import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.entity.mongodb.ReplyHeart;
import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.entity.*;
import com.banson.healthtagram.repository.mongoRepository.ReplyHeartRepository;
import com.banson.healthtagram.repository.mongoRepository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyHeartRepository replyHeartRepository;

    @Transactional
    public Reply replyUp(Long postId, String reply, String nickname) {
        Reply reply1 = Reply.builder()
                .reply(reply)
                .heartCount(0L)
                .nickname(nickname)
                .postId(postId)
                .replyDate(LocalDateTime.now())
                .build();

        return replyRepository.save(reply1);
    }

    @Transactional
    public void replyDown(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    public List<ReplyStateDto> findReply(Member member, Long postId, Long lastReplyId, Pageable pageable) {
        List<Reply> replyList = replyRepository.findByPostIdAndIdLessThan(postId, lastReplyId, pageable);
        List<ReplyHeart> replyHeartList = replyHeartRepository.findByMemberIdAndReplyIdIn(member.getId(), replyList.stream().map(Reply::getId).toList());


        List<ReplyStateDto> replyStateDtoList = new ArrayList<>();
        for (Reply reply : replyList) {
            boolean state = false;
            for (int i = 0; i < replyHeartList.size(); i++) {
                if (replyHeartList.get(i).getReplyId().equals(reply.getId())) {
                    state = true;
                }
            }
            ReplyStateDto replyStateDto = ReplyStateDto.builder()
                    .reply(reply)
                    .state(state).build();

            replyStateDtoList.add(replyStateDto);
        }

        return replyStateDtoList;
    }

    @Transactional
    public ReplyHeart likeReply(Member member, Long replyId) {
        ReplyHeart exist = replyHeartRepository.findByMemberIdAndReplyId(member.getId(), replyId);
        if (exist != null) {
            return exist;
        }

        replyRepository.updateHeartCount(replyId);

        log.info("좋아요 증가 완료 postId: {}", replyId);

        ReplyHeart replyHeart = ReplyHeart.builder()
                .replyId(replyId)
                .memberId(member.getId())
                .build();
        ReplyHeart saved = replyHeartRepository.save(replyHeart);
        return saved;
    }

    @Transactional
    public void cancelLikeReply(Long replyId, Member member) {
        ReplyHeart exist = replyHeartRepository.findByMemberIdAndReplyId(member.getId(), replyId);
        if (!(exist == null)) {
            replyRepository.decreaseHeartCount(replyId);
            replyHeartRepository.deleteByReplyIdAndMemberId(replyId, member.getId());
        }
    }
}
