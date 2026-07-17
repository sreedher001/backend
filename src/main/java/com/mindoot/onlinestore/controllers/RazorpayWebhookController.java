package com.mindoot.onlinestore.controllers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.service.OrderService;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/webhook")
public class RazorpayWebhookController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RazorpayWebhookController.class);
	
    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${razorpay.api.key}")
    private String razorpayKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;
    @Autowired
    private OrderService orderService; // service to update order status

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature1,
            @RequestHeader(value="x-razorpay-signature", required=false) String signature2,
            @RequestBody String payload) {
    	
    	String razorpaySignature = signature1 != null ? signature1 : signature2;
    	
    	LOGGER.info("razorpaySignature = {}",razorpaySignature);
    	LOGGER.warn("payload",payload);
//    	System.out.println("INFO: webhook invoked");
//    	System.out.println("INFO: razorpaySignature = " + razorpaySignature);
//    	System.out.println("WARN: payload = " + payload);

        try {
            // Verify the webhook signature
            boolean isValid = verifySignature(payload, razorpaySignature, webhookSecret);

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            // Parse webhook payload JSON
            JSONObject webhookJson = new JSONObject(payload);
            String event = webhookJson.getString("event");

            if ("payment.captured".equals(event)) {
                JSONObject payment = webhookJson.getJSONObject("payload")
                                              .getJSONObject("payment")
                                              .getJSONObject("entity");

                String razorpayOrderId = payment.getString("order_id");
                String razorpayPaymentId = payment.getString("id");
                System.out.println("payment>>>>>>>>> "+payment);
                RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
                com.razorpay.Order razorpayOrder = razorpayClient.orders.fetch(razorpayOrderId);
                System.out.println("razorpayOrder"+razorpayOrder);
                System.out.println("razorpayOrder--:"+razorpayOrder.toString());

                Object notesObj = razorpayOrder.get("notes");

                if (notesObj == null) {
                    throw new ApplicationException("Notes field is missing in Razorpay order", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                JSONObject notesJson;
                if (notesObj instanceof JSONObject) {
                    notesJson = (JSONObject) notesObj;
                } else {
                    // fallback conversion
                    try {
                        notesJson = new JSONObject(notesObj.toString());
                    } catch (Exception e) {
                        throw new ApplicationException("Notes field is malformed: " + notesObj, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

                if (!notesJson.has("internal_order_id")) {
                    throw new ApplicationException("internal_order_id missing in notes", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                String internalOrderId = notesJson.getString("internal_order_id");

                // process success
                orderService.handlePaymentSuccess(razorpayOrderId, razorpayPaymentId, internalOrderId);

                    return ResponseEntity.ok("Webhook processed..");
                

            }

            return ResponseEntity.ok("Event ignored");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    private boolean verifySignature(String payload, String actualSignature, String secret) throws Exception {
        String generatedSignature = hmacSha256(payload, secret);
        return generatedSignature.equals(actualSignature);
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return Hex.encodeHexString(hash);
    }
}

