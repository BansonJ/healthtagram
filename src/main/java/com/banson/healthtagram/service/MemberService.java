package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.repository.FollowRepository;
import com.banson.healthtagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    @Value("${file.path}")
    private String filePath;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FollowRepository followRepository;

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member signup(SignupRequest signupDto, MultipartFile multipartFile) {
        if (!signupDto.getPassword().equals(signupDto.getCheckPassword())) {
            return null;
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid + multipartFile.getOriginalFilename();
        String fullPath = filePath + fileName;
        if (!multipartFile.isEmpty()) {
            try {
                multipartFile.transferTo(new File(fullPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Member member = Member.builder()
                .name(signupDto.getName())
                .email(signupDto.getEmail())
                .nickname(signupDto.getNickname())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .profilePicture(fullPath)
                .build();
        Member member1 = memberRepository.save(member);
        return member1;
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

    @Transactional
    public void cancelAccount(Principal principal) {
        memberRepository.deleteByEmail(principal.getName());
    }

    public SearchPageDto search(String search, Pageable pageable, Member me) {
        Page<Member> member = memberRepository.findByNicknameContaining(search, pageable);
        Optional<List<Follow>> followList = followRepository.findByFollowerAndFollowingIn(me ,member);

        List<SearchResponse> searchDtoList = new ArrayList<>();

        for (Member member1 : member) {
            boolean state = false;
            for (int i = 0; i < followList.get().size(); i++) {
                if (followList.get().get(i).getFollowing().getNickname().equals(member1.getNickname())) {
                    state = true;
                }
            }

            SearchResponse searchDto = SearchResponse.builder()
                    .nickname(member1.getNickname())
                    .profilePicture(member1.getProfilePicture())
                    .state(state)
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
