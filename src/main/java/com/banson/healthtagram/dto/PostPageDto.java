package com.banson.healthtagram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPageDto {
    List<PostResponseDto> postResponseDto;
    private int pageNo; //현재 페이지
    private int pageSize;   //화면 하단에 나타낼 페이지 크기
    private long totalElements; //페이지의 담긴 데이터 수
    private int totalPages; //총 페이지
}
