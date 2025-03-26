package com.banson.healthtagram.entity.mongodb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Post {
    @Transient // 영속성 필드 제외
    public static final String SEQUENCE_NAME = "post_sequence";
    // 자동 증가 시퀀스에 대해 참조하는 SEQUENCE_NAME 선언

    @Id
    private Long id;

    @NotEmpty
    private String content;

    @NotBlank
    private String nickname;

    private Long heartCount;

    @NotNull
    private List<String> filePath;

    @CreatedDate
    private LocalDateTime createdAt;

    public void setId(Long id){
        this.id = id;
    }

    public void plusHeartCount() {
        this.heartCount++;
    }

    public void minusHeartCount() {
        this.heartCount--;
    }

    public void setFilePath(List<String> filePathList) {
        this.filePath = filePathList;
    }
}
