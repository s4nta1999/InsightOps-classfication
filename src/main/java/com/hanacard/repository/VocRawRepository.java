package com.hanacard.repository;

import com.hanacard.entity.VocRaw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VoC Raw 데이터 Repository
 */
@Repository
public interface VocRawRepository extends JpaRepository<VocRaw, Long> {
    
    // 미처리 데이터 조회
    List<VocRaw> findByProcessedFalse();
    
    // 미처리 데이터 페이지네이션 조회
    Page<VocRaw> findByProcessedFalse(Pageable pageable);
    
    // 특정 날짜 범위의 미처리 데이터 조회
    List<VocRaw> findByProcessedFalseAndConsultingDateBetween(
        LocalDateTime startDate, LocalDateTime endDate);
    
    // 처리된 데이터 조회
    List<VocRaw> findByProcessedTrue();
    
    // 전체 데이터 수 조회
    @Query("SELECT COUNT(v) FROM VocRaw v")
    long countAll();
    
    // 미처리 데이터 수 조회
    @Query("SELECT COUNT(v) FROM VocRaw v WHERE v.processed = false")
    long countUnprocessed();
    
    // 처리된 데이터 수 조회
    @Query("SELECT COUNT(v) FROM VocRaw v WHERE v.processed = true")
    long countProcessed();
    
    // 특정 source_id로 조회
    List<VocRaw> findBySourceId(String sourceId);
    
    // 최근 처리된 데이터 조회
    @Query("SELECT v FROM VocRaw v WHERE v.processed = true ORDER BY v.processedAt DESC")
    List<VocRaw> findRecentlyProcessed(Pageable pageable);
    
    // 처리 통계 조회
    @Query("SELECT " +
           "COUNT(CASE WHEN v.processed = true THEN 1 END) as processedCount, " +
           "COUNT(CASE WHEN v.processed = false THEN 1 END) as unprocessedCount, " +
           "COUNT(v) as totalCount " +
           "FROM VocRaw v")
    Object[] getProcessingStatistics();
}
