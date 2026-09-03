package com.sunrise.dental.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds a printable/exportable PDF for any of the Reports screen's tables:
 * a title, the applied date range, and the report's headings + rows.
 */
public class PdfReportGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static byte[] generate(String reportTitle, LocalDate from, LocalDate to,
                                   List<String> headers, List<List<String>> rows) throws DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font subFont = new Font(Font.HELVETICA, 10, Font.ITALIC, java.awt.Color.DARK_GRAY);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, java.awt.Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 10);

        Paragraph clinicName = new Paragraph("Sunrise Dental Clinic", titleFont);
        clinicName.setAlignment(Element.ALIGN_CENTER);
        document.add(clinicName);

        Paragraph title = new Paragraph(reportTitle, new Font(Font.HELVETICA, 14, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(4);
        document.add(title);

        Paragraph range = new Paragraph(
                "Date range: " + from.format(DATE_FMT) + " to " + to.format(DATE_FMT) +
                "   |   Generated: " + LocalDate.now().format(DATE_FMT), subFont);
        range.setAlignment(Element.ALIGN_CENTER);
        range.setSpacingBefore(2);
        range.setSpacingAfter(16);
        document.add(range);

        if (rows.isEmpty()) {
            document.add(new Paragraph("No data found for this date range.", cellFont));
        } else {
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(0x12, 0x3a, 0x5e));
                cell.setPadding(6);
                table.addCell(cell);
            }
            for (List<String> row : rows) {
                for (String value : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(value == null ? "-" : value, cellFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }
            document.add(table);
        }

        document.close();
        return out.toByteArray();
    }
}
