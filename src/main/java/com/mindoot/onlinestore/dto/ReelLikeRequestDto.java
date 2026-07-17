package com.mindoot.onlinestore.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReelLikeRequestDto {

    private Long userId;     // if logged in

    private String deviceId; // if guest user
    
    private Long reelId;

}
