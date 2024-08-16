package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
}
