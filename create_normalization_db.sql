-- ============================================
-- 하나카드 상담 분류 마이크로서비스 데이터베이스
-- 데이터베이스명: normalization
-- 테이블명: voc_normalized
-- ============================================

-- 1. 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS normalization
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE normalization;

-- 2. 테이블 생성 (voc_raw 구조에 맞춤)
CREATE TABLE IF NOT EXISTS voc_normalized (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id VARCHAR(255) NOT NULL,
    consulting_date DATETIME NOT NULL,
    client_gender VARCHAR(10) NOT NULL,
    client_age INT NOT NULL,
    consulting_turns INT NOT NULL,
    consulting_length INT NOT NULL,
    consulting_content TEXT NOT NULL,
    processing_time DOUBLE,
    consulting_category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    analysis_result JSON NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 인덱스 생성
CREATE INDEX idx_source_id ON voc_normalized(source_id);
CREATE INDEX idx_consulting_date ON voc_normalized(consulting_date);
CREATE INDEX idx_consulting_category ON voc_normalized(consulting_category);
CREATE INDEX idx_created_at ON voc_normalized(created_at);
CREATE INDEX idx_client_info ON voc_normalized(client_gender, client_age);

-- 4. JSON 인덱스 (MySQL 8.0+)
CREATE INDEX idx_category_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.category')) AS CHAR(100)))
);
CREATE INDEX idx_confidence_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.confidence')) AS DECIMAL(3,2)))
);

-- 5. 테스트 데이터 삽입 (voc_raw 구조에 맞춤)
INSERT INTO voc_normalized (
    source_id, consulting_date, client_gender, client_age, consulting_turns, consulting_length,
    consulting_content, processing_time, consulting_category, analysis_result
) VALUES (
    'test001', '2025-09-09 14:30:00', '여자', 30, 15, 120,
    '안녕하세요. 카드 분실 신고를 하고 싶습니다.',
    4.932, '도난/분실 신청/해제',
    JSON_OBJECT(
        'classification', JSON_OBJECT(
            'category', '도난/분실 신청/해제',
            'confidence', 0.95,
            'alternative_categories', JSON_ARRAY()
        ),
        'analysis', JSON_OBJECT(
            'problem_situation', '고객이 카드 분실 신고를 원하고 있습니다.',
            'solution_approach', '고객에게 카드 분실 신고 절차를 안내하고, 필요한 정보를 수집하여 신고를 진행합니다.',
            'expected_outcome', '신고가 완료되면 카드가 즉시 정지되고, 새로운 카드 발급 절차가 시작됩니다.'
        )
    )
);

-- 6. 확인
SELECT 'normalization 데이터베이스 생성 완료!' as message;
SELECT COUNT(*) as total_records FROM voc_normalized;

-- 7. 테이블 구조 확인
DESCRIBE voc_normalized;

-- 8. 샘플 데이터 확인
SELECT 
    id,
    source_id,
    consulting_date,
    client_gender,
    client_age,
    consulting_turns,
    consulting_length,
    consulting_category,
    processing_time,
    created_at
FROM voc_normalized 
LIMIT 5;
