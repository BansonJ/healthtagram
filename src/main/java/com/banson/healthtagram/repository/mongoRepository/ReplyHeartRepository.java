package com.banson.healthtagram.repository.mongoRepository;

import com.banson.healthtagram.entity.mongodb.ReplyHeart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyHeartRepository extends MongoRepository<ReplyHeart, String> {
    ReplyHeart findByMemberIdAndReplyId(Long memberId, Long replyId);

    void deleteByReplyIdAndMemberId(Long replyId, Long memberId);
}
