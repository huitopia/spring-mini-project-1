USE prj01;

# Board Table
CREATE TABLE board
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    title   VARCHAR(100)  NOT NULL,
    content VARCHAR(1000) NOT NULL,
    writer  VARCHAR(100)  NOT NULL,
    inseted DATETIME      NOT NULL DEFAULT NOW()
);