package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.LikeViewCountDto;
import com.mindoot.onlinestore.dto.ReelLikeRequestDto;
import com.mindoot.onlinestore.dto.ReelResponseDto;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Reel;
import com.mindoot.onlinestore.repository.ReelRepository;
import com.mindoot.onlinestore.service.ReelService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

@RestController
@RequestMapping("/api/reels")
public class ReelController {

	@Autowired
    private ReelService reelService;
	@Autowired
	private TokenUtils tokenUtils;

    @GetMapping("/all-reel")
    public ResponseEntity <List<ReelResponseDto>> getReels(
    		 @RequestHeader(value = "Authorization", required = false) String authorization,
    	        @RequestHeader(value = "guestId", required = false) String deviceId) {
    	Long userId = null;

	    //  If logged in
	    if (authorization != null && authorization.startsWith("Bearer ")) {
	        UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
	        userId = userInfo.getId();
	    }

	    //  Safety check
	    if (userId == null && deviceId == null) {
	        throw new ApplicationException("Either Authorization or deviceId must be provided",HttpStatus.BAD_REQUEST);
	    }
    	
    	List<ReelResponseDto> allReel = reelService.getAllReel(userId, deviceId);
        return ResponseEntity.ok( allReel);
    }
    
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> addView(@PathVariable("id") Long id) {
        reelService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-like")
    public ResponseEntity<LikeViewCountDto> toggleLike(@RequestBody ReelLikeRequestDto requestDto,
    	@RequestHeader(name = "Authorization",required = false) String authorization) {
    	Long userId =null;
    	if(authorization!=null) {
    	 userId = this.tokenUtils.getUserId(authorization);}
    	
    	LikeViewCountDto resp = reelService.toggleLike(requestDto.getReelId(),userId,requestDto.getDeviceId());
        return ResponseEntity.ok(resp);
    }

}
