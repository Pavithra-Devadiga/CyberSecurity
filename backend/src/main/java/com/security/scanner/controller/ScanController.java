package com.security.scanner.controller;

import com.security.scanner.dto.request.ScanRequest;
import com.security.scanner.dto.response.ApiResponse;
import com.security.scanner.dto.response.ScanDetailResponse;
import com.security.scanner.dto.response.ScanResponse;
import com.security.scanner.service.ScanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scans")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScanResponse>> initiateScan(@Valid @RequestBody ScanRequest request) {
        ScanResponse response = scanService.initiateScan(request);
        return ResponseEntity.ok(ApiResponse.success("Scan initiated successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScanResponse>>> getUserScans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ScanResponse> scans = scanService.getUserScans(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(scans));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScanDetailResponse>> getScanDetails(@PathVariable Long id) {
        ScanDetailResponse details = scanService.getScanDetails(id);
        return ResponseEntity.ok(ApiResponse.success(details));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScanResponse>> cancelScan(@PathVariable Long id) {
        ScanResponse response = scanService.cancelScan(id);
        return ResponseEntity.ok(ApiResponse.success("Scan cancelled", response));
    }
}
