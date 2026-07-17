package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.ReturnStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "return_timelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long returnId;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private String changedBy; // USER / ADMIN / SYSTEM

    private LocalDateTime changedAt;
}
