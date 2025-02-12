package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.ReplyRequestDto;
import com.banson.healthtagram.dto.ReplyResponseDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.Reply;
import com.banson.healthtagram.entity.ReplyHeart;
import com.banson.healthtagram.repository.ReplyHeartRepository;
import com.banson.healthtagram.repository.ReplyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    PostService postService;
    @Mock
    ReplyRepository replyRepository;
    @Mock
    ReplyHeartRepository replyHeartRepository;
    @InjectMocks
    ReplyService replyService;

    @Test
    @DisplayName("댓글 달기")
    void replyUp() {
        //given
        Post post = Post.builder()
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        Reply reply = Reply.builder()
                .reply("reply")
                .heartCount(0L)
                .nickname("banson2")
                .build();
        ReplyRequestDto replyRequestDto = ReplyRequestDto.builder().reply("reply").build();

        when(postService.findById(post.getId())).thenReturn(post);
        when(replyRepository.save(any())).thenReturn(reply);
        //when
        Reply replyUp = replyService.replyUp(post.getId(), replyRequestDto, reply.getNickname());
        //then
        Assertions.assertThat(replyUp.getReply()).isEqualTo(reply.getReply());
    }

    @Test
    @DisplayName("댓글들 출력")
    void findReply() {
        //given
        Post post = Post.builder()
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        Reply reply = Reply.builder()
                .reply("reply")
                .heartCount(0L)
                .nickname("banson2")
                .build();

        when(postService.findById(post.getId())).thenReturn(post);
        when(replyRepository.findByPostAndIdLessThan(any(), anyLong(), any())).thenReturn(Arrays.asList(reply));
        //when
        List<ReplyResponseDto> replyList = replyService.findReply(post.getId(), 20L, pageable);
        //then
        Assertions.assertThat(replyList.get(0).getReply()).isEqualTo(reply.getReply());
    }

    @Test
    @DisplayName("댓글 좋아요")
    void likeReply() {
        //given
        Reply reply = Reply.builder()
                .reply("reply")
                .heartCount(0L)
                .nickname("banson2")
                .id(2L)
                .build();
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        ReplyHeart replyHeart = ReplyHeart.builder()
                .reply(reply)
                .member(member1)
                .build();

        when(replyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(reply));
        when(replyHeartRepository.findByMemberAndReply(member1, reply)).thenReturn(replyHeart);
        //when
        ReplyHeart result = replyService.likeReply(member1, 2L);
        //then
        Assertions.assertThat(result.getMember().getNickname()).isEqualTo(member1.getNickname());
    }

    @Test
    @DisplayName("댓글 좋아요 취소")
    void cancelLikeReply() {
        //given
        Reply reply = Reply.builder()
                .reply("reply")
                .heartCount(3L)
                .nickname("banson2")
                .id(2L)
                .build();
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        ReplyHeart replyHeart = ReplyHeart.builder()
                .reply(reply)
                .member(member1)
                .build();

        when(replyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(reply));
        when(replyHeartRepository.findByMemberAndReply(member1, reply)).thenReturn(replyHeart);
        //when
        replyService.cancelLikeReply(2L, member1);
        //then
    }

    static Pageable pageable = new Pageable() {
        @Override
        public int getPageNumber() {
            return 1;
        }

        @Override
        public int getPageSize() {
            return 1;
        }

        @Override
        public long getOffset() {
            return 1;
        }

        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "id");
        }

        @Override
        public Pageable next() {
            return null;
        }

        @Override
        public Pageable previousOrFirst() {
            return null;
        }

        @Override
        public Pageable first() {
            return null;
        }

        @Override
        public Pageable withPage(int pageNumber) {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }
    };
}