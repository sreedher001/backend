package com.mindoot.onlinestore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(nullable = false)
    private String videoUrl;

    private String thumbnailUrl;

    private Integer durationSeconds;

    private Long views = 0L;

    private Long likes = 0L;

    private Boolean isActive = true;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}