package com.mindoot.onlinestore.repository.projection;

import java.time.LocalDateTime;

public interface StockInterestSummaryProjection {

	Long getVariantId();

	Long getWaitingCount();

	LocalDateTime getOldestRequest();
}
