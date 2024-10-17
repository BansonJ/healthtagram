package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FollowRepositoryTest {
    @Autowired
    FollowRepository followRepository;
    private static Member member1;
    private static Member member2;
    private static Follow follow;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("1234")
                .profilePicture(null)
                .build();
        follow = Follow.builder()
                .follower(member1)
                .following(member2)
                .build();
        followRepository.save(follow);
    }

    @Test
    void deleteByFollowingAndFollower() {
        //given
        //when
        followRepository.deleteByFollowingAndFollower(member2, member1);
        //then
        Optional<Follow> byId = followRepository.findById(1L);
        Assertions.assertThat(byId).isEmpty();
    }

    @Test
    void findByFollowerAndFollowing() {
        //given
        //when
        Optional<Follow> byFollowerAndFollowing = followRepository.findByFollowerAndFollowing(member1, member2);
        //then
        Assertions.assertThat(byFollowerAndFollowing.get().getFollower()).isEqualTo(member1);
    }

    @Test
    void findByFollowerAndFollowingIn() {
        //given
        //when
        Optional<List<Follow>> byFollowerAndFollowing = followRepository.findByFollowerAndFollowingIn(member1, Arrays.asList(member2));
        //then
        Assertions.assertThat(byFollowerAndFollowing.get().get(0).getFollowing()).isEqualTo(member2);
    }

    @Test
    void save() {
        //given
        //when
        Follow saved = followRepository.save(follow);
        //then
        Assertions.assertThat(saved.getFollower().getNickname()).isEqualTo(member1.getNickname());
    }
}