package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPageResponse {

    private RatingSummaryResponse summary;

    private List<ReviewResponse> reviews;

    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean last;
}
