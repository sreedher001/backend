package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.ReelRequestDto;
import com.mindoot.onlinestore.dto.ReelResponseDto;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.ReelService;

@RestController
@RequestMapping("/api/admin/reels")
public class AdminReelController {

	@Autowired
	ReelService reelService;

	@PostMapping("/create")
	public ReelResponseDto createReel(@RequestBody ReelRequestDto reel) {
		return reelService.createReel(reel);
	}
	
	@PostMapping("/update/{id}")
    public ReelResponseDto updateReel(
            @PathVariable("id") Long id,
            @RequestBody ReelRequestDto reel) {
        return reelService.updateReel(id, reel);
    }
	
	@PostMapping("/delete/{id}")
    public  ResponseEntity<MessageResponse> deleteReel(@PathVariable("id") Long id) {
        reelService.deleteReel(id);
        return ResponseEntity.ok(new MessageResponse("Reel deleted successfully",HttpStatus.OK));
    }

}
