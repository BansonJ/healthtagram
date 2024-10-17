package com.banson.healthtagram.entity;

import com.banson.healthtagram.repository.ReplyRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private String content;

    @NotBlank
    private String nickname;

    private Long heartCount;

    @NotNull
    private List<String> filePath;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "tag")
    private String tag;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PostHeart> postHeartList;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reply> replyList;

    public void updateMember(Member member) {
        this.member = member;
    }

    public void plusHeartCount() {
        this.heartCount++;
    }

    public void minusHeartCount() {
        this.heartCount--;
    }

}
