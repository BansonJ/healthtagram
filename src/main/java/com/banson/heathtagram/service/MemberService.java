package com.banson.heathtagram.service;

import com.banson.heathtagram.dto.LoginDto;
import com.banson.heathtagram.dto.MyPageDto;
import com.banson.heathtagram.dto.SignupDto;
import com.banson.heathtagram.entity.Member;
import com.banson.heathtagram.jwt.JwtTokenFilter;
import com.banson.heathtagram.jwt.JwtTokenProvider;
import com.banson.heathtagram.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    @Transactional
    public void signup(SignupDto signupDto) {
        Member member = Member.builder()
                .name(signupDto.getName())
                .email(signupDto.getEmail())
                .nickname(signupDto.getNickname())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .build();
        memberRepository.save(member);
    }

    public String signin(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자"));
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호");
        }

        return createToken(loginDto.getEmail());
    }

    public String createToken(String email) {
        return jwtTokenProvider.createAccessToken(email);
    }

    public MyPageDto myPage(HttpServletRequest request) {
        String accessToken = jwtTokenFilter.resolveToken(request);
        String email = jwtTokenProvider.getAccessTokenInfo(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow();

        return MyPageDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .followedMemberId(member.getFollowedMemberId())
                .followingMemberId(member.getFollowingMemberId())
                .profilePicture(member.getProfilePicture())
                .build();
    }
}
