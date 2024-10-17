package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    public static Member member1;
    public static Member member2;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("1234")
                .profilePicture(null)
                .build();

        memberRepository.save(member1);
    }

    @Test
    void save() {
        //given
        //when
        Member save = memberRepository.save(member2);
        //then
        Assertions.assertThat(save.getNickname()).isEqualTo(member2.getNickname());
    }

    @Test
    void findByEmail() {
        //given
        //when
        Member save = memberRepository.findByEmail(member1.getEmail());
        //then
        Assertions.assertThat(save.getNickname()).isEqualTo(member1.getNickname());
    }

    @Test
    void findByNickname() {
        //given
        //when
        Member save = memberRepository.findByNickname(member1.getNickname());
        //then
        Assertions.assertThat(save.getNickname()).isEqualTo(member1.getNickname());
    }

    @Test
    void deleteByEmail() {
        //given
        //when
        memberRepository.deleteByEmail(member1.getEmail());
        Member byEmail = memberRepository.findByEmail(member1.getEmail());
        //then
        Assertions.assertThat(byEmail).isNull();
    }

    @Test
    void findByNicknameContaining() {
        //given
        memberRepository.save(member2);
        //when
        Page<Member> save = memberRepository.findByNicknameContaining("ban", pageable);
        List<Member> memberList= save.get().toList();
        //then
        System.out.println("memberList = " + memberList.get(0).getNickname());
        Assertions.assertThat(memberList.size()).isEqualTo(2);
    }

    static org.springframework.data.domain.Pageable pageable = new org.springframework.data.domain.Pageable() {
        @Override
        public int getPageNumber() {
            return 1;
        }

        @Override
        public int getPageSize() {
            return 5;
        }

        @Override
        public long getOffset() {
            return 0;
        }

        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "id");
        }

        @Override
        public org.springframework.data.domain.Pageable next() {
            return null;
        }

        @Override
        public org.springframework.data.domain.Pageable previousOrFirst() {
            return null;
        }

        @Override
        public org.springframework.data.domain.Pageable first() {
            return null;
        }

        @Override
        public Pageable withPage(int pageNumber) {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }
    };
}