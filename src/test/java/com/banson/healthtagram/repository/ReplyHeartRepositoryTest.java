package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.entity.mongodb.ReplyHeart;
import com.banson.healthtagram.repository.mongoRepository.ReplyHeartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
class ReplyHeartRepositoryTest {

    @Autowired
    ReplyHeartRepository replyHeartRepository;

    public static Member member1;
    public static ReplyHeart replyHeart;
    public static Reply reply1;
    public static Post post1;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        post1 = Post.builder()
                .content("content")
                .nickname("banson1")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        reply1 = Reply.builder()
                .reply("reply1")
                .replyDate(LocalDateTime.now())
                .heartCount(0L)
                .nickname("banson1")
                .postId(post1.getId())
                .build();

        replyHeart = ReplyHeart.builder()
                .memberId(member1.getId())
                .replyId(reply1.getId())
                .build();

        replyHeartRepository.save(replyHeart);
    }

    @Test
    void findByMemberAndReply() {
        //given
        //when
        ReplyHeart byMemberAndReply = replyHeartRepository.findByMemberIdAndReplyId(member1.getId(), reply1.getId());
        //then
        Assertions.assertThat(byMemberAndReply.getReplyId()).isEqualTo(reply1.getId());
    }

    @Test
    void deleteByReplyAndMember() {
        //given
        //when
        replyHeartRepository.deleteByReplyIdAndMemberId(reply1.getId(), member1.getId());
        List<ReplyHeart> all = replyHeartRepository.findAll();
        //then
        Assertions.assertThat(all).isEmpty();
    }
}