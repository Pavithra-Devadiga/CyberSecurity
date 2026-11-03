package com.security.scanner.repository;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.User;
import com.security.scanner.entity.enums.ScanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {

    List<Scan> findByUserOrderByCreatedAtDesc(User user);

    Page<Scan> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<Scan> findByStatus(ScanStatus status);

    @Query("SELECT s FROM Scan s ORDER BY s.createdAt DESC")
    List<Scan> findTop10RecentScans(Pageable pageable);

    @Query("SELECT AVG(s.securityScore) FROM Scan s WHERE s.securityScore IS NOT NULL")
    Double calculateAverageSecurityScore();

    @Query("SELECT COUNT(s) FROM Scan s WHERE s.status = :status")
    long countByStatus(ScanStatus status);
}
