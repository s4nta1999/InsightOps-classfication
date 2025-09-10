-- VOC 정규화 테이블 생성 (MySQL) - voc_raw 구조에 맞춤
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
    category_id VARCHAR(8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    analysis_result JSON NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_source_id ON voc_normalized(source_id);
CREATE INDEX idx_consulting_date ON voc_normalized(consulting_date);
CREATE INDEX idx_consulting_category ON voc_normalized(consulting_category);
CREATE INDEX idx_created_at ON voc_normalized(created_at);
CREATE INDEX idx_client_info ON voc_normalized(client_gender, client_age);
CREATE INDEX idx_category_id ON voc_normalized(category_id);

-- MySQL JSON 함수 기반 인덱스 (MySQL 8.0+)
CREATE INDEX idx_category_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.category')) AS CHAR(100)))
);
CREATE INDEX idx_confidence_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.confidence')) AS DECIMAL(3,2)))
);

-- 테스트 데이터 삽입 (voc_raw 구조에 맞춤)
INSERT INTO voc_normalized (
    source_id, consulting_date, client_gender, client_age, consulting_turns, consulting_length,
    consulting_content, processing_time, consulting_category, category_id, analysis_result
) VALUES (
    'test001', '2025-09-09 14:30:00', '여자', 30, 15, 120,
    '안녕하세요. 카드 분실 신고를 하고 싶습니다.',
    4.932, '도난/분실 신청/해제', '550e8400',
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
