package com.mindoot.onlinestore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.AdminOrderSummaryDto;
import com.mindoot.onlinestore.dto.CancelOrderRequest;
import com.mindoot.onlinestore.dto.CheckoutRequest;
import com.mindoot.onlinestore.dto.OrderDetailResponse;
import com.mindoot.onlinestore.dto.OrderResponse;
import com.mindoot.onlinestore.dto.OrderSummaryDto;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.utility.UserInfo;

@Component
public interface OrderService {

    OrderResponse checkout(Long userId, CheckoutRequest request);

    List<OrderSummaryDto> getOrderHistory(Long userId);

    OrderDetailResponse getOrderByOrderNumber(String orderNumber, Long userId);

    Page<AdminOrderSummaryDto> getOrdersForAdminByDateRange(LocalDate startDate, LocalDate endDate, int page, int size);

    Page<AdminOrderSummaryDto> getOrdersForAdminByDateRangeAndPurchaseType(LocalDate startDate, LocalDate endDate, String purchaseType, int page, int size);

    OrderStatus updateOrderStatus(Long id, OrderStatus status);

    void cancelOrder(CancelOrderRequest cancelOrderRequest, UserInfo userInfo);

    Order findByOrderNumberAndUserId(String orderNumber, Long userId);

    void rejectOrder(String orderNumber);

    AdminOrderSummaryDto getOrderDetails(Long orderId);

    void handlePaymentSuccess(String razorpayOrderId, String razorpayPaymentId, String internalOrderId);

    long getTotalOrdersCount();

    long getRetailOrdersCount();

    long getWholesaleOrdersCount();

    Double getTotalRevenue();

    Double getRetailRevenue();

    Double getWholesaleRevenue();
}
