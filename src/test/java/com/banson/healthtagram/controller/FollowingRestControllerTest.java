package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.SearchResponseDto;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.FollowService;
import com.banson.healthtagram.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowingRestController.class)
@MockBean(JpaMetamodelMappingContext.class)
class FollowingRestControllerTest {
    @MockBean
    private MemberService memberService;
    @MockBean
    private FollowService followService;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("내가 팔로잉하는 사람 리스트 보기")
    @WithMockUser
    @Test
    void followingList() throws Exception {
        //given
        List<SearchResponseDto> followingList = new ArrayList<>();
        followingList.add(SearchResponseDto.builder()
                .nickname("banson2")
                .profilePicture("abcd.png")
                .build());
        //when
        given(followService.followingList(any())).willReturn(followingList);
        //then
        mockMvc.perform(get("/api/followingList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("banson2"))
                .andDo(print());
    }

    @DisplayName("날 팔로우하는 사람 리스트 보기")
    @WithMockUser
    @Test
    void followerList() throws Exception {
        //given
        List<SearchResponseDto> followerList = new ArrayList<>();
        followerList.add(SearchResponseDto.builder()
                .nickname("banson3")
                .profilePicture("jkhu.png")
                .build());
        //when
        given(followService.followerList(any())).willReturn(followerList);
        //then
        mockMvc.perform(get("/api/followerList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("banson3"))
                .andDo(print());
    }
}