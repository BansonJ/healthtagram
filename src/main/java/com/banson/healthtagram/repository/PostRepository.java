package com.banson.healthtagram.repository;

import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByIdLessThanAndMemberIn(Long lastPostId, List<Member> id, Pageable pageable);

    List<Post> findByNickname(String nickname, Pageable pageable);

    List<Post> findByIdLessThanAndTagContaining(Long id ,String tagSearching, Pageable pageable);
}
