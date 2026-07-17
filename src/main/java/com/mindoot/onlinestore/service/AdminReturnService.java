package com.mindoot.onlinestore.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.AdminReturnDetailDto;
import com.mindoot.onlinestore.dto.AdminReturnListDto;
import com.mindoot.onlinestore.dto.ReturnDecisionDto;
import com.mindoot.onlinestore.enums.ReturnStatus;

@Component
public interface AdminReturnService {

	public void reviewReturn(Long returnId, ReturnDecisionDto dto);
	Page<AdminReturnListDto> getAllReturns(
            ReturnStatus status,
            int page,
            int size
    );

    AdminReturnDetailDto getReturnDetails(Long returnId);

    void approveReturn(Long returnId, String adminComment);

    void rejectReturn(Long returnId, String adminComment);
	public void markItemReceived(Long returnId, String name);
	public void qualityCheck(Long returnId, boolean passed, String remarks);
	public void initiateRefund(Long returnId, String name);
	public void markRefundCompleted(Long returnId, String refundReference, String name);
	public void closeReturn(Long returnId, String name);

}
