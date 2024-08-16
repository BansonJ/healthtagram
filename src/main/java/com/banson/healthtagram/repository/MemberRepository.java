package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Member findByNickname(String nickname);

    void deleteByEmail(String email);

    Page<Member> findByNicknameContaining(String search, Pageable pageable);

}
