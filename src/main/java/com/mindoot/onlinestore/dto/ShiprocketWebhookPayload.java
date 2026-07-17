package com.mindoot.onlinestore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ShiprocketWebhookPayload {
    private String awb;
    private String courierName;
    private String currentStatus;
    private Integer currentStatusId;
    private String shipmentStatus;
    private Integer shipmentStatusId;
    private String currentTimestamp;
    private String orderId;
    private Long srOrderId;
    private String awbAssignedDate;
    private String pickupScheduledDate;
    private String etd;
    private List<Scan> scans;
    private Integer isReturn;
    private Long channelId;
    private String podStatus;
    private String pod;

    @Data
    public static class Scan {
        private String date;
        private String status;
        private String activity;
        private String location;
        @JsonProperty("sr-status")
        private String srStatus;
        @JsonProperty("sr-status-label")
        private String srStatusLabel;
    }
}
