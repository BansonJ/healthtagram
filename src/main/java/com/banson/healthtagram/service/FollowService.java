package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.SearchResponse;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberService memberService;

    public void following(Member following, Member follower) {
        if (following == follower) {
            throw new IllegalArgumentException();
        } else if (followRepository.findByFollowingAndFollower(following, follower).isPresent()) {
            throw new IllegalArgumentException();
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public List<SearchResponse> followingList(Member member) {
        List<SearchResponse> followingList = new ArrayList<>();

        for (Follow follow : member.getFollowingList()) {
            followingList.add(SearchResponse.builder()
                    .nickname(follow.getFollowing().getNickname())
                    .profilePicture(follow.getFollowing().getProfilePicture())
                    .build());
        }

        return followingList;
    }

    public List<SearchResponse> followerList(Member member) {
        List<SearchResponse> followerList = new ArrayList<>();

        for (Follow follow : member.getFollowerList()) {
            followerList.add(SearchResponse.builder()
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
}
