package com.banson.healthtagram.repository.mongoRepository;

import com.banson.healthtagram.entity.mongodb.PostHeart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHeartRepository extends MongoRepository<PostHeart, String> {
    List<PostHeart> findByMemberIdAndPostIdIn(Long memberId, List<Long> postIdList);

    void deleteByPostIdAndMemberId(Long postId, Long member);

    PostHeart findByMemberIdAndPostId(Long member, Long postId);
}
