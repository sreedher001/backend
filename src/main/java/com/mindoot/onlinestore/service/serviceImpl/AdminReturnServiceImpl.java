package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.AdminReturnDetailDto;
import com.mindoot.onlinestore.dto.AdminReturnListDto;
import com.mindoot.onlinestore.dto.ReturnDecisionDto;
import com.mindoot.onlinestore.dto.ReturnTimelineDto;
import com.mindoot.onlinestore.enums.ReturnStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ReturnRequest;
import com.mindoot.onlinestore.model.ReturnTimeline;
import com.mindoot.onlinestore.repository.ReturnRequestRepository;
import com.mindoot.onlinestore.repository.ReturnTimelineRepository;
import com.mindoot.onlinestore.service.AdminReturnService;
import com.mindoot.onlinestore.service.ReturnTimelineService;

import jakarta.transaction.Transactional;

@Service
public class AdminReturnServiceImpl implements AdminReturnService {

	@Autowired
	private  ReturnRequestRepository returnRepo;
	
	@Autowired
    private  ReturnTimelineRepository timelineRepo;
	
	@Autowired
	private  ReturnTimelineService timelineService;
	
	@Transactional
	public void reviewReturn(Long returnId, ReturnDecisionDto dto) {

	    ReturnRequest request = findReturn(returnId);

	    if (request.getStatus() != ReturnStatus.REQUESTED) {
	        throw new ApplicationException("Return already processed",HttpStatus.BAD_REQUEST);
	    }

	    if (dto.isApprove()) {
	        request.setStatus(ReturnStatus.APPROVED);
	        request.setApprovedAt(LocalDateTime.now());
	        saveTimeline(returnId, ReturnStatus.APPROVED, "ADMIN");
	    } else {
	        request.setStatus(ReturnStatus.REJECTED);
	        request.setClosedAt(LocalDateTime.now());
	        saveTimeline(returnId, ReturnStatus.REJECTED, "ADMIN");
	    }

	    returnRepo.save(request);
	}
	private void saveTimeline(Long returnId, ReturnStatus status, String by) {
        ReturnTimeline t = new ReturnTimeline();
        t.setReturnId(returnId);
        t.setStatus(status);
        t.setChangedBy(by);
        t.setChangedAt(LocalDateTime.now());
        timelineRepo.save(t);
    }
	
	@Override
    public Page<AdminReturnListDto> getAllReturns(
            ReturnStatus status,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("requestedAt").descending());

        Page<ReturnRequest> returns = (status == null)
                ? returnRepo.findAll(pageable)
                : returnRepo.findAllByStatus(status, pageable);

