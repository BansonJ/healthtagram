package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    void deleteByFollowingAndFollower(Member following, Member follower);

    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);
}
