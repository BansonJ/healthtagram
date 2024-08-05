package com.banson.heathtagram.entity;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    private Long id;

    @Column
    private String content;

    private String picture;

    private String nickname;

    private Long heartCount;

    private LocalDateTime createdAt;

}
