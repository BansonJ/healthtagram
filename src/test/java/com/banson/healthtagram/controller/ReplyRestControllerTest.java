package com.banson.healthtagram.controller;

import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.entity.Reply;
import com.banson.healthtagram.jwt.JwtTokenProvider;
import com.banson.healthtagram.service.MemberService;
import com.banson.healthtagram.service.ReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReplyRestController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ReplyRestControllerTest {
    @MockBean
    private MemberService memberService;
    @MockBean
    private ReplyService replyService;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("댓글 보기")
    @WithMockUser
    @Test
    void reply() throws Exception {
        //given
        List<ReplyResponseDto> replyResponseDtoList = new ArrayList<>();
            ReplyResponseDto replyResponseDto = ReplyResponseDto.builder()
                    .reply("reply")
                    .nickname("banson1")
                    .replyId(1L)
                    .createdAt(LocalDateTime.now())
                    .heartCount(1L)
                    .build();
            replyResponseDtoList.add(replyResponseDto);
        //when
        given(replyService.findReply(any(), any(), any())).willReturn(replyResponseDtoList);
        //then
        mockMvc.perform(get("/api/{postId}/reply", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastReplyId", "50")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reply").value("reply"))
                .andDo(print());
    }

    @DisplayName("댓글 달기")
    @WithMockUser
    @Test
    void replyUp() throws Exception {
        //given
        Reply reply = Reply.builder()
                .id(1L)
                .replyDate(LocalDateTime.now())
                .reply("reply")
                .heartCount(1L)
                .nickname("banson1")
                .build();
        ReplyRequestDto replyRequestDto = ReplyRequestDto.builder()
                .reply("reply")
                .build();
        //when
        given(replyService.replyUp(any(), any(), any())).willReturn(reply);
        //then
        mockMvc.perform(post("/api/{postId}/replyUp", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyRequestDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}