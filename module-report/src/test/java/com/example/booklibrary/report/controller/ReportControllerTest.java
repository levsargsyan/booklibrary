package com.example.booklibrary.report.controller;

import com.example.booklibrary.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ReportController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void testGenerateReport() throws Exception {
        byte[] mockReportData = "Dummy PDF content".getBytes();
        ByteArrayResource dummyReport = new ByteArrayResource(mockReportData);

        when(reportService.generateGeneralReport()).thenReturn(dummyReport);

        mockMvc.perform(get("/api/v1/reports/general"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=GeneralReport.pdf"))
                .andExpect(content().bytes(mockReportData));

        verify(reportService, times(1)).generateGeneralReport();
    }
}
