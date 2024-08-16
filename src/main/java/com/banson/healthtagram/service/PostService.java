package com.banson.healthtagram.service;

import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.*;
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

    public List<PostResponseDto> findPostInMember(Long lastPostId, Member member, Pageable pageable) {
        List<Member> id = new ArrayList<>();
        id.add(member);
        for (Follow follow : member.getFollowingList()) {
            id.add(follow.getFollowing());
        }

        log.info("시작");
        List<Post> postList = postRepository.findByIdLessThanAndMemberIn(lastPostId, id, pageable);
        log.info("끝:{}", postList);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(post.getFilePath())
                    .heartCount(post.getHeartCount())
                    .tagList(Arrays.stream(post.getTag().split("#")).toList())
                    .nickname(post.getNickname())
                    .build();
            postResponseDtoList.add(postResponseDto);
        }

        return postResponseDtoList;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow();
    }

    public void likePost(Long postId) {
        Post post = this.findById(postId);
        post.plusHeartCount();
    }
}
