package com.mindoot.onlinestore.utility;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindoot.onlinestore.model.Order;

public class PdfUtils {

	@Value("${app.mail.from}")
	private String mailFrom;
	
	public byte[] generatePdf(Order order) throws Exception {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4, 50, 50, 60, 50);
	    PdfWriter writer = PdfWriter.getInstance(document, out);

	    // Add header/footer
	    writer.setPageEvent(new HeaderFooterPageEvent());

	    document.open();

	    // Add logo
	    Image logo = Image.getInstance("classpath:/static/images/logo.png"); // adjust path if needed
	    logo.scaleToFit(120, 50);
	    logo.setAlignment(Element.ALIGN_LEFT);
	    document.add(logo);

	    // Title
	    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
	    Paragraph title = new Paragraph("INVOICE", titleFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    title.setSpacingBefore(10);
	    title.setSpacingAfter(20);
	    document.add(title);

	    // Customer + Order Info Table
	    Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	    Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
	    PdfPTable infoTable = new PdfPTable(2);
	    infoTable.setWidthPercentage(100);
	    infoTable.setSpacingAfter(30);
	    infoTable.setWidths(new float[]{1.5f, 3.5f});

	    addInfoRow(infoTable, "Order Number:", order.getOrderNumber(), labelFont, valueFont);
	    addInfoRow(infoTable, "Customer Name:", order.getUser().getUsername(), labelFont, valueFont);
	    addInfoRow(infoTable, "Payment ID:", order.getPaymentId(), labelFont, valueFont);
	    addInfoRow(infoTable, "Paid Amount:", "₹" + String.format("%.2f", order.getTotalAmount()), labelFont, valueFont);

	    document.add(infoTable);

	    // Order Summary (if needed - example)
	    PdfPTable summaryTable = new PdfPTable(4);
	    summaryTable.setWidthPercentage(100);
	    summaryTable.setWidths(new float[]{3, 1, 1, 1});

	    addTableHeader(summaryTable, Arrays.asList("Item", "Qty", "Price", "Total"));
	 // Loop through order items
	    order.getItems().forEach(item-> {
	        summaryTable.addCell(item.getProductName()); // Or item.getProduct().getName()
	        summaryTable.addCell(String.valueOf(item.getQuantity()));
	        summaryTable.addCell("₹" + String.format("%.2f", item.getPrice()));
	        double total = item.getQuantity() * item.getPrice();
	        summaryTable.addCell("₹" + String.format("%.2f", total));
	    });


	    summaryTable.setSpacingAfter(30);
	    document.add(summaryTable);

	    // Thank-you Note
	    Font thankFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.GRAY);
	    Paragraph thanks = new Paragraph("Thank you for shopping with us!\nWe hope to serve you again.", thankFont);
	    thanks.setAlignment(Element.ALIGN_CENTER);
	    thanks.setSpacingBefore(30);
	    document.add(thanks);

	    // Footer (optional)
	    Paragraph footer = new Paragraph("For any queries, contact: support@zfc.com | +91-1234567890", valueFont);
	    footer.setAlignment(Element.ALIGN_CENTER);
	    footer.setSpacingBefore(20);
	    document.add(footer);

	    document.close();
	    return out.toByteArray();
	}
	
	private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
	    PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
	    PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
	    styleCell(cell1);
	    styleCell(cell2);
	    table.addCell(cell1);
	    table.addCell(cell2);
	}

	private void addTableHeader(PdfPTable table, java.util.List<String> headers) {
	    for (String header : headers) {
	        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
	        cell.setBackgroundColor(BaseColor.BLUE);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setPadding(8);
	        table.addCell(cell);
	    }
	}

	private void styleCell(PdfPCell cell) {
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setPadding(8);
	}

	
	// Header & Footer Event Handler
		class HeaderFooterPageEvent extends PdfPageEventHelper {
		    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.GRAY);
		    Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);

		    @Override
		    public void onEndPage(PdfWriter writer, Document document) {
		        PdfPTable header = new PdfPTable(1);
		        header.setTotalWidth(527);
		        header.setLockedWidth(true);
		        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		        header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		        header.addCell(new Phrase("Spice Store", headerFont));
		        header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());

		        PdfPTable footer = new PdfPTable(1);
		        footer.setTotalWidth(527);
		        footer.setLockedWidth(true);
		        footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		        footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		        footer.addCell(new Phrase("Contact: "+mailFrom, footerFont));
		        footer.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());
		    }
		}

}
