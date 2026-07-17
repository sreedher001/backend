package com.mindoot.onlinestore.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mindoot.onlinestore.dto.AdminOrderSummaryDto;
import com.mindoot.onlinestore.dto.InvoiceDto;
import com.mindoot.onlinestore.dto.UploadImageDto;
import com.mindoot.onlinestore.model.Invoice;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.InvoiceService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private TokenUtils tokenUtils;

    @PostMapping("/upload-invoice")
    public ResponseEntity<MessageResponse> uploadInvoice(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadata,@RequestHeader("Authorization") String token
    ) throws IOException {
    	 UserInfo userInfo = tokenUtils.getUserInfo(token.substring(7));
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());
    	 mapper.registerModule(new JavaTimeModule());
    	 InvoiceDto invoice = mapper.readValue(metadata, InvoiceDto.class);
        Invoice saved = invoiceService.uploadInvoice(file, invoice);
		return ResponseEntity.ok(new MessageResponse("Uploaded successfully", HttpStatus.OK));
    }
    
    @GetMapping("/all-invoices")
	public ResponseEntity<org.springframework.data.domain.Page<InvoiceDto>> getAllInvoices(
	    @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	    @RequestParam(name="page",defaultValue = "0") int page,
	    @RequestParam(name="size",defaultValue = "10") int size
	) {
	    org.springframework.data.domain.Page<InvoiceDto> pagedInvoice = invoiceService.getInvoiceForAdminByDateRange(startDate, endDate, page, size);
	    return ResponseEntity.ok(pagedInvoice);
	}

    

    @PostMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
