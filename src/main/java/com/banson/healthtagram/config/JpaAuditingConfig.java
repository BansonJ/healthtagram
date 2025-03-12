package com.banson.healthtagram.config;

import com.banson.healthtagram.entity.mongodb.DatabaseSequence;
import com.banson.healthtagram.repository.elasticsearch.TagRepository;
import com.banson.healthtagram.repository.mongoRepository.PostHeartRepository;
import com.banson.healthtagram.repository.mongoRepository.PostRepository;
import com.banson.healthtagram.repository.mongoRepository.ReplyHeartRepository;
import com.banson.healthtagram.repository.mongoRepository.ReplyRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.banson.healthtagram.repository.jpa",
        excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {TagRepository.class, PostRepository.class, PostHeartRepository.class,
                ReplyRepository.class, ReplyHeartRepository.class, DatabaseSequence.class}))
@Configuration
public class JpaAuditingConfig {
}
