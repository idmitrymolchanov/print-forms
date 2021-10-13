package com.example.printforms.controller;

import com.example.printforms.service.reports.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/generate/id/{id}/format/{format}/data/{data}")
    public byte[] getReport(@PathVariable("id") String reportId,
                            @PathVariable("format") String reportFormat,
                            @PathVariable("data") byte[] reportData) {

        System.out.println(reportId + " " + reportFormat);
        byte[] decoded = Base64.getDecoder().decode(reportData);

        //JSONObject jsonObject = new JSONObject(new String(decoded));
        try {
            //String reportDataDecoded = jsonObject.toString();
            //System.out.println(reportDataDecoded);
            return service.getReportInBase64(reportId, reportFormat, new String(decoded));
        }
        catch (Exception ignored) {

        }
        log.error("error");
        return null;
    }

}