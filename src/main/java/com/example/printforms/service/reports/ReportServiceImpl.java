package com.example.printforms.service.reports;

import com.example.printforms.model.Item;

import com.example.printforms.service.error.JsonConvertingException;
import com.example.printforms.service.error.PrintedFormsException;
import com.example.printforms.service.storage.StorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StorageService storageService;

    @Autowired
    public ReportServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public byte[] getReport(String reportId, ReportFormat format, List<Item> items) throws PrintedFormsException {
        try {
            byte[] report = generateReport(reportId, format, items);
            storageService.save(report);
            return report;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
return null;
        }
    }

    @Override
    public String getReportId(String reportId, ReportFormat format, List<Item> items) throws PrintedFormsException {
        try {
            return storageService.save(generateReport(reportId, format, items));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private byte[] generateReport(String id, ReportFormat format, List<Item> reportData) throws JsonProcessingException, PrintedFormsException{

            String reportDataString = objectMapper
                    .writeValueAsString(reportData
                            .stream()
                            .collect(Collectors
                                    .toMap(Item::getKey, Item::getValue)));


        byte[] report = null;
        try {
            JasperReport jasperReport = getReportFormById(id);
            JasperPrint jasperPrint = getJasperPrint(reportDataString, jasperReport);
            report = exportReportToByteArray(jasperPrint, format);
        } catch (JRException | JsonConvertingException e) {
            log.error(e.getMessage());
        }
        return Base64.getEncoder().encode(report);
    }

    private JasperReport getReportFormById(String id) throws JRException, JsonConvertingException, PrintedFormsException {
        try {
            File fileReport = ResourceUtils.getFile("classpath:" + id + ".jrxml");
            return JasperCompileManager.compileReport(fileReport.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new PrintedFormsException("message", e.getClass().getName());
        }
    }

    private JasperPrint getJasperPrint(String reportData, JasperReport report) throws PrintedFormsException {
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(reportData.getBytes());
        JsonDataSource dataSource;
        try {
            dataSource = new JsonDataSource(jsonDataStream);
        } catch (JRException e) {
            throw new PrintedFormsException("Json datasource not found", e.getClass().getName());
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Techie");
        parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

        try {
            return JasperFillManager.fillReport(report, parameters, dataSource);
        } catch (JRException e) {
            throw new PrintedFormsException("JasperFillManager fill error", e.getClass().getName());
        }
    }

    private byte[] exportReportToByteArray(final JasperPrint print, ReportFormat format) throws PrintedFormsException {
        final Exporter exporter;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        switch (format) {
            case HTML:
                exporter = new HtmlExporter();
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
                break;
            case PDF:
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                break;
            case CSV:
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                break;

            default:
                throw new PrintedFormsException("Unknown report format: " + format, "JRException");
        }

        try {
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.exportReport();
        } catch (JRException e) {
            throw new PrintedFormsException("Error with export report", "JRException");
        }

        return outputStream.toByteArray();
    }
}
