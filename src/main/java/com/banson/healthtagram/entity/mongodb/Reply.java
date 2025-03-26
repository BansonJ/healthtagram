package com.banson.healthtagram.entity.mongodb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reply {
    @Transient // 영속성 필드 제외
    public static final String SEQUENCE_NAME = "reply_sequence";
    // 자동 증가 시퀀스에 대해 참조하는 SEQUENCE_NAME 선언

    @Id
    private Long id;

    @NotEmpty
    private String reply;

    @NotEmpty
    private String nickname;

    private Long heartCount;

    private Long postId;

    @CreatedDate
    private LocalDateTime replyDate;

    public void setId(Long id){
        this.id = id;
    }
}
