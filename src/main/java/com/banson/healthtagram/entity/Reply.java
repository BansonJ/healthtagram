package com.banson.healthtagram.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String reply;

    private String nickname;

    private Long heartCount;

    private LocalDateTime replyDate;

}
