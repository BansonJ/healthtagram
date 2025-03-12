package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.SearchResponseDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.repository.jpa.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public void following(Member following, Member follower) {
        if (following == follower) {
            throw new IllegalArgumentException();
        } else if (followRepository.findByFollowerAndFollowing(follower, following) != null) {
            throw new IllegalArgumentException();
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public List<SearchResponseDto> followingList(Member member) {
        List<SearchResponseDto> followingList = new ArrayList<>();

        for (Follow follow : member.getFollowingList()) {
            followingList.add(SearchResponseDto.builder()
                    .nickname(follow.getFollowing().getNickname())
                    .profilePicture(follow.getFollowing().getProfilePicture())
                    .build());
        }

        return followingList;
    }

    public List<SearchResponseDto> followerList(Member member) {
        List<SearchResponseDto> followerList = new ArrayList<>();

        for (Follow follow : member.getFollowerList()) {
            followerList.add(SearchResponseDto.builder()
                    .nickname(follow.getFollower().getNickname())
                    .profilePicture(follow.getFollower().getProfilePicture())
                    .build());
        }
        return followerList;
    }

    @Transactional
    public void unfollowing(Member following, Member follower) {
        followRepository.deleteByFollowingAndFollower(following, follower);
    }

    public String followState(Member me, Member friend) {
        if (followRepository.findByFollowerAndFollowing(me, friend) != null) {
            return "following";
        }
        return "unfollowing";
    }
}
