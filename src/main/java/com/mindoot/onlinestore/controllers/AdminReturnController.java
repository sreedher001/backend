package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.AdminReturnDetailDto;
import com.mindoot.onlinestore.dto.AdminReturnListDto;
import com.mindoot.onlinestore.dto.RefundCompleteRequest;
import com.mindoot.onlinestore.dto.ReturnDecisionDto;
import com.mindoot.onlinestore.dto.ReturnFlowDto;
import com.mindoot.onlinestore.enums.ReturnStatus;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.AdminReturnService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

/**
 * @author 91827
 *
 */
@RestController
@RequestMapping("/api/admin/returns")
public class AdminReturnController {

	@Autowired
	private AdminReturnService adminReturnService;

	@Autowired
	private TokenUtils tokenUtils;

	
	@GetMapping("/all-returns")
	public Page<AdminReturnListDto> getReturns(@RequestParam(name = "status", required = false) ReturnStatus status,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {

		return adminReturnService.getAllReturns(status, page, size);
	}

	@GetMapping("/{returnId}")
	public AdminReturnDetailDto getReturn(@PathVariable("returnId") Long returnId) {
		return adminReturnService.getReturnDetails(returnId);
	}

	@PostMapping("/{returnId}/approve")
	public ResponseEntity<MessageResponse> approve(@PathVariable("returnId") Long returnId,
			@RequestBody ReturnFlowDto returnFlowDto) {

		adminReturnService.approveReturn(returnId, returnFlowDto.getComment());
		return ResponseEntity.ok(new MessageResponse("Status updated", HttpStatus.OK));

	}

	@PostMapping("/{returnId}/reject")
	public ResponseEntity<MessageResponse> reject(@PathVariable("returnId") Long returnId,
			@RequestBody ReturnFlowDto returnFlowDto) {

		adminReturnService.rejectReturn(returnId, returnFlowDto.getComment());
		return ResponseEntity.ok(new MessageResponse("Status updated", HttpStatus.OK));
	}

	@PostMapping("/{returnId}/received")
	public ResponseEntity<MessageResponse> markReceived(@PathVariable("returnId") Long returnId,
			@RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);

		adminReturnService.markItemReceived(returnId, userInfo.getName());
		return ResponseEntity.ok(new MessageResponse("Marked as received", HttpStatus.OK));
	}

	@PostMapping("/{returnId}/quality-check")
	public ResponseEntity<MessageResponse> qualityCheck(@PathVariable("returnId") Long returnId,
			@RequestHeader("Authorization") String authorization, @RequestBody ReturnFlowDto returnFlowDto) {

		adminReturnService.qualityCheck(returnId, returnFlowDto.getPassed(), returnFlowDto.getComment());
		return ResponseEntity.ok(new MessageResponse("Quality check status updated", HttpStatus.OK));
	}
	
	
	@PostMapping("/{returnId}/review")
	public ResponseEntity<?> reviewReturn(
	        @PathVariable("returnId") Long returnId,
	        @RequestBody ReturnDecisionDto dto) {

		adminReturnService.reviewReturn(returnId, dto);
	    return ResponseEntity.ok(new MessageResponse("Return processed",HttpStatus.OK));
	}
	
	@PostMapping("/{returnId}/refund/initiate")
    public ResponseEntity<?> initiateRefund(
            @PathVariable("returnId") Long returnId,
            @RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
        adminReturnService.initiateRefund(returnId, userInfo.getName());
        return ResponseEntity.ok(
                new MessageResponse("Refund initiated successfully",HttpStatus.OK)
        );
    }

    /**
     * 2 Mark Refund Completed (Manual / Razorpay later)
     */
    @PostMapping("/{returnId}/refund/complete")
    public ResponseEntity<?> markRefundCompleted(
            @PathVariable("returnId") Long returnId,
            @RequestBody RefundCompleteRequest refundReference,
            @RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
        adminReturnService.markRefundCompleted(
                returnId,
                refundReference.getRefundReference(),
                userInfo.getName()
        );
        return ResponseEntity.ok(
                new MessageResponse("Refund marked as completed",HttpStatus.OK)
        );
    }

    /**
     *  Close Return (Final state)
     */
    @PostMapping("/{returnId}/close")
    public ResponseEntity<?> closeReturn(
            @PathVariable("returnId") Long returnId,
            @RequestHeader("Authorization") String authorization) {
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
        adminReturnService.closeReturn(returnId, userInfo.getName());
        return ResponseEntity.ok(
                new MessageResponse("Return closed successfully",HttpStatus.OK)
        );
    }
}
