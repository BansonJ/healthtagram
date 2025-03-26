package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.Follow;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.repository.jpa.FollowRepository;
import com.banson.healthtagram.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FollowRepository followRepository;
    private final JdbcTemplate jdbcTemplate;

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
    public void cancelAccount(Member member) {
        memberRepository.deleteByEmail(member.getName());
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

            String profileFileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(member1.getProfilePicture()).getName();
            log.info(profileFileUrl);

            SearchResponseDto searchDto = SearchResponseDto.builder()
                    .nickname(member1.getNickname())
                    .profilePicture(profileFileUrl)
                    .state(state)
                    .build();
            searchDtoList.add(searchDto);
        }

        SearchPageResponseDto searchPageDto = SearchPageResponseDto.builder()
                .searchDto(searchDtoList)
                .build();

        return searchPageDto;
    }

    
    public void insertData(List<Member> members) {
        String sql = "INSERT INTO MEMBER (Id, email, password, name, nickname, profile_picture, created_at) " +
                " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        log.info("db넣기 시작 {}", LocalDateTime.now());

        jdbcTemplate.batchUpdate(sql,
                members,
                members.size(),
                (PreparedStatement ps, Member member) -> {
                   ps.setLong(1, member.getId());
                   ps.setString(2, member.getEmail());
                   ps.setString(3, member.getPassword());
                   ps.setString(4, member.getName());
                   ps.setString(5, member.getNickname());
                   ps.setString(6, member.getProfilePicture());
                });
        log.info("db 넣기 끝 {}", LocalDateTime.now());
    }
}
