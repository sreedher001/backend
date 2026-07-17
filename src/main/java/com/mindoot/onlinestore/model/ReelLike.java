package com.mindoot.onlinestore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reel_likes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReelLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id")
    private Reel reel;

}
