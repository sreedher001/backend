package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ReturnTimeline;

@Repository
public interface ReturnTimelineRepository
        extends JpaRepository<ReturnTimeline, Long> {

    List<ReturnTimeline> findByReturnId(Long returnId);
    List<ReturnTimeline> findByReturnIdOrderByChangedAtAsc(Long returnId);
}

