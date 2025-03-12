package com.banson.healthtagram.repository.mongoRepository;

import com.banson.healthtagram.entity.mongodb.Reply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, Long> {
    List<Reply> findByPostIdAndIdLessThan(Long postId, Long lastReplyId, Pageable pageable);
}
