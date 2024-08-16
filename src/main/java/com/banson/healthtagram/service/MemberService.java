package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public void signup(SignupRequest signupDto) {
        Member member = Member.builder()
                .name(signupDto.getName())
                .email(signupDto.getEmail())
                .nickname(signupDto.getNickname())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public String signin(LoginRequest loginDto) {
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

    public SearchPageDto search(Pageable pageable, String search) {
        Page<Member> member = memberRepository.findByNicknameContaining(search, pageable);
        List<SearchResponse> searchDtoList = new ArrayList<>();

        for (Member member1 : member) {
            SearchResponse searchDto = SearchResponse.builder()
                            .nickname(member1.getNickname())
                                    .profilePicture(member1.getProfilePicture())
                                            .build();
            searchDtoList.add(searchDto);
        }

        SearchPageDto searchPageDto = SearchPageDto.builder()
                .searchDto(searchDtoList)
                .pageNo(member.getPageable().getPageNumber())
                .pageSize(member.getPageable().getPageSize())
                .totalPages(member.getTotalPages())
                .totalElements(member.getTotalElements())
                .build();

        return searchPageDto;
    }

    public void insertData() {
        for (int i = 0; i < 30; i++) {
            Member member = Member.builder()
                    .email("wjdtmdgus313@naver.com" + i)
                    .password(passwordEncoder.encode("1234"))
                    .name("정승현"+i)
                    .nickname("banson"+i)
                    .build();
            memberRepository.save(member);
        }
    }
}
