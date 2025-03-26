package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.SearchResponseDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.repository.jpa.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void following(Member follower, Member following) {
        if (following == follower) {
            throw new IllegalArgumentException();
        } else if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new IllegalArgumentException();
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public List<SearchResponseDto> followingList(Member member, Pageable pageable) {
        List<SearchResponseDto> followingList = new ArrayList<>();

        Optional<List<Follow>> myFollowing = followRepository.findByFollower(member, pageable);

        for (Follow follow : myFollowing.get().stream().collect(Collectors.toList())) {

            String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(follow.getFollowing().getProfilePicture()).getName();

            followingList.add(SearchResponseDto.builder()
                    .nickname(follow.getFollowing().getNickname())
                    .profilePicture(profileFileUrl)
                    .build());
        }

        return followingList;
    }

    public List<SearchResponseDto> followerList(Member member, Pageable pageable) {
        List<SearchResponseDto> followerList = new ArrayList<>();

        Optional<List<Follow>> myFollower= followRepository.findByFollowing(member, pageable);

        for (Follow follow : myFollower.get().stream().collect(Collectors.toList())) {

            String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(follow.getFollower().getProfilePicture()).getName();

            followerList.add(SearchResponseDto.builder()
                    .nickname(follow.getFollower().getNickname())
                    .profilePicture(profileFileUrl)
                    .build());
        }

        return followerList;
    }

    @Transactional
    public void unfollowing(Member following, Member follower) {
        followRepository.deleteByFollowingAndFollower(following, follower);
    }

    public String followState(Member me, Member friend) {
        if (followRepository.findByFollowerAndFollowing(me, friend).isPresent()) {
            return "follow";
        }
        return "unfollow";
    }
}
