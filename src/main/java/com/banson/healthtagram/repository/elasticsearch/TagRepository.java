package com.banson.healthtagram.repository.elasticsearch;

import com.banson.healthtagram.entity.es.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends ElasticsearchRepository<Tag, String> {
    Optional<Tag> findByPostId(Long PostId);

    List<Tag> findByTagAndPostIdLessThan(String tagSearching, Long PostId, Pageable pageable);
}
