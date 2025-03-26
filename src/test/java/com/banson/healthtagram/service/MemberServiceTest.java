package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.LoginRequestDto;
import com.banson.healthtagram.dto.SearchPageResponseDto;
import com.banson.healthtagram.dto.SignupRequestDto;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.repository.jpa.FollowRepository;
import com.banson.healthtagram.repository.jpa.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    FollowRepository followRepository;
    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("닉네임으로 찾기")
    void findByNickname() {
        //given
        String nickname = "banson1";
        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        when(memberRepository.findByNickname(nickname)).thenReturn(member);
        //when
        Member member1 = memberService.findByNickname(nickname);
        //then
        Assertions.assertThat(member1).isEqualTo(member);
    }

    @Test
    @DisplayName("이메일로 찾기")
    void findByEmail() {
        //given
        String email = "banson1";
        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        when(memberRepository.findByEmail(email)).thenReturn(member);
        //when
        Member member1 = memberService.findByEmail(email);
        //then
        Assertions.assertThat(member1).isEqualTo(member);
    }

    @Test
    @DisplayName("회원가입")
    void signup() {
        //given
        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        SignupRequestDto signupDto = SignupRequestDto.builder()
                .email("wjdtmdgus313@naver.com1")
                .name("banson1")
                .password("1234")
                .checkPassword("1234")
                .nickname("banson1")
                .build();
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "", "", "".getBytes());
        when(memberRepository.save(any())).thenReturn(member);
        //when
        Member signup = memberService.signup(signupDto, multipartFile);
        //then
        Assertions.assertThat(signup.getEmail()).isEqualTo(member.getEmail());
        passwordEncoder.matches("1234", signup.getPassword());
    }

    @Test
    @DisplayName("로그인")
    void signin() {
        //given
        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("wjdtmdgus313@naver.com1")
                .password("1234")
                .build();

        when(memberRepository.findByEmail(any())).thenReturn(member);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(any())).thenReturn("adfasdfasfdsaf");
        //when
        String token = memberService.signin(loginRequestDto);
        //then
        Assertions.assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("닉네임 검색")
    void search() {
        //given
        String search = "banson1";

        List<Member> memberList = new ArrayList<>();
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        memberList.add(member1);
        Member member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("1234")
                .profilePicture(null)
                .build();

        Page<Member> members = new Page<>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super Member, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 1;
            }

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<Member> getContent() {
                return memberList;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return Sort.by(Sort.Direction.DESC, "nickname");
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Member> iterator() {
                return memberList.listIterator();
            }
        };
        Follow follow = Follow.builder()
                .follower(member1)
                .following(member2)
                .build();

        List<Follow> followList = new ArrayList<>();
        followList.add(follow);

        when(memberRepository.findByNicknameContaining(any(), any())).thenReturn(members);
        when(followRepository.findByFollowerAndFollowingIn(any(), any())).thenReturn(Optional.of(followList));
        //when
        SearchPageResponseDto searchPageResponseDto = memberService.search(search, any(), any());
        //then
        Assertions.assertThat(searchPageResponseDto.getSearchDto().get(0).getNickname()).isEqualTo(search);
    }
}