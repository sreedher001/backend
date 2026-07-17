package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.StockInterestRequest;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.StockInterestService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

@RestController
@RequestMapping("/api/interest")
public class StockInterestController {

	@Autowired
	private StockInterestService service;
	
	@Autowired
	private TokenUtils tokenUtils;

    @PostMapping("/variants/{variantId}/stock-interest")
    public ResponseEntity<?> registerStockInterest(
            @PathVariable("variantId") Long variantId,
            @RequestHeader(name="Authorization",required = false) String authorization,
            @RequestBody(required = false) StockInterestRequest request) {

        Long userId = null;
        String email = null;

        if (authorization != null) {
        	UserInfo user = this.tokenUtils.getUserInfo(authorization);
            userId = user.getId();
            email=user.getEmail();
        } else {
            email = request.email();
        }

        service.registerInterest(variantId, userId, email);

        return ResponseEntity.ok(new MessageResponse("Interest recorded",HttpStatus.OK) );
    }
}
