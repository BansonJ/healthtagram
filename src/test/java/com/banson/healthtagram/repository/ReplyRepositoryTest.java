package com.banson.healthtagram.repository;

import com.banson.healthtagram.entity.mongodb.Post;
import com.banson.healthtagram.entity.mongodb.Reply;
import com.banson.healthtagram.repository.mongoRepository.ReplyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
class ReplyRepositoryTest {
    @Autowired
    ReplyRepository replyRepository;

    @Test
    void findByPostAndIdLessThan() {
        //given
        Post post1 = Post.builder()
                .id(0L)
                .content("content")
                .nickname("banson1")
                .filePath(Arrays.asList("filePath"))
                .heartCount(0L)
                .build();
        Reply reply1 = Reply.builder()
                .id(0L)
                .reply("reply1")
                .replyDate(LocalDateTime.now())
                .heartCount(0L)
                .nickname("banson1")
                .postId(post1.getId())
                .build();
        replyRepository.save(reply1);
        //when
        List<Reply> byPostAndIdLessThan = replyRepository.findByPostIdAndIdLessThan(post1.getId(), 5L, pageable);
        //then
        Assertions.assertThat(byPostAndIdLessThan.get(0).getReply()).isEqualTo(reply1.getReply());
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