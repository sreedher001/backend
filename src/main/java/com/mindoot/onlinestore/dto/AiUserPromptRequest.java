package com.mindoot.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiUserPromptRequest {
    private String prompt;  // e.g., "Suggest wedding outfits for summer"
}
