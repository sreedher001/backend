package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.ReturnRequestDto;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.ReturnService;
import com.mindoot.onlinestore.utility.TokenUtils;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

	@Autowired
	private ReturnService returnService;
	
	@Autowired
	private TokenUtils tokenUtils;
	
	@PostMapping("/return-request")
	public ResponseEntity<?> requestReturn(
	        
	        @RequestBody ReturnRequestDto dto,
	@RequestHeader("Authorization") String authorization) {
	    		Long userId = this.tokenUtils.getUserId(authorization);

	    returnService.requestReturn(userId, dto);
	    return ResponseEntity.ok(new MessageResponse("Return requested",HttpStatus.OK));
	}

	
}
