package com.banson.healthtagram.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String content;

    @NotNull
    private String nickname;

    private Long heartCount;

    @NotNull
    private List<String> filePath;

    @CreatedDate
    private LocalDateTime createdAt;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostHeart> postHeartList;

    @Setter
    private boolean likeState;

    public void updateMember(Member member) {
        this.member = member;
        member.addPost(this);
    }

    public void plusHeartCount() {
        this.heartCount++;
    }

}
