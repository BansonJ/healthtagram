package com.banson.healthtagram.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String reply;

    private String nickname;

    private Long heartCount;

    @CreatedDate
    private LocalDateTime replyDate;

    @OneToMany(mappedBy = "reply")
    private List<ReplyHeart> replyHeartList;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void plusHeartCount() {
        this.heartCount++;
    }

    public void minusHeartCount() {
        this.heartCount--;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

}
