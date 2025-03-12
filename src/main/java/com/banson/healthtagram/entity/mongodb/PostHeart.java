package com.banson.healthtagram.entity.mongodb;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "postHeart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class PostHeart {
    @Id
    private String id;

    private Long memberId;

    private Long postId;
}
