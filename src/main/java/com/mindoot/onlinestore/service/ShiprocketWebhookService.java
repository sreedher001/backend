package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

@Component
public interface ShiprocketWebhookService {

	public void handleWebhook(String payload);
}
