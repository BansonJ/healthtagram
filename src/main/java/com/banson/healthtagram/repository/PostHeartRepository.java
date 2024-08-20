package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    List<PostHeart> findByMemberAndPostIn(Member member, List<Post> postIdList);
}
