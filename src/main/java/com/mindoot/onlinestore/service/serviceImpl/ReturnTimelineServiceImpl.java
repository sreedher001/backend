package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.enums.ReturnStatus;
import com.mindoot.onlinestore.model.ReturnTimeline;
import com.mindoot.onlinestore.repository.ReturnTimelineRepository;
import com.mindoot.onlinestore.service.ReturnTimelineService;

@Service
public class ReturnTimelineServiceImpl implements ReturnTimelineService {

	@Autowired
    private ReturnTimelineRepository timelineRepo;

    @Override
    public void log(Long returnId, ReturnStatus status, String changedBy) {

        ReturnTimeline timeline = new ReturnTimeline();
        timeline.setReturnId(returnId);
        timeline.setStatus(status);
        timeline.setChangedBy(changedBy);
        timeline.setChangedAt(LocalDateTime.now());

        timelineRepo.save(timeline);
    }
}
