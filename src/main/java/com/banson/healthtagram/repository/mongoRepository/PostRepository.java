package com.banson.healthtagram.repository.mongoRepository;

import com.banson.healthtagram.entity.mongodb.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    @Query(sort = "{_id:-1}")
    List<Post> findByIdLessThanAndNicknameIn(Long lastPostId, List<String> nickname, Pageable pageable);

    List<Post> findByNickname(String nickname, Pageable pageable);

    @Query(sort = "{_id:-1}")
    List<Post> findByIdIn(List<Long> postId, Pageable pageable);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'heartCount': 1 } }")  // 💡 좋아요 수 증가
    void updateHeartCount(Long postId);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'heartCount': -1 } }")  // 💡 좋아요 취소 (1 감소)
    void decreaseHeartCount(Long postId);
}
