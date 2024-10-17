package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.Member;
import com.banson.healthtagram.entity.Post;
import com.banson.healthtagram.entity.PostHeart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    public static Member member1;
    public static Post post1;
    public static Post post2;

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
                .content("content")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();
        post2 = Post.builder()
                .content("content2")
                .nickname("banson1")
                .tag("#dd#aa")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .member(member1)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
    }

    @Test
    void findByIdLessThanAndMemberIn() {
        //given
        //when
        List<Post> byIdLessThanAndMemberIn = postRepository.findByIdLessThanAndMemberIn(post2.getId(), Arrays.asList(member1), pageable);
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
        List<Post> byIdLessThanAndTagContaining = postRepository.findByIdLessThanAndTagContaining(2L, "dd", pageable);
        //then
        Assertions.assertThat(byIdLessThanAndTagContaining.size()).isEqualTo(1);
        Assertions.assertThat(byIdLessThanAndTagContaining.get(0).getContent()).isEqualTo(post1.getContent());
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