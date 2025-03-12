package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.PostHeart;
import com.banson.healthtagram.repository.mongoRepository.PostHeartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class PostHeartRepositoryTest {
    @Autowired
    PostHeartRepository postHeartRepository;

    public static Member member1;
    public static Post post1;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .id(1L)
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        post1 = Post.builder()
                .id(1L)
                .content("content")
                .nickname("banson1")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        PostHeart postHeart = PostHeart.builder()
                .postId(post1.getId())
                .memberId(member1.getId())
                .build();

        postHeartRepository.save(postHeart);
    }

    @AfterEach
    void after() {
        postHeartRepository.deleteAll();
    }

    @Test
    void findByMemberAndPostIn() {
        //given
        //when
        List<PostHeart> byMemberAndPostIn = postHeartRepository.findByMemberIdAndPostIdIn(member1.getId(), Arrays.asList(post1.getId()));
        //then
        Assertions.assertThat(byMemberAndPostIn.get(0).getPostId()).isEqualTo(post1.getId());
    }

    @Test
    void deleteByPostAndMember() {
        //given
        //when
        postHeartRepository.deleteByPostIdAndMemberId(post1.getId(), member1.getId());
        List<PostHeart> all = postHeartRepository.findAll();
        //then
        Assertions.assertThat(all).isEmpty();
    }

    @Test
    void findByMemberAndPost() {
        //given
        //when
        PostHeart byMemberAndPost = postHeartRepository.findByMemberIdAndPostId(member1.getId(), post1.getId());
        //then
        Assertions.assertThat(byMemberAndPost.getPostId()).isEqualTo(post1.getId());
    }
}