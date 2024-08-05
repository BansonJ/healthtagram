package com.banson.heathtagram.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
