package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.repository.mongoRepository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    public Member member1;
    public Post post1;
    public Post post2;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .name("정승현1")
                .email("wjdtmdgus313@naver.com1")
                .nickname("banson1")
                .password("1234")
                .profilePicture(null)
                .build();
        post1 = Post.builder()
                .id(0L)
                .content("content")
                .nickname("banson1")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        post2 = Post.builder()
                .id(1L)
                .content("content2")
                .nickname("banson1")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
    }

    @AfterEach
    void setdown() {
        postRepository.deleteAll();
    }

    @Test
    void findByIdLessThanAndMemberIn() {
        //given
        //when
        List<Post> byIdLessThanAndMemberIn = postRepository.findByIdLessThanAndNicknameIn(post2.getId(), Arrays.asList(member1.getNickname()), pageable);
        //then
        Assertions.assertThat(byIdLessThanAndMemberIn.get(0).getContent()).isEqualTo(post1.getContent());
        Assertions.assertThat(byIdLessThanAndMemberIn.size()).isEqualTo(1);
    }

    @Test
    void findByNickname() {
        //given
        //when
        List<Post> post = postRepository.findByNickname("banson1", pageable);
        //then
        System.out.println("post = " + post.get(0).getId());
        System.out.println("post = " + post.get(1).getId());
        Assertions.assertThat(post.size()).isEqualTo(2);
    }

    @Test
    void findByIdLessThanAndTagContaining() {
        //given
        //when
        List<Post> byIdLessThanAndTagContaining = postRepository.findByIdIn(List.of(0L, 1L), pageable);
        //then
        //tag에 대한검증
        Assertions.assertThat(byIdLessThanAndTagContaining.size()).isEqualTo(2);
    }

    static Pageable pageable = new Pageable() {
        @Override
        public int getPageNumber() {
            return 1;
        }

        @Override
        public int getPageSize() {
            return 5;
        }

        @Override
        public long getOffset() {
            return 0;
        }

        @Override
        public Sort getSort() {
            return Sort.by(Sort.Direction.DESC, "id");
        }

        @Override
        public org.springframework.data.domain.Pageable next() {
            return null;
        }

        @Override
        public org.springframework.data.domain.Pageable previousOrFirst() {
            return null;
        }

        @Override
        public org.springframework.data.domain.Pageable first() {
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