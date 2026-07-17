package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.enums.ReturnStatus;


public interface ReturnTimelineService {

	void log(Long returnId, ReturnStatus status, String changedBy);
}
