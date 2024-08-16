package com.banson.healthtagram.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty
    private String password;

    @Column(name = "name", nullable = false)
    @NotEmpty
    private String name;

    @Column(name = "nickname", unique = true, nullable = false)
    @NotEmpty
    private String nickname;

    @Column(name = "profile_picture")
    private String profilePicture;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followingList;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followerList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> postList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHeart> postHeartList;

    public void addPost(Post post) {
        this.postList.add(post);
    }

}
