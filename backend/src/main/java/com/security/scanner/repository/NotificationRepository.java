package com.security.scanner.repository;

import com.security.scanner.entity.Notification;
import com.security.scanner.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndIsReadFalse(User user);

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
}
