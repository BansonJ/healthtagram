package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.PostRequestDto;
import com.banson.healthtagram.dto.PostResponseDto;
import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.PostHeart;
import com.banson.healthtagram.entity.PostImage;
import com.banson.healthtagram.repository.PostHeartRepository;
import com.banson.healthtagram.repository.PostImageRepository;
import com.banson.healthtagram.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    PostImageRepository postImageRepository;
    @Mock
    MemberService memberService;
    @Mock
    PostHeartRepository postHeartRepository;
    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("포스트 저장")
    void savePost() {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "", "", "".getBytes());

        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .content("content")
                .tag("#dd#aa")
                .build();
        Post post = Post.builder()
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        PostImage postImage = PostImage.builder()
                .postId(post.getId())
                .storedFileName("filePath123456")
                .originalFileName("filePath").build();
        when(postRepository.save(any())).thenReturn(post);
        when(memberService.findByNickname(any())).thenReturn(member1);
        when(postImageRepository.save(any())).thenReturn(postImage);
        //when
        PostResponseDto savedPost = postService.savePost(postRequestDto, Arrays.asList(multipartFile), member1);
        //then
        Assertions.assertThat(savedPost.getNickname()).isEqualTo(post.getNickname());
        Assertions.assertThat(savedPost.getTagList()).isEqualTo(Arrays.asList(post.getTag().replaceAll("#", " ").trim().split(" ")));
    }

    @Test
    @DisplayName("멤버로 포스트 검색")
    void findPostInMember() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Member member2 = Member.builder()
                .name("정승현2")
                .email("wjdtmdgus313@naver.com2")
                .nickname("banson2")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();
        PostHeart postHeart = PostHeart.builder()
                .id(1L)
                .member(member1)
                .post(post)
                .build();
        List<Post> postList = Arrays.asList(post);
        List<PostHeart> postHeartList = Arrays.asList(postHeart);
        List<Member> memberList = Arrays.asList(member1, member2);

        when(postRepository.findByIdLessThanAndMemberIn(any(), any(), any())).thenReturn(postList);
        when(postHeartRepository.findByMemberAndPostIn(member1, Arrays.asList(post))).thenReturn(postHeartList);
        //when
        List<PostResponseDto> post1 = postService.findPostInMember(50L, member1, memberList, pageable);
        List<PostResponseDto> post2 = postService.findPostInMember(50L, member2, memberList, pageable);
        //then
        Assertions.assertThat(post1.get(0).getNickname()).isEqualTo(member1.getNickname());
        Assertions.assertThat(post1.get(0).isLikeState()).isEqualTo(true);
        Assertions.assertThat(post2.get(0).isLikeState()).isEqualTo(false);
    }

    @Test
    @DisplayName("아이디 검색")
    void findById() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        //when
        Post post1 = postService.findById(post.getId());
        //then
        Assertions.assertThat(post1).isEqualTo(post);
    }

    @Test
    @DisplayName("닉네임 검색")
    void findByNickname() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();

        when(postRepository.findByNickname(post.getNickname(), pageable)).thenReturn(Arrays.asList(post));
        //when
        List<Post> post1 = postService.findByNickname(post.getNickname(), pageable);
        //then
        Assertions.assertThat(post1.get(0)).isEqualTo(post);
    }

    @Test
    @DisplayName("포스트 좋아요")
    void likePost() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();
        PostHeart postHeart = PostHeart.builder()
                .id(1L)
                .member(member1)
                .post(post)
                .build();

        when(postHeartRepository.findByMemberAndPost(any(), any())).thenReturn(null);
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
        //when
        postService.likePost(1L, member1);
        //then
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancelLikePost() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();
        PostHeart postHeart = PostHeart.builder()
                .id(1L)
                .member(member1)
                .post(post)
                .build();

        when(postHeartRepository.findByMemberAndPost(any(), any())).thenReturn(null);
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
        //when
        postService.cancelLikePost(1L, member1);
        //then
    }

    @Test
    @DisplayName("태그로 검색")
    void tagSearching() {
        //given
        Member member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        Post post = Post.builder()
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();
        PostHeart postHeart = PostHeart.builder()
                .id(1L)
                .member(member1)
                .post(post)
                .build();
        List<Post> postList = Arrays.asList(post);
        List<PostHeart> postHeartList = Arrays.asList(postHeart);

        when(postRepository.findByIdLessThanAndTagContaining(40L, "dd", pageable)).thenReturn(postList);
        when(postHeartRepository.findByMemberAndPostIn(any(), any())).thenReturn(postHeartList);
        //when
        List<PostResponseDto> post1 = postService.tagSearching("dd", 40L, member1, pageable);
        List<PostResponseDto> post2 = postService.tagSearching("cc", 40L, member1, pageable);
        //then
        Assertions.assertThat(post1.get(0).getNickname()).isEqualTo(member1.getNickname());
        Assertions.assertThat(post1.get(0).isLikeState()).isEqualTo(true);
        Assertions.assertThat(post2).isEmpty();
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