        return returns.map(this::mapToListDto);
    }

    @Override
    public AdminReturnDetailDto getReturnDetails(Long returnId) {

        ReturnRequest r = findReturn(returnId);

        List<ReturnTimelineDto> timeline = timelineRepo
                .findByReturnIdOrderByChangedAtAsc(returnId)
                .stream()
                .map(t -> ReturnTimelineDto.builder()
                        .status(t.getStatus())
                        .changedBy(t.getChangedBy())
                        .changedAt(t.getChangedAt())
                        .build())
                .toList();

        return AdminReturnDetailDto.builder()
                .returnId(r.getId())
                .orderNumber(r.getOrder().getOrderNumber())
                .customerName(r.getUser().getUsername())
                .email(r.getUser().getEmail())
                .phone(r.getUser().getPhoneNumber())
                .productName(r.getVariant().getProduct().getName())
                .variantName(r.getVariant().getVariantName())
                .quantity(r.getQuantity())
                .reason(r.getReason())
                //.comment(r.getComment())
                .status(r.getStatus())
                .timeline(timeline)
                .build();
    }

    @Override
    public void approveReturn(Long returnId, String adminComment) {

        ReturnRequest r = getReturnOrThrow(returnId);

        r.setStatus(ReturnStatus.APPROVED);
        r.setAdminComment(adminComment);

        timelineService.log(returnId, ReturnStatus.APPROVED, "ADMIN");

        returnRepo.save(r);
    }

    @Override
    public void rejectReturn(Long returnId, String adminComment) {

        ReturnRequest r = getReturnOrThrow(returnId);

        r.setStatus(ReturnStatus.REJECTED);
        r.setAdminComment(adminComment);

        timelineService.log(returnId, ReturnStatus.REJECTED, "ADMIN");

        returnRepo.save(r);
    }

    private ReturnRequest getReturnOrThrow(Long id) {
        return returnRepo.findById(id)
                .orElseThrow(() -> new ApplicationException("Return not found",HttpStatus.BAD_REQUEST));
    }

    private AdminReturnListDto mapToListDto(ReturnRequest r) {
        return AdminReturnListDto.builder()
                .returnId(r.getId())
                .orderId(r.getOrder().getId())
                .orderNumber(r.getOrder().getOrderNumber())
                .productName(r.getVariant().getProduct().getName())
                .variantInfo(r.getVariant().getVariantName())
                .reason(r.getReason())
                .status(r.getStatus())
                .requestedAt(r.getRequestedAt())
                .customerName(r.getUser().getUsername())
                .build();
    }
    
    @Transactional
    public void markItemReceived(Long returnId, String adminUser) {

        ReturnRequest r = findReturn(returnId);

        // State validation (CRITICAL)
        if (!(r.getStatus() == ReturnStatus.PICKED_UP
           || r.getStatus() == ReturnStatus.IN_TRANSIT
           || r.getStatus() == ReturnStatus.APPROVED)) {
            throw new ApplicationException("Invalid return state",HttpStatus.BAD_REQUEST);
        }

        r.setStatus(ReturnStatus.RECEIVED_AT_WAREHOUSE);
        r.setReceivedAt(LocalDateTime.now());

        returnRepo.save(r);

        timelineService.log(
            returnId,
            ReturnStatus.RECEIVED_AT_WAREHOUSE,
            adminUser
        );
    }

    @Transactional
    @Override
    public void qualityCheck(Long returnId, boolean passed, String remarks) {

        ReturnRequest r = findReturn(returnId);

        if (r.getStatus() != ReturnStatus.RECEIVED_AT_WAREHOUSE) {
            throw new ApplicationException("Item not received yet",HttpStatus.BAD_REQUEST);
        }

        if (passed) {
            r.setStatus(ReturnStatus.QC_PASSED);
        } else {
            r.setStatus(ReturnStatus.MANUAL_ACTION_REQUIRED);
        }

        r.setQcRemarks(remarks);
        returnRepo.save(r);
    }

    private ReturnRequest findReturn(Long returnId) {
        return returnRepo.findById(returnId)
                .orElseThrow(() ->
                    new ApplicationException("Return not found with id: " + returnId,
                            HttpStatus.NOT_FOUND)
                );
    }

    @Transactional
    public void initiateRefund(Long returnId, String adminUser) {

        ReturnRequest r = findReturn(returnId);

        if (r.getStatus() != ReturnStatus.QC_PASSED) {
            throw new ApplicationException("Refund allowed only after QC passed",HttpStatus.BAD_REQUEST);
        }

        r.setStatus(ReturnStatus.REFUND_INITIATED);
        r.setRefundInitiatedAt(LocalDateTime.now());

        returnRepo.save(r);

        timelineService.log(
            returnId,
            ReturnStatus.REFUND_INITIATED,
            adminUser
        );
    }
    
    @Transactional
    public void markRefundCompleted(Long returnId, String refundRef, String adminUser) {

        ReturnRequest r = findReturn(returnId);

        if (r.getStatus() != ReturnStatus.REFUND_INITIATED) {
            throw new ApplicationException("Refund not initiated",HttpStatus.BAD_REQUEST);
        }

        r.setStatus(ReturnStatus.REFUND_COMPLETED);
        r.setRefundReference(refundRef);
        r.setRefundedAt(LocalDateTime.now());

        returnRepo.save(r);

        timelineService.log(
            returnId,
            ReturnStatus.REFUND_COMPLETED,
            adminUser
        );
    }

    @Transactional
    public void closeReturn(Long returnId, String adminUser) {

        ReturnRequest r = findReturn(returnId);

        if (r.getStatus() != ReturnStatus.REFUND_COMPLETED
            && r.getStatus() != ReturnStatus.QC_FAILED) {
            throw new ApplicationException("Return cannot be closed",HttpStatus.BAD_REQUEST);
        }

        r.setStatus(ReturnStatus.CLOSED);
        r.setClosedAt(LocalDateTime.now());

        returnRepo.save(r);

        timelineService.log(
            returnId,
            ReturnStatus.CLOSED,
            adminUser
        );
    }


}
