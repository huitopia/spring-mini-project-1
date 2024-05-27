USE prj01;

# Board Table
CREATE TABLE board
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    title    VARCHAR(100)  NOT NULL,
    content  VARCHAR(1000) NOT NULL,
    writer   VARCHAR(100)  NOT NULL,
    inserted DATETIME      NOT NULL DEFAULT NOW()
);

SELECT *
FROM board
ORDER BY id DESC;

# member table 만들기
CREATE TABLE member
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    nick_name VARCHAR(100) NOT NULL UNIQUE,
    inserted  DATETIME     NOT NULL DEFAULT NOW()
);

SELECT *
FROM member;

# board table 수정
# writer column 지우기
# member_id column references member(id)
ALTER TABLE board
    DROP COLUMN writer;

ALTER TABLE board
    ADD COLUMN member_id INT REFERENCES member (id) AFTER content;

UPDATE board
SET member_id = (SELECT id FROM member ORDER BY id DESC LIMIT 1)
WHERE id > 0;

# NOT NULL 제약사항 추가
ALTER TABLE board
    MODIFY COLUMN member_id INT NOT NULL;

DESC board;

SELECT *
FROM board;

# 권한 테이블
CREATE TABLE authority
(
    member_id INT         NOT NULL REFERENCES member (id),
    name      VARCHAR(20) NOT NULL,
    PRIMARY KEY (member_id, name)
);

INSERT INTO authority (member_id, name)
VALUES (1, 'admin');

SELECT *
FROM authority;

USE prj01;
# 게시물 여러개 입력
INSERT INTO board
    (title, content, member_id)
SELECT title, content, member_id
FROM board;

SELECT COUNT(*)
FROM board;

SELECT *
FROM member;

UPDATE member
SET nick_name = 'abcd'
WHERE id = 4;
UPDATE member
SET nick_name = 'efgh'
WHERE id = 3;

UPDATE board
SET member_id = 4
WHERE id % 2 = 0;

UPDATE board
SET member_id = 3
WHERE id % 2 = 1;

UPDATE board
SET title   = 'abc def',
    content = 'ghi jkl'
WHERE id % 3 = 0;
UPDATE board
SET title   = 'mno pqr',
    content = 'stu vwx'
WHERE id % 3 = 1;
UPDATE board
SET title   = 'yz1 234',
    content = '567 890'
WHERE id % 3 = 2;