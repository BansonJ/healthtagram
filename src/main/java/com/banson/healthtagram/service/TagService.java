package com.banson.healthtagram.service;

import com.banson.healthtagram.repository.elasticsearch.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;



}
