package com.example.booklibrary.report.service;

import org.springframework.core.io.ByteArrayResource;

public interface ReportService {
    ByteArrayResource generateGeneralReport();
}
