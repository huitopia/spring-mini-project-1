package com.springminiprj1.domain.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer; // nickName
    private Integer memberId;
    private LocalDateTime inserted;
}
