package com.banson.healthtagram.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class PostHeart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member")
    private Member member;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post")
    private Post post;
}
