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
    public Member signup(SignupRequestDto signupDto, MultipartFile multipartFile) {
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
    public String signin(LoginRequestDto loginDto) {
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

    public SearchPageResponseDto search(String search, Pageable pageable, Member me) {
        Page<Member> member = memberRepository.findByNicknameContaining(search, pageable);
        List<Member> memberList = member.getContent();

        Optional<List<Follow>> followList = followRepository.findByFollowerAndFollowingIn(me ,memberList);

        List<SearchResponseDto> searchDtoList = new ArrayList<>();

        for (Member member1 : member) {
            boolean state = false;
            for (int i = 0; i < followList.get().size(); i++) {
                if (followList.get().get(i).getFollowing().getNickname().equals(member1.getNickname())) {
                    state = true;
                }
            }

            SearchResponseDto searchDto = SearchResponseDto.builder()
                    .nickname(member1.getNickname())
                    .profilePicture(member1.getProfilePicture())
                    .state(state)
                    .build();
            searchDtoList.add(searchDto);
        }

        SearchPageResponseDto searchPageDto = SearchPageResponseDto.builder()
                .searchDto(searchDtoList)
                .pageNo(member.getPageable().getPageNumber())
                .pageSize(member.getPageable().getPageSize())
                .totalPages(member.getTotalPages())
                .totalElements(member.getTotalElements())
                .build();

        return searchPageDto;
    }

    public void insertData() {
        List<Member> memberList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Member member = Member.builder()
                    .email("wjdtmdgus313@naver.com" + i)
                    .password(passwordEncoder.encode("1234"))
                    .name("정승현"+i)
                    .nickname("banson"+i)
                    .build();
            memberList.add(member);
        }

        memberRepository.saveAll(memberList);
    }
}
