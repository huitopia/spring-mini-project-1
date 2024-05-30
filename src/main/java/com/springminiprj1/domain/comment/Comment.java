package com.springminiprj1.domain.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer boardId;
    private Integer memberId;
    private String nickName;
    private String comment;
    private LocalDateTime inserted;
}
