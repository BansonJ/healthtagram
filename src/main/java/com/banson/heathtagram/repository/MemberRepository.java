package com.banson.heathtagram.repository;

import com.banson.heathtagram.dto.MemberDto;
import com.banson.heathtagram.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Member findByNickname(String nickname);

    void deleteByEmail(String email);
}
