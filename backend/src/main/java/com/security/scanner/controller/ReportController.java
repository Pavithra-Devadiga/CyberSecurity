package com.security.scanner.controller;

import com.security.scanner.dto.request.ReportGenerateRequest;
import com.security.scanner.dto.response.ApiResponse;
import com.security.scanner.dto.response.ReportResponseDto;
import com.security.scanner.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ReportResponseDto>> generateReportSummary(
            @Valid @RequestBody ReportGenerateRequest request
    ) {
        ReportResponseDto response = reportService.generateReportSummary(request);
        return ResponseEntity.ok(ApiResponse.success("Report generated successfully", response));
    }

    @GetMapping("/download/{scanId}")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long scanId) {
        byte[] pdfBytes = reportService.generatePdfReport(scanId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "security-audit-report-scan-" + scanId + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
