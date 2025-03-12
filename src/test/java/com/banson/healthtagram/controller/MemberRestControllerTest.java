package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.LoginRequestDto;
import com.banson.healthtagram.dto.SearchPageResponseDto;
import com.banson.healthtagram.dto.SearchResponseDto;
import com.banson.healthtagram.dto.SignupRequestDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberRestController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberRestControllerTest {


    @Autowired
    MockMvc mockMvc;
    @MockBean
    BCryptPasswordEncoder passwordEncoder;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PostService postService;
    @MockBean
    private FollowService followService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void signup() throws Exception {
        //given
        SignupRequestDto signupDto = SignupRequestDto.builder()
                .email("wjdtmdgus313@naver.com44")
                .name("banson44")
                .password("1234")
                .checkPassword("1234")
                .nickname("banson44")
                .build();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("multipartFile", "test.png", "png", "<<data>>".getBytes());
        //when
        given(memberService.signup(any(),any())).willReturn(Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build());
        //then
        mockMvc.perform(multipart("/api/signup")
                        .file(mockMultipartFile)
                        .file(new MockMultipartFile("signupDto", "", "application/json", objectMapper.writeValueAsString(signupDto).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void signin() throws Exception {
        //given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .email("wjdtmdgus313@naver.com2")
                .password("1234")
                .build();
        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        //when
        given(memberService.signin(loginRequest)).willReturn("token");
        //then
        mockMvc.perform(post("/api/signin")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("멤버페이지 불러오기 성공")
    @WithMockUser
    void memberPage() throws Exception {
        //given
        String nickname = "banson1";

        Member member = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        List<Post> postList = new ArrayList<>();
        Pageable pageable = null;
        //when
        given(memberService.findByNickname(nickname)).willReturn(member);
        given(postService.findByNickname(any(), any())).willReturn(postList);
        given(followService.followState(any(),any())).willReturn("me");
        //then
        mockMvc.perform(get("/api/memberPage/{nickname}", nickname)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageable))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("banson1"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 검색")
    @WithMockUser
    void nicknameSearch() throws Exception {
        //given
        Pageable pageable = null;
        List<SearchResponseDto> searchDtoList = new ArrayList<>();
        SearchResponseDto searchResponseDto = SearchResponseDto.builder()
                .nickname("banson1")
                .profilePicture(null)
                .state(false)
                .build();
        searchDtoList.add(searchResponseDto);
        SearchPageResponseDto searchPageDto = SearchPageResponseDto.builder()
                .searchDto(searchDtoList)
                .pageNo(1)
                .pageSize(1)
                .totalPages(1)
                .totalElements(1)
                .build();
        //when
        given(memberService.search(any(), any(), any())).willReturn(searchPageDto);
        //then
        mockMvc.perform(get("/api/nicknameSearching")
                .param("search", "ban")
                        .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchDto[0].nickname").value("banson1"))
                .andDo(print());
    }

}
