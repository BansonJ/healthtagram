package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.*;
import com.banson.healthtagram.repository.PostHeartRepository;
import com.banson.healthtagram.repository.PostImageRepository;
import com.banson.healthtagram.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Value("${file.path}")
    private String filePath;

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MemberService memberService;
    private final PostHeartRepository postHeartRepository;

    @Transactional
    public PostResponseDto savePost(PostRequestDto postRequestDto, List<MultipartFile> multipartFile, Member member) {

        List<String> path = new ArrayList<>();

        for (MultipartFile multipartFile1 : multipartFile) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + multipartFile1.getOriginalFilename();
            String fullPath = filePath + fileName;
            if (!multipartFile.isEmpty()) {
                try {
                    multipartFile1.transferTo(new File(fullPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            path.add(fullPath);
        }

        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .nickname(member.getNickname())
                .tag(postRequestDto.getTag())
                .filePath(path)
                .heartCount(0L)
                .build();
        Post savedPost = postRepository.save(post);
        post.updateMember(memberService.findByNickname(member.getNickname()));

        int i = 0;
        for (String path1 : path) {
            PostImage postImage = PostImage.builder()
                    .postId(post.getId())
                    .storedFileName(path1)
                    .originalFileName(multipartFile.get(i).getOriginalFilename()).build();
            postImageRepository.save(postImage);
            i++;
        }
        return PostResponseDto.builder()
                .content(savedPost.getContent())
                .createdAt(savedPost.getCreatedAt())
                .filePath(savedPost.getFilePath())
                .heartCount(savedPost.getHeartCount())
                .tagList(Arrays.stream(post.getTag().replaceAll("#", " ").trim().split(" ")).toList())
                .nickname(savedPost.getNickname())
                .likeState(false)
                .postId(savedPost.getId())
                .build();
    }

    public List<PostResponseDto> findPostInMember(Long lastPostId, Member member, List<Member> id,Pageable pageable) {
        List<Post> postList = postRepository.findByIdLessThanAndMemberIn(lastPostId, id, pageable);
        List<PostHeart> postHeartList = postHeartRepository.findByMemberAndPostIn(member, postList);
//        log.info("멤버:{}", postList.get(0).getMember());

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {

            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPost().equals(post)) {
                    state = true;
                }
            }

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(post.getFilePath())
                    .heartCount(post.getHeartCount())
                    .tagList(Arrays.stream(post.getTag().split("#")).toList())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .postId(post.getId())
                    .build();
            postResponseDtoList.add(postResponseDto);
        }

        return postResponseDtoList;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow();
    }

    public List<Post> findByNickname(String nickname, Pageable pageable) {
        return postRepository.findByNickname(nickname, pageable);
    }

    @Transactional
    public void likePost(Long postId, Member member) {
        Post post = this.findById(postId);
        PostHeart exist = postHeartRepository.findByMemberAndPost(member, post);
        if (exist != null) {
            return;
        }

        post.plusHeartCount();

        PostHeart postHeart = PostHeart.builder()
                .post(post)
                .member(member)
                .build();
        postHeartRepository.save(postHeart);
    }

    @Transactional
    public void cancelLikePost(Long postId, Member member) {
        Post post = this.findById(postId);
        PostHeart exist = postHeartRepository.findByMemberAndPost(member, post);
        if (exist == null) {
            return;
        }

        post.minusHeartCount();

        postHeartRepository.deleteByPostAndMember(post, member);
    }

    public List<PostResponseDto> tagSearching(String tagSearching, Long latPostId, Member member, Pageable pageable) {
        List<Post> postList = postRepository.findByIdLessThanAndTagContaining(latPostId, tagSearching, pageable);
        List<PostHeart> postHeartList = postHeartRepository.findByMemberAndPostIn(member, postList);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {

            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPost().equals(post)) {
                    state = true;
                }
            }

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(post.getFilePath())
                    .heartCount(post.getHeartCount())
                    .tagList(Arrays.stream(post.getTag().replaceAll("#", " ").trim().split(" ")).toList())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .build();
            postResponseDtoList.add(postResponseDto);
        }
        log.info("여기가 두번쨰:{}", postResponseDtoList);

        return postResponseDtoList;
    }

    public void insertPost() {
        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < 0; i++) {
            Post post = Post.builder()
                    .tag("gg"+i)
                    .content("content"+i)
                    .createdAt(LocalDateTime.now())
                    .heartCount(0L)
                    .nickname("banson1")
                    .filePath(List.of("abcde")).build();
            postList.add(post);
        }
        postRepository.saveAll(postList);
    }
}
