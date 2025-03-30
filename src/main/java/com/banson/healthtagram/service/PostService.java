package com.banson.healthtagram.service;

import com.banson.healthtagram.aop.annotation.Timer;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Async  //kafka로 처리해도 되지만 사실 단일 서버라 재시도 외에 큰 차이는 없다...
    public void saveFile(List<String> files, List<MultipartFile> multipartFile) {
        for (int i = 0; i < files.size(); i++)
        if (!files.get(i).isEmpty()) {
            try {
                multipartFile.get(i).transferTo(new File(files.get(i)));
                log.info("저장성공: {}", files.get(i));
            } catch (IOException e) {
                log.info("저장실패: {}", files.get(i));
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public PostResponseDto savePost(PostRequestDto postRequestDto, List<MultipartFile> multipartFile, Member member) {

        List<String> path = new ArrayList<>();

        //랜덤uid + fileName으로 이름 설정
        for (MultipartFile multipartFile1 : multipartFile) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + multipartFile1.getOriginalFilename();
            String fullPath = filePath + fileName;
            path.add(fullPath);
        }
        saveFile(path, multipartFile);

        //포스트 저장
        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .nickname(member.getNickname())
                .filePath(path)
                .heartCount(0L)
                .createdAt(LocalDateTime.now())
                .build();
        Post savedPost = postRepository.save(post);

        //포스트의 이미지는 따로 저장. 근데 그냥 post에 넣어도 됟듯
        int i = 0;
        for (String path1 : path) {
            PostImage postImage = PostImage.builder()
                    .postId(post.getId())
                    .storedFileName(path1)
                    .originalFileName(multipartFile.get(i).getOriginalFilename()).build();
            postImageRepository.save(postImage);
            i++;
        }

        //태그 저장
        Tag tagList = tagRepository.save(Tag.builder()
                .tag(postRequestDto.getTagList())
                .postId(post.getId())
                .build());

        log.info("postid: {}", post.getId());
        log.info("taglist: {}", tagList.getTag());

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

            List<String> updatedFilePaths = new ArrayList<>();
            for (String filePath : post.getFilePath()) {
                // 로컬 경로를 HTTP URL로 변환 (예: C:/uploads/image.jpg -> http://localhost:8080/api/uploads/image.jpg)
                String fileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(filePath).getName();
                updatedFilePaths.add(fileUrl);
            }

            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPostId().equals(post.getId())) {
                    state = true;
                }
            }

            Optional<Tag> tagList = tagRepository.findByPostId(post.getId());

            if (tagList.isEmpty() || tagList == null) {
                tagList = Optional.ofNullable(Tag.builder().
                        tag("많은이용부탁드림")
                        .postId(post.getId())
                        .id("random").build());
            }

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(updatedFilePaths)
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

    public List<Post> findPostInMember(String nickname, Long lastPostId, Pageable pageable) {
        return postRepository.findByIdLessThanAndNicknameIn(lastPostId, Collections.singletonList(nickname), pageable);
    }

    @Transactional
    public void likePost(Long postId, Member member) {
        PostHeart exist = postHeartRepository.findByMemberIdAndPostId(member.getId(), postId);

        if (exist != null) {
            return;
        }

        log.info("좋아요 증가 전 postId: {}", postId);

        // ✅ 기존 문서 업데이트 (새로운 문서 추가 X)
        postRepository.updateHeartCount(postId);

        log.info("좋아요 증가 완료 postId: {}", postId);

        PostHeart postHeart = PostHeart.builder()
                .postId(postId)
                .memberId(member.getId())
                .build();
        postHeartRepository.save(postHeart);
    }

    @Transactional
    public void cancelLikePost(Long postId, Member member) {
        PostHeart exist = postHeartRepository.findByMemberIdAndPostId(member.getId(), postId);

        if (exist == null) {
            return;
        }

        log.info("좋아요 취소 전 postId: {}", postId);

        // ✅ 기존 문서의 좋아요 수 감소 (새로운 문서 추가 X)
        postRepository.decreaseHeartCount(postId);

        log.info("좋아요 취소 완료 postId: {}", postId);

        // ✅ 좋아요 기록 삭제
        postHeartRepository.deleteByPostIdAndMemberId(postId, member.getId());
    }

    public List<PostResponseDto> tagSearching(String tagSearching, Long lastPostId, Member member, Pageable pageable) {
        Pageable pageable1 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("postId").descending());
        List<Tag> tagList = tagRepository.findByTagAndPostIdLessThan(tagSearching, lastPostId, pageable1);
        List<Long> postId = tagList.stream().map(Tag::getPostId).toList();

        List<Post> postList = postRepository.findByIdIn(postId, pageable);
        log.info(postList.get(0).toString());
        List<PostHeart> postHeartList = postHeartRepository.findByMemberIdAndPostIdIn(member.getId(), postList.stream().map(Post::getId).toList());
        log.info("zzzzzzz:{}" , postHeartList.toString());

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {

            boolean state = false;
            for (int i = 0; i < postHeartList.size(); i++) {
                if (postHeartList.get(i).getPostId().equals(post.getId())) {
                    state = true;
                }
            }

            List<String> updatedFilePaths = new ArrayList<>();
            for (String filePath : post.getFilePath()) {
                // 로컬 경로를 HTTP URL로 변환 (예: C:/uploads/image.jpg -> http://localhost:8080/api/uploads/image.jpg)
                String fileUrl = "http://localhost:8080/api/study/healthtagramImage/" + new File(filePath).getName();
                updatedFilePaths.add(fileUrl);
            }

            Optional<Tag> tag = tagRepository.findByPostId(post.getId());

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .filePath(updatedFilePaths)
                    .heartCount(post.getHeartCount())
                    .nickname(post.getNickname())
                    .likeState(state)
                    .postId(post.getId())
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
