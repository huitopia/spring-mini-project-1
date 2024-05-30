package com.springminiprj1.domain.board;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer; // nickName
    private Integer memberId;
    private LocalDateTime inserted;
    private Integer numberOfLike;
    private Integer numberOfImages;
    private List<BoardFile> fileList;
}
