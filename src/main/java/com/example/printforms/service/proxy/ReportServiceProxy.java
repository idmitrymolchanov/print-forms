package com.example.printforms.service.proxy;

import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="document-service", url="${document-service.ribbon.listOfServers}")
public interface ReportServiceProxy {
    @GetMapping("/report/data/id/{id}")
    public JSONObject getReportDataById
            (@PathVariable("id") String id);

    @GetMapping("/report/data")
    public JSONObject getAllReportData();
}