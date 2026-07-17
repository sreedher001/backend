package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.AdminProductReviewDto;
import com.mindoot.onlinestore.service.AdminReviewService;


@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

	@Autowired
    private AdminReviewService adminReviewService;

    @GetMapping("/all-reviews")
    public ResponseEntity<Page<AdminProductReviewDto>> getAllReviews(
            @RequestParam(name="page",  defaultValue = "0") int page,
            @RequestParam(name="size",defaultValue = "20") int size,
            @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
            @RequestParam(name="direction",defaultValue = "desc") String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(adminReviewService.getAllReviews(pageable));
    }
}

