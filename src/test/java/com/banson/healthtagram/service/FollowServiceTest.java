package com.banson.healthtagram.service;

import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.repository.FollowRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @InjectMocks
    FollowService followService;

    @Test
    @DisplayName("팔로잉 하기")
    void following() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Member member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("12345")
                .profilePicture(null)
                .build();
        Follow follow = Follow.builder()
                .follower(member1)
                .following(member2)
                .build();

        when(followRepository.findByFollowerAndFollowing(any(), any())).thenReturn(null);
        when(followRepository.save(any())).thenReturn(follow);
        //when
        followService.following(member1, member2);
        //then
    }

    @Test
    @DisplayName("팔로우 상태")
    void followState() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Member member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("12345")
                .profilePicture(null)
                .build();
        Follow follow = Follow.builder()
                .follower(member1)
                .following(member2)
                .build();

        when(followRepository.findByFollowerAndFollowing(member1, member2)).thenReturn(Optional.ofNullable(follow));
        when(followRepository.findByFollowerAndFollowing(member2, member1)).thenReturn(null);
        //when
        String result1 = followService.followState(member1, member2);
        String result2 = followService.followState(member2, member1);
        //then
        Assertions.assertThat(result1).isEqualTo("following");
        Assertions.assertThat(result2).isEqualTo("unfollowing");
    }
}