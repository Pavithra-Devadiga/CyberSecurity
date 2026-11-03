package com.security.scanner.dto.response;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private Long scanId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getScanId() { return scanId; }
    public void setScanId(Long scanId) { this.scanId = scanId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
