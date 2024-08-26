package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.Reply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostAndIdLessThan(Post post, Long lastReplyId, Pageable pageable);
}
