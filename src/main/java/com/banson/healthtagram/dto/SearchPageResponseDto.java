package com.banson.healthtagram.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchPageResponseDto {

    private List<SearchResponseDto> searchDto; //검색 정보
    private int pageNo; //현재 페이지
    private int pageSize;   //화면 하단에 나타낼 페이지 크기
    private long totalElements; //페이지의 담긴 데이터 수
    private int totalPages; //총 페이지
}
