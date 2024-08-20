/*
package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.LoginRequest;
import com.banson.healthtagram.dto.SearchPageDto;
import com.banson.healthtagram.dto.SignupRequest;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

@TestInstance(value = PER_CLASS)
@SpringBootTest
class MemberRestControllerTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeAll
    void insertData() {
        memberService.insertData();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup() {
        //given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("wjdtmdgus313@naver.com50")
                .name("banson50")
                .password("1234")
                .checkPassword("1234")
                .nickname("banson50")
                .build();
        //when
        Member member = memberService.findByNickname("banson50");
        //then
        Assertions.assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("로그인 성공")
    void signin() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("wjdtmdgus313@naver.com2")
                .password("1234").build();
        //when
        String token = memberService.signin(loginRequest);
        //then
        Assertions.assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("마이페이지 불러오기 성공")
    void myPage() {
        //given
        String email = "wjdtmdgus313@naver.com3";
        //when
        //then
        Assertions.assertThat(memberDto.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원 정보 조회")
    void memberPage() {
        //given
        String nickname = "banson3";
        //when

        //then
        Assertions.assertThat("wjdtmdgus313@naver.com3").isEqualTo(memberDto.getEmail());
    }

    @Test
    @DisplayName("닉네임 검색")
    void nicknameSearch() {
        //given
        String search = "ban";
        //when
        //then
        Assertions.assertThat(searchPageDto.getSearchDto().get(0).getNickname()).isEqualTo("banson0");
    }

}*/
