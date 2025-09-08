package com.hanacard.repository;

import com.hanacard.entity.ConsultingClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 상담 분류 결과 Repository
 * JSONB 기반의 고급 쿼리 메서드들 포함
 */
@Repository
public interface ConsultingClassificationRepository extends JpaRepository<ConsultingClassification, Long> {
    
    // 기본 조회 메서드들
    Optional<ConsultingClassification> findBySourceId(String sourceId);
    
    List<ConsultingClassification> findByConsultingDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<ConsultingClassification> findByConsultingDate(LocalDate date);
    
    // JSONB 기반 고급 쿼리들
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result->'classification'->>'category' = :category", nativeQuery = true)
    List<ConsultingClassification> findByCategory(@Param("category") String category);
    
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE (analysis_result->'classification'->>'confidence')::numeric > :minConfidence", nativeQuery = true)
    List<ConsultingClassification> findByConfidenceGreaterThan(@Param("minConfidence") Double minConfidence);
    
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result->'analysis'->>'urgency_level' = :urgencyLevel", nativeQuery = true)
    List<ConsultingClassification> findByUrgencyLevel(@Param("urgencyLevel") String urgencyLevel);
    
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE (analysis_result->'analysis'->>'priority_score')::numeric > :minPriority", nativeQuery = true)
    List<ConsultingClassification> findByPriorityGreaterThan(@Param("minPriority") Double minPriority);
    
    // 복합 조건 검색
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result->'classification'->>'category' = :category " +
                   "AND (analysis_result->'analysis'->>'priority_score')::numeric > :minPriority", nativeQuery = true)
    List<ConsultingClassification> findByCategoryAndPriority(
        @Param("category") String category, 
        @Param("minPriority") Double minPriority);
    
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result->'analysis'->>'urgency_level' = :urgencyLevel " +
                   "AND (analysis_result->'classification'->>'confidence')::numeric > :minConfidence", nativeQuery = true)
    List<ConsultingClassification> findByUrgencyAndConfidence(
        @Param("urgencyLevel") String urgencyLevel, 
        @Param("minConfidence") Double minConfidence);
    
    // 통계 쿼리들
    @Query(value = "SELECT " +
                   "analysis_result->'classification'->>'category' as category, " +
                   "COUNT(*) as count " +
                   "FROM voc_normalized " +
                   "GROUP BY analysis_result->'classification'->>'category' " +
                   "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> getCategoryStatistics();
    
    @Query(value = "SELECT " +
                   "analysis_result->'analysis'->>'urgency_level' as urgency_level, " +
                   "COUNT(*) as count " +
                   "FROM voc_normalized " +
                   "GROUP BY analysis_result->'analysis'->>'urgency_level'", nativeQuery = true)
    List<Object[]> getUrgencyLevelStatistics();
    
    @Query(value = "SELECT " +
                   "DATE(created_at) as date, " +
                   "COUNT(*) as count " +
                   "FROM voc_normalized " +
                   "WHERE created_at >= :startDate " +
                   "GROUP BY DATE(created_at) " +
                   "ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyProcessingCount(@Param("startDate") String startDate);
    
    // 검색 기능
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result::text LIKE CONCAT('%', :searchTerm, '%') " +
                   "OR consulting_content LIKE CONCAT('%', :searchTerm, '%')", nativeQuery = true)
    List<ConsultingClassification> searchByContent(@Param("searchTerm") String searchTerm);
    
    // 최근 처리된 상담들
    @Query(value = "SELECT * FROM voc_normalized " +
                   "ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ConsultingClassification> findRecentClassifications(@Param("limit") int limit);
    
    // 카테고리별 최근 상담
    @Query(value = "SELECT * FROM voc_normalized " +
                   "WHERE analysis_result->'classification'->>'category' = :category " +
                   "ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ConsultingClassification> findRecentByCategory(
        @Param("category") String category, 
        @Param("limit") int limit);
}
