package com.example.printforms.service.reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
@Service
public class ReportService {

    public byte[] getReportInBase64(String id, String format, String reportData) throws FileNotFoundException, JRException {
        log.info("in getreport");
        JasperReport jasperReport = getReportById(id);
        JasperPrint jasperPrint = getJasperPrint(reportData, jasperReport);
        byte[] report = exportReportToByteArray(jasperPrint, format);
        return Base64.getEncoder().encode(report);
    }

    private byte[] exportReportToByteArray(final JasperPrint print, String format) throws JRException {
        final Exporter exporter;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean html = false;

        switch (format) {
            case "html":
                exporter = new HtmlExporter();
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
                html = true;
                break;
            case "pdf":
                exporter = new JRPdfExporter();
                break;

            default:
                throw new JRException("Unknown report format: " + format.toString());
        }

        if (!html) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        }

        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    private JasperReport getReportById(String id) throws FileNotFoundException, JRException {
        File fileReport = ResourceUtils.getFile("classpath:" + id + ".jrxml");

        return JasperCompileManager.compileReport(fileReport.getAbsolutePath());
    }

    private JasperPrint getJasperPrint(String reportData, JasperReport report) throws JRException {
        log.info("in get  " + reportData);
        //Convert json string to byte array.
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(reportData.toString().getBytes());
        //Create json datasource from json stream
        JsonDataSource dataSource = new JsonDataSource(jsonDataStream);


        ///
        log.info("ALLLLLLL OKAAAAAAAAY");
        String rawJsonData = "[{\"id\":1, \"name\":\"Jerry\", \"surname\":\"Jesus\", \"age\":18, \"human\":true}]";
        ByteArrayInputStream jsonDataStream2 = new ByteArrayInputStream(rawJsonData.toString().getBytes());
        JsonDataSource dataSource2 = new JsonDataSource(jsonDataStream2);
        ///

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Techie");
        parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));

        return JasperFillManager.fillReport(report, parameters, dataSource);
    }
}
