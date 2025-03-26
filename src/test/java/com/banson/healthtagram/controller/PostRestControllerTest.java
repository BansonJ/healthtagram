package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.PostRequestDto;
import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostRestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PostService postService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("게시글 저장")
    @WithMockUser
    @Test
    void savePost() throws Exception {
        //given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .content("content")
                .tagList("aa, bb")
                .build();
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .postId(1L)
                .tagList("gg, ff")
                .heartCount(0L)
                .filePath(List.of("filePath"))
                .nickname("banson1")
                .content("content")
                .createdAt(LocalDateTime.now())
                .build();
        List<MockMultipartFile> multipartFileList = new ArrayList<>();
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "test.png", "png", "data".getBytes());
        multipartFileList.add(multipartFile);

        MockMultipartFile postMultipartFile = new MockMultipartFile("postRequestDto", "", "application/json", objectMapper.writeValueAsString(postRequestDto).getBytes());
        //when
        given(postService.savePost(any(), any(), any())).willReturn(postResponseDto);
        //then
        mockMvc.perform(multipart("/api/post")
                .file(multipartFileList.get(0))
                .file(postMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("홈화면")
    @WithMockUser
    void home() throws Exception {
        //given
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .content("content")
                .createdAt(LocalDateTime.now())
                .filePath(null)
                .heartCount(1L)
                .tagList("aa, dd")
                .nickname("banson1")
                .likeState(false)
                .postId(1L)
                .build();
        postResponseDtoList.add(postResponseDto);

        //when
        given(postService.findPostInMember(any(), any(), any(), any())).willReturn(postResponseDtoList);
        //then
        mockMvc.perform(get("/api/home")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastPostId", String.valueOf(50))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("content"))
                .andDo(print());
    }

    @Test
    @DisplayName("멤버 게시글")
    @WithMockUser
    void memberPost() throws Exception {
        //given
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .content("content")
                .createdAt(LocalDateTime.now())
                .filePath(null)
                .heartCount(1L)
                .tagList("aa, bb")
                .nickname("banson1")
                .likeState(false)
                .postId(1L)
                .build();
        postResponseDtoList.add(postResponseDto);

        //when
        given(postService.findPostInMember(any(), any(), any(), any())).willReturn(postResponseDtoList);
        //then
        mockMvc.perform(get("/api/memberPage/{nickname}/post", "banson1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("lastPostId", String.valueOf(50))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("태그로 검색")
    @WithMockUser
    void tagSearch() throws Exception {
        //given
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .content("content")
                .createdAt(LocalDateTime.now())
                .filePath(null)
                .heartCount(1L)
                .tagList("aa, bb")
                .nickname("banson1")
                .likeState(false)
                .postId(1L)
                .build();
        postResponseDtoList.add(postResponseDto);
        //when
        given(postService.tagSearching(any(), any(), any(), any())).willReturn(postResponseDtoList);
        //then
        mockMvc.perform(get("/api/tagSearching")
                .contentType(MediaType.APPLICATION_JSON)
                .param("tagSearching", "aa")
                .param("lastPostId", "50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tagList[0]").value("aa"))
                .andDo(print());
    }
}