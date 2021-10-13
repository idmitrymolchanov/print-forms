package com.example.printforms.controller;

import com.example.printforms.model.Item;
import com.example.printforms.service.error.PrintedFormsException;
import com.example.printforms.service.reports.ReportFormat;
import com.example.printforms.service.reports.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping(value = "/reports/{reportId}/generate", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<byte[]> getReport(@PathVariable("reportId") String reportId,
                                                         @RequestParam() ReportFormat format,
                                                         @RequestBody List<Item> items) throws PrintedFormsException
    {
        byte[] reportBase64;
        reportBase64 = service.getReport(reportId, format, items);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition",
                    "inline; filename="+reportId+"."+format.name().toLowerCase(Locale.ROOT)+"");
        headers.add("Content-Type", "text/plain");;

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(reportBase64);
    }

    @PostMapping(value = "/reports/{reportId}/generateId", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getReportId(@PathVariable("reportId") String reportId,
                                              @RequestParam() ReportFormat format,
                                              @RequestBody List<Item> items) throws PrintedFormsException
    {

        String createdReportId = service.getReportId(reportId, format, items);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(createdReportId);
    }
}
