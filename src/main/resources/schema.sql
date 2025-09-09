-- VOC 정규화 테이블 생성 (MySQL) - normalization_db
CREATE TABLE IF NOT EXISTS voc_normalized (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id VARCHAR(100) NOT NULL,
    source VARCHAR(50),
    consulting_date DATE,
    consulting_time TIME,
    consulting_content TEXT NOT NULL,
    processing_time DOUBLE,
    client_gender VARCHAR(10),
    client_age VARCHAR(10),
    consulting_turns INTEGER,
    consulting_length INTEGER,
    consulting_category VARCHAR(100),
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

-- MySQL JSON 함수 기반 인덱스 (MySQL 8.0+)
CREATE INDEX idx_category_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.category')) AS CHAR(100)))
);
CREATE INDEX idx_confidence_json ON voc_normalized(
    (CAST(JSON_UNQUOTE(JSON_EXTRACT(analysis_result, '$.classification.confidence')) AS DECIMAL(3,2)))
);
