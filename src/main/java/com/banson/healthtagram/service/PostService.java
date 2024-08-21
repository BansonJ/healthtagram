package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.*;
import com.banson.healthtagram.repository.PostHeartRepository;
import com.banson.healthtagram.repository.PostImageRepository;
import com.banson.healthtagram.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    public void savePost(PostRequestDto postRequestDto, List<MultipartFile> multipartFile, String nickname) {

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
                .nickname(nickname)
                .tag(postRequestDto.getTag())
                .filePath(path)
                .heartCount(0L)
                .build();
        postRepository.save(post);
        post.updateMember(memberService.findByNickname(nickname));

        int i = 0;
        for (String path1 : path) {
            PostImage postImage = PostImage.builder()
                    .postId(post.getId())
                    .storedFileName(path1)
                    .originalFileName(multipartFile.get(i).getOriginalFilename()).build();
            postImageRepository.save(postImage);
            i++;
        }
    }

    public List<PostResponseDto> findPostInMember(Long lastPostId, Member member, List<Member> id,Pageable pageable) {
        List<Post> postList = postRepository.findByIdLessThanAndMemberIn(lastPostId, id, pageable);
        log.info("여기가 첫번째:{}",postList);
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
                    .tagList(Arrays.stream(post.getTag().split("#")).toList())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .build();
            postResponseDtoList.add(postResponseDto);
        }
        log.info("여기가 두번쨰:{}", postResponseDtoList);

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
        PostHeart exist = postHeartRepository.findByMemberAndPost(member, postRepository.findById(postId).orElseThrow());
        if (exist != null) {
            return;
        }

        Post post = this.findById(postId);
        post.plusHeartCount();

        PostHeart postHeart = PostHeart.builder()
                .post(postRepository.findById(postId).orElseThrow())
                .member(member)
                .build();
        postHeartRepository.save(postHeart);
    }

    @Transactional
    public void cancelLikePost(Long postId, Member member) {
        PostHeart exist = postHeartRepository.findByMemberAndPost(member, postRepository.findById(postId).orElseThrow());
        if (exist == null) {
            return;
        }

        Post post = this.findById(postId);
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
                    .tagList(Arrays.stream(post.getTag().split("#")).toList())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .build();
            postResponseDtoList.add(postResponseDto);
        }
        log.info("여기가 두번쨰:{}", postResponseDtoList);

        return postResponseDtoList;
    }
}
