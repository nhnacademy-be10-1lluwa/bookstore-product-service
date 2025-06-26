-- 최상위
INSERT INTO categories (category_name, parent_id) VALUES ('소설', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('에세이', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('자기계발', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('경제/경영', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('인문학', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('사회과학', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('과학', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('IT/컴퓨터', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('예술/대중문화', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('역사', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('아동/청소년', NULL);
INSERT INTO categories (category_name, parent_id) VALUES ('외국어', NULL);

-- 소설 하위
INSERT INTO categories (category_name, parent_id) VALUES ('한국소설', 1);
INSERT INTO categories (category_name, parent_id) VALUES ('해외소설', 1);
INSERT INTO categories (category_name, parent_id) VALUES ('장르소설', 1);

-- 에세이 하위
INSERT INTO categories (category_name, parent_id) VALUES ('일상에세이', 2);
INSERT INTO categories (category_name, parent_id) VALUES ('교양에세이', 2);

-- 자기계발 하위
INSERT INTO categories (category_name, parent_id) VALUES ('성공학', 3);
INSERT INTO categories (category_name, parent_id) VALUES ('시간관리', 3);

-- 경제/경영 하위
INSERT INTO categories (category_name, parent_id) VALUES ('재테크', 4);
INSERT INTO categories (category_name, parent_id) VALUES ('경영전략', 4);
INSERT INTO categories (category_name, parent_id) VALUES ('마케팅', 4);

-- 인문학 하위
INSERT INTO categories (category_name, parent_id) VALUES ('철학', 5);
INSERT INTO categories (category_name, parent_id) VALUES ('종교', 5);
INSERT INTO categories (category_name, parent_id) VALUES ('문학', 5);

-- 사회과학 하위
INSERT INTO categories (category_name, parent_id) VALUES ('정치', 6);
INSERT INTO categories (category_name, parent_id) VALUES ('사회학', 6);

-- 과학 하위
INSERT INTO categories (category_name, parent_id) VALUES ('물리학', 7);
INSERT INTO categories (category_name, parent_id) VALUES ('생명과학', 7);

-- IT/컴퓨터 하위
INSERT INTO categories (category_name, parent_id) VALUES ('프로그래밍', 8);
INSERT INTO categories (category_name, parent_id) VALUES ('데이터베이스', 8);
INSERT INTO categories (category_name, parent_id) VALUES ('인공지능', 8);

-- 예술/대중문화 하위
INSERT INTO categories (category_name, parent_id) VALUES ('음악', 9);
INSERT INTO categories (category_name, parent_id) VALUES ('영화', 9);
INSERT INTO categories (category_name, parent_id) VALUES ('디자인', 9);

-- 역사 하위
INSERT INTO categories (category_name, parent_id) VALUES ('한국사', 10);
INSERT INTO categories (category_name, parent_id) VALUES ('세계사', 10);

-- 아동/청소년 하위
INSERT INTO categories (category_name, parent_id) VALUES ('동화', 11);
INSERT INTO categories (category_name, parent_id) VALUES ('청소년소설', 11);

-- 외국어 하위
INSERT INTO categories (category_name, parent_id) VALUES ('영어', 12);
INSERT INTO categories (category_name, parent_id) VALUES ('일본어', 12);
INSERT INTO categories (category_name, parent_id) VALUES ('중국어', 12);