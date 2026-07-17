package com.mindoot.onlinestore.service.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mindoot.onlinestore.dto.InvoiceDto;
import com.mindoot.onlinestore.dto.InvoiceItemDto;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Invoice;
import com.mindoot.onlinestore.model.InvoiceItem;
import com.mindoot.onlinestore.repository.InvoiceRepository;
import com.mindoot.onlinestore.service.InvoiceService;
import com.mindoot.onlinestore.service.LocalFileStorageService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private LocalFileStorageService fileStorageService;

	@Override
	public Invoice uploadInvoice(MultipartFile file, InvoiceDto invoiceDto) throws IOException {
		Invoice invoice = new Invoice();

		String fileUrl = fileStorageService.uploadImage(file, "invoices");
		invoice.setS3Url(fileUrl);
		invoice.setFileName(file.getOriginalFilename());
		invoice.setCurrency(invoiceDto.getCurrency());
		invoice.setVendorName(invoiceDto.getVendorName());
		invoice.setInvoiceDate(invoiceDto.getInvoiceDate());
		invoice.setCategory(invoiceDto.getCategory());
		invoice.setPaymentMethod(invoiceDto.getPaymentMethod());
		invoice.setTags(invoiceDto.getTags());

		List<InvoiceItem> items = invoiceDto.getInvoiceItem().stream().map(itemDto -> {
			InvoiceItem item = new InvoiceItem();
			item.setItemName(itemDto.getItemName());
			item.setQuantity(itemDto.getQuantity());
			item.setUnitPrice(itemDto.getPrice());
			item.setInvoice(invoice);
			return item;
		}).collect(Collectors.toList());

		invoice.setItems(items);
		BigDecimal totalAmount = invoice.calculateTotalAmount();
		invoice.setAmount(totalAmount);

		return invoiceRepository.save(invoice);
	}

	@Override
	public void deleteInvoice(Long id) {
		Invoice invoice = invoiceRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("Invoice not found with ID", HttpStatus.NOT_FOUND));

		if (invoice.getS3Url() != null) {
			fileStorageService.deleteFile(invoice.getS3Url());
		}

		invoiceRepository.delete(invoice);
	}

	@Override
	public Page<InvoiceDto> getInvoiceForAdminByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

		Page<Invoice> invoicePage = invoiceRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(startDateTime, endDateTime, pageable);

		return invoicePage.map(invoice -> InvoiceDto.builder()
			.id(invoice.getId())
			.fileName(invoice.getFileName())
			.s3Url(invoice.getS3Url())
			.vendorName(invoice.getVendorName())
			.amount(invoice.getAmount())
			.currency(invoice.getCurrency())
			.invoiceDate(invoice.getInvoiceDate())
			.category(invoice.getCategory())
			.paymentMethod(invoice.getPaymentMethod())
			.tags(invoice.getTags())
			.invoiceItem(invoice.getItems().stream().map(item -> InvoiceItemDto.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.quantity(item.getQuantity())
				.price(item.getUnitPrice())
				.totalPrice(item.getTotalPrice())
				.build()
			).toList())
			.build());
	}
}
