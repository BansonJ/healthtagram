package com.banson.healthtagram.service;

import com.banson.healthtagram.entity.es.Tag;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.PostHeart;
import com.banson.healthtagram.dto.*;
import com.banson.healthtagram.entity.*;
import com.banson.healthtagram.repository.mongoRepository.PostHeartRepository;
import com.banson.healthtagram.repository.jpa.PostImageRepository;
import com.banson.healthtagram.repository.mongoRepository.PostRepository;
import com.banson.healthtagram.repository.elasticsearch.TagRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Value("${file.path}")
    private String filePath;

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostHeartRepository postHeartRepository;
    private final TagRepository tagRepository;

    @Transactional
    public PostResponseDto savePost(PostRequestDto postRequestDto, List<MultipartFile> multipartFile, Member member) {

        List<String> path = new ArrayList<>();

        for (MultipartFile multipartFile1 : multipartFile) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + multipartFile1.getOriginalFilename();
            String fullPath = filePath + fileName;
            File destinationFile = new File(fullPath);
            log.info("saving to: {}", destinationFile.getAbsolutePath());
            if (!multipartFile.isEmpty()) {
                try {
                    multipartFile1.transferTo(new File(fullPath));
                    log.info("저장성공: {}", fullPath);
                } catch (IOException e) {
                    log.info("저장실패: {}", fullPath);
                    e.printStackTrace();
                }
            }
            path.add(fullPath);
        }

        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .nickname(member.getNickname())
                .filePath(path)
                .heartCount(0L)
                .build();
        Post savedPost = postRepository.save(post);

        int i = 0;
        for (String path1 : path) {
            PostImage postImage = PostImage.builder()
                    .postId(post.getId())
                    .storedFileName(path1)
                    .originalFileName(multipartFile.get(i).getOriginalFilename()).build();
            postImageRepository.save(postImage);
            i++;
        }

        Tag tagList = tagRepository.save(Tag.builder()
                .tag(postRequestDto.getTagList())
                .postId(post.getId())
                .build());

        return PostResponseDto.builder()
                .content(savedPost.getContent())
                .createdAt(savedPost.getCreatedAt())
                .filePath(savedPost.getFilePath())
                .heartCount(savedPost.getHeartCount())
                .nickname(savedPost.getNickname())
                .tagList(tagList.getTag())
                .likeState(false)
                .postId(savedPost.getId())
                .build();
    }

    public List<PostResponseDto> findPostInMember(Long lastPostId, Member member, List<Member> id,Pageable pageable) {
        List<Post> postList = postRepository.findByIdLessThanAndNicknameIn(lastPostId, id.stream().map(Member::getNickname).toList(), pageable);
        List<PostHeart> postHeartList = postHeartRepository.findByMemberIdAndPostIdIn(member.getId(), postList.stream().map(Post::getId).toList());

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();


        for (Post post : postList) {

            log.info("{}", post.getId());
            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPostId().equals(post.getId())) {
                    state = true;
                }
            }

            Optional<Tag> tagList = tagRepository.findByPostId(post.getId());

            log.info(String.valueOf(post.getId()));

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(post.getFilePath())
                    .heartCount(post.getHeartCount())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .postId(post.getId())
                    .tagList(tagList.get().getTag())
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
        PostHeart exist = postHeartRepository.findByMemberIdAndPostId(member.getId(), postId);
        if (exist != null) {
            return;
        }

        post.plusHeartCount();
        postRepository.save(post);

        PostHeart postHeart = PostHeart.builder()
                .postId(postId)
                .memberId(member.getId())
                .build();
        postHeartRepository.save(postHeart);
    }

    @Transactional
    public void cancelLikePost(Long postId, Member member) {
        Post post = this.findById(postId);
        PostHeart exist = postHeartRepository.findByMemberIdAndPostId(member.getId(), postId);
        if (exist == null) {
            return;
        }

        post.minusHeartCount();
        postRepository.save(post);

        postHeartRepository.deleteByPostIdAndMemberId(postId, member.getId());
    }

    public List<PostResponseDto> tagSearching(String tagSearching, Long lastPostId, Member member, Pageable pageable) {
        List<Tag> tagList = tagRepository.findByTagAndPostIdLessThan(tagSearching, lastPostId, pageable);
        List<Long> postId = tagList.stream().map(Tag::getPostId).toList();

        List<Post> postList = postRepository.findByIdIn(postId, pageable);
        log.info(postList.get(0).toString());
        List<PostHeart> postHeartList = postHeartRepository.findByMemberIdAndPostIdIn(member.getId(), postList.stream().map(Post::getId).toList());
        log.info(postHeartList.toString());

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {

            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPostId().equals(post.getId())) {
                    state = true;
                }
            }

            Optional<Tag> tag = tagRepository.findByPostId(post.getId());

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(post.getFilePath())
                    .heartCount(post.getHeartCount())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .tagList(tag.get().getTag())
                    .build();
            postResponseDtoList.add(postResponseDto);
        }

        return postResponseDtoList;
    }

    public void insertPost() {
        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < 0; i++) {
            Post post = Post.builder()
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
