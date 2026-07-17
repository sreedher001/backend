package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.ReturnRequestDto;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.enums.ReturnStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.ReturnRequest;
import com.mindoot.onlinestore.model.ReturnTimeline;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.ReturnRequestRepository;
import com.mindoot.onlinestore.repository.ReturnTimelineRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.ReturnService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReturnServiceImpl implements ReturnService{

	@Autowired
	private  ReturnRequestRepository returnRepo;
	@Autowired
    private  ReturnTimelineRepository timelineRepo;
	@Autowired
    private  OrderRepository orderRepo;
	@Autowired
    private  ProductVariantRepository variantRepo;
	@Autowired
    private  UserRepository userRepo;

    public void requestReturn(Long userId, ReturnRequestDto dto) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));

        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() -> new ApplicationException("Order not found",HttpStatus.NOT_FOUND));

        ProductVariant variant = variantRepo.findById(dto.getVariantId())
                .orElseThrow(() -> new ApplicationException("Variant not found",HttpStatus.NOT_FOUND));

        // Eligibility
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new ApplicationException("Order not delivered yet",HttpStatus.BAD_REQUEST);
        }

        if (returnRepo.existsByOrderAndVariant(order, variant)) {
            throw new ApplicationException("Return already requested",HttpStatus.BAD_REQUEST);
        }

        ReturnRequest request = new ReturnRequest();
        request.setUser(user);
        request.setOrder(order);
        request.setVariant(variant);
        request.setQuantity(dto.getQuantity());
        request.setReason(dto.getReason());
        request.setDescription(dto.getDescription());
        request.setStatus(ReturnStatus.REQUESTED);
        request.setRequestedAt(LocalDateTime.now());

        returnRepo.save(request);

        saveTimeline(request.getId(), ReturnStatus.REQUESTED, "USER");
    }

    private void saveTimeline(Long returnId, ReturnStatus status, String by) {
        ReturnTimeline t = new ReturnTimeline();
        t.setReturnId(returnId);
        t.setStatus(status);
        t.setChangedBy(by);
        t.setChangedAt(LocalDateTime.now());
        timelineRepo.save(t);
    }
}
