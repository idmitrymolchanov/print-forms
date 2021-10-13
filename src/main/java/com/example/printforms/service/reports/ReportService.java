package com.example.printforms.service.reports;

import com.example.printforms.model.Item;
import com.example.printforms.service.error.PrintedFormsException;

import java.util.List;

public interface ReportService {
    byte[] getReport(String reportId, ReportFormat format, List<Item> reportData) throws PrintedFormsException;
    String getReportId(String reportId, ReportFormat format, List<Item> reportData) throws PrintedFormsException;
}
