package com.springminiprj1.mapper.board;

import com.springminiprj1.domain.board.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board (title, content, member_id)
            VALUES (#{title}, #{content}, #{memberId})
            """)
    int add(Board board);

    @Select("""
            SELECT b.id, b.title, m.nick_name writer
            FROM board b JOIN member m ON b.member_id = m.id
            ORDER BY b.id DESC
            """)
    List<Board> selectAll();

    @Select("""
            SELECT b.id,
                   b.title,
                   b.content,
                   b.inserted,
                   m.nick_name writer,
                   b.member_id
            FROM board b JOIN member m ON b.member_id = m.id
            WHERE b.id = #{id}
            """)
    Board selectBoardById(Integer id);

    @Delete("""
            DELETE FROM board
            WHERE id = #{id}
            """)
    int deleteBoardById(Integer id);

    @Update("""
            UPDATE board
            SET title=#{title},
                content=#{content}
            WHERE id = #{id}
            """)
    int updateBoard(Board board);

    @Delete("""
            DELETE FROM board
            WHERE member_id=#{memberId}
            """)
    int deleteByMemberId(Integer memberId);
}
