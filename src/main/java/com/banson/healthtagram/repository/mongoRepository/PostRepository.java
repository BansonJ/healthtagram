package com.banson.healthtagram.repository.mongoRepository;

import com.banson.healthtagram.entity.mongodb.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    @Query(sort = "{_id:-1}")
    List<Post> findByIdLessThanAndNicknameIn(Long lastPostId, List<String> nickname, Pageable pageable);

    List<Post> findByNickname(String nickname, Pageable pageable);

    List<Post> findByIdIn(List<Long> postId, Pageable pageable);
}
