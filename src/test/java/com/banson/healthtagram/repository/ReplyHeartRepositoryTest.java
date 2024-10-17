package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.Reply;
import com.banson.healthtagram.entity.ReplyHeart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        reply1 = Reply.builder()
                .reply("reply1")
                .replyDate(LocalDateTime.now())
                .heartCount(0L)
                .nickname("banson1")
                .post(post1)
                .build();

        replyHeart = ReplyHeart.builder()
                .member(member1)
                .reply(reply1)
                .build();

        replyHeartRepository.save(replyHeart);
    }

    @Test
    void findByMemberAndReply() {
        //given
        //when
        ReplyHeart byMemberAndReply = replyHeartRepository.findByMemberAndReply(member1, reply1);
        //then
        Assertions.assertThat(byMemberAndReply.getReply().getReply()).isEqualTo(reply1.getReply());
    }

    @Test
    void deleteByReplyAndMember() {
        //given
        //when
        replyHeartRepository.deleteByReplyAndMember(reply1,  member1);
        List<ReplyHeart> all = replyHeartRepository.findAll();
        //then
        Assertions.assertThat(all).isEmpty();
    }
}