package com.security.scanner.service;

import com.security.scanner.dto.response.NotificationDto;
import com.security.scanner.entity.Notification;
import com.security.scanner.entity.User;
import com.security.scanner.exception.ResourceNotFoundException;
import com.security.scanner.repository.NotificationRepository;
import com.security.scanner.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Notification> page = notificationRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        List<NotificationDto> dtoList = page.stream().map(this::mapToDto).toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        if (currentUser == null) return 0;
        return notificationRepository.countByUserAndIsReadFalse(currentUser);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<Notification> unread = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(currentUser);
            for (Notification n : unread) {
                n.setRead(true);
            }
            notificationRepository.saveAll(unread);
        }
    }

    public NotificationDto mapToDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setScanId(n.getScanId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
