package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.PostHeart;
import com.banson.healthtagram.entity.Reply;
import com.banson.healthtagram.entity.ReplyHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyHeartRepository extends JpaRepository<ReplyHeart, Long> {
    ReplyHeart findByMemberAndReply(Member member, Reply reply);

    ReplyHeart deleteByReplyAndMember(Reply reply, Member member);
}
