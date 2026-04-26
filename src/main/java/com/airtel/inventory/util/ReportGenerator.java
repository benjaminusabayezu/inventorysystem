package com.airtel.inventory.util;

import com.airtel.inventory.dto.DeviceDTO;
import com.airtel.inventory.dto.TransactionDTO;
import com.airtel.inventory.dto.UserDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final BaseColor AIRTEL_RED = new BaseColor(220, 30, 40);
    private static final BaseColor LIGHT_GRAY = new BaseColor(240, 240, 240);

    // ── PDF ─────────────────────────────────────────────

    public byte[] generateDevicesPDF(List<DeviceDTO> devices) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, baos);
        doc.open();
        addPdfHeader(doc, "Device Inventory Report");
        addPdfTimestamp(doc);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 4, 3, 3, 2, 2, 3, 4});
        for (String h : new String[]{"ID","Name","Type","Serial No.","Condition","Status","Brand","Assigned To"})
            addHeaderCell(table, h);

        boolean alt = false;
        for (DeviceDTO d : devices) {
            BaseColor bg = alt ? LIGHT_GRAY : BaseColor.WHITE;
            addCell(table, String.valueOf(d.getId()), bg);
            addCell(table, d.getName(), bg);
            addCell(table, d.getType() != null ? d.getType().name() : "-", bg);
            addCell(table, d.getSerialNumber(), bg);
            addCell(table, d.getCondition() != null ? d.getCondition().name() : "-", bg);
            addCell(table, d.getStatus() != null ? d.getStatus().name() : "-", bg);
            addCell(table, d.getBrand() != null ? d.getBrand() : "-", bg);
            addCell(table, d.getAssignedToUserName() != null ? d.getAssignedToUserName() : "Not assigned", bg);
            alt = !alt;
        }
        doc.add(table);
        addPdfFooter(doc, devices.size() + " devices total");
        doc.close();
        return baos.toByteArray();
    }

    public byte[] generateTransactionsPDF(List<TransactionDTO> transactions) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, baos);
        doc.open();
        addPdfHeader(doc, "Transaction History Report");
        addPdfTimestamp(doc);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 4, 4, 3, 3, 3, 4});
        for (String h : new String[]{"ID","Device","Serial No.","Action","User","Performed By","Date"})
            addHeaderCell(table, h);

        boolean alt = false;
        for (TransactionDTO t : transactions) {
            BaseColor bg = alt ? LIGHT_GRAY : BaseColor.WHITE;
            addCell(table, String.valueOf(t.getId()), bg);
            addCell(table, t.getDeviceName() != null ? t.getDeviceName() : "-", bg);
            addCell(table, t.getDeviceSerial() != null ? t.getDeviceSerial() : "-", bg);
            addCell(table, t.getAction() != null ? t.getAction().name() : "-", bg);
            addCell(table, t.getUserName() != null ? t.getUserName() : "-", bg);
            addCell(table, t.getPerformedByName() != null ? t.getPerformedByName() : "-", bg);
            addCell(table, t.getActionDate() != null ? t.getActionDate().format(FORMATTER) : "-", bg);
            alt = !alt;
        }
        doc.add(table);
        addPdfFooter(doc, transactions.size() + " transactions total");
        doc.close();
        return baos.toByteArray();
    }

    public byte[] generateUsersPDF(List<UserDTO> users) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 40, 30);
        PdfWriter.getInstance(doc, baos);
        doc.open();
        addPdfHeader(doc, "Staff / User Report");
        addPdfTimestamp(doc);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 4, 3, 3, 3, 2, 2});
        for (String h : new String[]{"Emp ID","Full Name","Department","Position","Email","Role","Devices"})
            addHeaderCell(table, h);

        boolean alt = false;
        for (UserDTO u : users) {
            BaseColor bg = alt ? LIGHT_GRAY : BaseColor.WHITE;
            addCell(table, u.getEmployeeId(), bg);
            addCell(table, u.getFullName(), bg);
            addCell(table, u.getDepartment(), bg);
            addCell(table, u.getPosition() != null ? u.getPosition() : "-", bg);
            addCell(table, u.getEmail() != null ? u.getEmail() : "-", bg);
            addCell(table, u.getRole() != null ? u.getRole().name() : "-", bg);
            addCell(table, String.valueOf(u.getAssignedDeviceCount()), bg);
            alt = !alt;
        }
        doc.add(table);
        addPdfFooter(doc, users.size() + " users total");
        doc.close();
        return baos.toByteArray();
    }

    // ── Excel ────────────────────────────────────────────

    public byte[] generateDevicesExcel(List<DeviceDTO> devices) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Devices");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle altStyle = createAltStyle(wb);
            String[] headers = {"ID","Name","Type","Serial Number","Condition","Status","Brand","Model","Assigned To","Purchase Date","Created At"};
            createHeaderRow(sheet, headers, headerStyle);
            int rowIdx = 1;
            for (DeviceDTO d : devices) {
                Row row = sheet.createRow(rowIdx++);
                CellStyle style = (rowIdx % 2 == 0) ? altStyle : null;
                setCell(row, 0, String.valueOf(d.getId()), style);
                setCell(row, 1, d.getName(), style);
                setCell(row, 2, d.getType() != null ? d.getType().name() : "", style);
                setCell(row, 3, d.getSerialNumber(), style);
                setCell(row, 4, d.getCondition() != null ? d.getCondition().name() : "", style);
                setCell(row, 5, d.getStatus() != null ? d.getStatus().name() : "", style);
                setCell(row, 6, d.getBrand() != null ? d.getBrand() : "", style);
                setCell(row, 7, d.getModel() != null ? d.getModel() : "", style);
                setCell(row, 8, d.getAssignedToUserName() != null ? d.getAssignedToUserName() : "Not assigned", style);
                setCell(row, 9, d.getPurchaseDate() != null ? d.getPurchaseDate().format(FORMATTER) : "", style);
                setCell(row, 10, d.getCreatedAt() != null ? d.getCreatedAt().format(FORMATTER) : "", style);
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateTransactionsExcel(List<TransactionDTO> transactions) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Transactions");
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle altStyle = createAltStyle(wb);
            String[] headers = {"ID","Device","Serial No.","Action","User","Performed By","Date","Expected Return","Notes"};
            createHeaderRow(sheet, headers, headerStyle);
            int rowIdx = 1;
            for (TransactionDTO t : transactions) {
                Row row = sheet.createRow(rowIdx++);
                CellStyle style = (rowIdx % 2 == 0) ? altStyle : null;
                setCell(row, 0, String.valueOf(t.getId()), style);
                setCell(row, 1, t.getDeviceName() != null ? t.getDeviceName() : "", style);
                setCell(row, 2, t.getDeviceSerial() != null ? t.getDeviceSerial() : "", style);
                setCell(row, 3, t.getAction() != null ? t.getAction().name() : "", style);
                setCell(row, 4, t.getUserName() != null ? t.getUserName() : "", style);
                setCell(row, 5, t.getPerformedByName() != null ? t.getPerformedByName() : "", style);
                setCell(row, 6, t.getActionDate() != null ? t.getActionDate().format(FORMATTER) : "", style);
                setCell(row, 7, t.getExpectedReturnDate() != null ? t.getExpectedReturnDate().format(FORMATTER) : "", style);
                setCell(row, 8, t.getNotes() != null ? t.getNotes() : "", style);
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        }
    }

    public String getReportFilename(String type, String format) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "airtel_" + type + "_" + ts + "." + format;
    }

    // ── PDF helpers ──────────────────────────────────────

    private void addPdfHeader(Document doc, String title) throws DocumentException {
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        PdfPTable h = new PdfPTable(1);
        h.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase("AIRTEL RWANDA — " + title.toUpperCase(), f));
        cell.setBackgroundColor(AIRTEL_RED);
        cell.setPadding(12);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.addCell(cell);
        doc.add(h);
        doc.add(Chunk.NEWLINE);
    }

    private void addPdfTimestamp(Document doc) throws DocumentException {
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY);
        doc.add(new Paragraph("Generated: " + LocalDateTime.now().format(FORMATTER), f));
        doc.add(Chunk.NEWLINE);
    }

    private void addPdfFooter(Document doc, String summary) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);
        doc.add(new Paragraph(summary + " | Airtel Rwanda Inventory System", f));
    }

    private void addHeaderCell(PdfPTable table, String text) {
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setBackgroundColor(AIRTEL_RED);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, BaseColor bg) {
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", f));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        table.addCell(cell);
    }

    // ── Excel helpers ────────────────────────────────────

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createAltStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row row = sheet.createRow(0);
        row.setHeight((short) 400);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        if (style != null) cell.setCellStyle(style);
    }
}