package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.PostHeart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostHeartRepositoryTest {
    @Autowired
    PostHeartRepository postHeartRepository;

    public static Member member1;
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
        PostHeart postHeart = PostHeart.builder()
                .post(post1)
                .member(member1)
                .build();

        postHeartRepository.save(postHeart);
    }

    @Test
    void findByMemberAndPostIn() {
        //given
        //when
        List<PostHeart> byMemberAndPostIn = postHeartRepository.findByMemberAndPostIn(member1, Arrays.asList(post1));
        //then
        Assertions.assertThat(byMemberAndPostIn.get(0).getPost().getId()).isEqualTo(post1.getId());
    }

    @Test
    void deleteByPostAndMember() {
        //given
        //when
        postHeartRepository.deleteByPostAndMember(post1, member1);
        List<PostHeart> all = postHeartRepository.findAll();
        //then
        Assertions.assertThat(all).isEmpty();
    }

    @Test
    void findByMemberAndPost() {
        //given
        //when
        PostHeart byMemberAndPost = postHeartRepository.findByMemberAndPost(member1, post1);
        //then
        Assertions.assertThat(byMemberAndPost.getPost().getId()).isEqualTo(post1.getId());
    }
}