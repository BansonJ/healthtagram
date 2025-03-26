package com.banson.healthtagram.service;

import com.banson.healthtagram.entity.es.Tag;
import com.banson.healthtagram.repository.elasticsearch.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<String> tagSearching(String search, Pageable pageable) {
        List<Tag> tags = tagRepository.findByTagContaining(search, pageable);
        return tags.stream()
                .flatMap(tag -> {
                    // tag.getTag()는 쉼표로 구분된 태그들
                    String[] tagArray = tag.getTag().split(",");  // 쉼표로 분리

                    // 각 태그 배열에서 검색어와 정확히 일치하는 태그만 찾아 반환
                    return Arrays.stream(tagArray)
                            .map(String::trim)  // 공백을 제거하고
                            .filter(t -> t.contains(search.trim()));  // 정확히 일치하는 태그만 필터링
                })
                .collect(Collectors.toList());
    }
}
