package com.example.booklibrary.report.service.impl;

import com.example.booklibrary.book.service.BookService;
import com.example.booklibrary.book.service.InventoryService;
import com.example.booklibrary.purchase.service.PurchaseService;
import com.example.booklibrary.report.service.ReportService;
import com.example.booklibrary.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final BookService bookService;
    private final InventoryService inventoryService;
    private final PurchaseService purchaseService;
    private final UserService userService;

    @Override
    public ByteArrayResource generateGeneralReport() {
        try {
            log.info("Generating general report");

            try (InputStream jrxmlInput = this.getClass().getResourceAsStream("/report-general.jrxml")) {
                JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

                Map<String, Object> params = new HashMap<>();
                params.put("BOOK_COUNT", bookService.getAllBooksCount());
                params.put("BOOK_INVENTORY_COUNT", inventoryService.getTotalInventoryBooksCount());
                params.put("PURCHASE_COUNT", purchaseService.getAllPurchasesCount());
                params.put("PURCHASED_BOOK_COUNT", purchaseService.getTotalPurchasedBooksCount());
                params.put("USER_COUNT", userService.getAllUsersCount());

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
                byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);

                return new ByteArrayResource(report);
            }
        } catch (Exception ex) {
            log.error("Error generating report", ex);
            throw new ResponseStatusException(BAD_REQUEST, "Failed to generate the report.", ex);
        }
    }
}

