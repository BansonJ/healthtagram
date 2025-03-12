package com.banson.healthtagram.entity.mongodb;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "replyHeart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReplyHeart {
    @Id
    private String id;

    private Long memberId;

    private Long replyId;
}
