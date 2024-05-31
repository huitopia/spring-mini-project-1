package com.springminiprj1.domain.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Comment {
    private Integer id;
    private Integer boardId;
    private Integer memberId;
    private String nickName;
    private String comment;
    private LocalDateTime inserted;

    public String getInserted() {
        LocalDateTime beforeOneDay = LocalDateTime.now().minusDays(1);

        if (inserted.isBefore(beforeOneDay)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return inserted.format(formatter).toString();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return inserted.format(formatter).toString();
        }
    }
}
