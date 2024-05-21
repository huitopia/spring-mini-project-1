package com.springminiprj1.mapper.board;

import com.springminiprj1.domain.board.Board;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board (title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    int add(Board board);

    @Select("""
            SELECT id, title, writer
            FROM board
            ORDER BY id DESC
            """)
    List<Board> selectAll();
}
