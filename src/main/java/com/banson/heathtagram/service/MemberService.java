package com.banson.heathtagram.service;

import com.banson.heathtagram.dto.LoginDto;
import com.banson.heathtagram.dto.MemberDto;
import com.banson.heathtagram.dto.SignupDto;
import com.banson.heathtagram.entity.Member;
import com.banson.heathtagram.jwt.JwtTokenFilter;
import com.banson.heathtagram.jwt.JwtTokenProvider;
import com.banson.heathtagram.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Transactional
    public String signin(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail());
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호");
        }

        return createToken(loginDto.getEmail());
    }

    public String createToken(String email) {
        return jwtTokenProvider.createAccessToken(email);
    }

    public MemberDto myPage(Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        return MemberDto.toEntity(member);
    }

    public MemberDto memberPage(String nickname) {
        Member member = memberRepository.findByNickname(nickname);

        return MemberDto.toEntity(member);
    }

    @Transactional
    public void cancelAccount(Principal principal) {
        memberRepository.deleteByEmail(principal.getName());
    }
}
