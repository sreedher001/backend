package com.mindoot.onlinestore.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.InvoiceDto;
import com.mindoot.onlinestore.model.Invoice;

@Component
public interface InvoiceService {

	Invoice uploadInvoice(MultipartFile file, InvoiceDto invoice) throws IOException;

	void deleteInvoice(Long id);

	Page<InvoiceDto> getInvoiceForAdminByDateRange(LocalDate startDate, LocalDate endDate, int page, int size);

	
}
