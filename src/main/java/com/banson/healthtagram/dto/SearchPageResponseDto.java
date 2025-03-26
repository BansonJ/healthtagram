package com.banson.healthtagram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchPageResponseDto {

    private List<SearchResponseDto> searchDto; //검색 정보
}
