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
    @Options(useGeneratedKeys = true, keyProperty = "id")
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

    @Select("""
            <script>
            SELECT b.id, 
                   b.title,
                   m.nick_name writer,
                   COUNT(f.name) number_of_images
            FROM board b JOIN member m ON b.member_id = m.id
                         LEFT JOIN board_file f ON b.id = f.board_id
               <trim prefix="WHERE" prefixOverrides="OR">
                   <if test="searchType != null">
                       <bind name="pattern" value="'%' + searchKeyword + '%'" />
                       <if test="searchType == 'all' || searchType == 'text'">
                           OR b.title LIKE #{pattern}
                           OR b.content LIKE #{pattern}
                       </if>
                       <if test="searchType == 'all' || searchType == 'nickName'">
                           OR m.nick_name LIKE #{pattern}
                       </if>
                   </if>
               </trim>
            GROUP BY b.id
            ORDER BY b.id DESC
            LIMIT #{offset}, 10
            </script>
            """)
    List<Board> selectAllPaging(Integer offset, String searchType, String searchKeyword);

    @Select("""
            SELECT COUNT(*)
            FROM board;
            """)
    Integer countAll();

    @Select("""
            <script>
            SELECT COUNT(b.id)
            FROM board b JOIN member m ON b.member_id = m.id
            <trim prefix="WHERE" prefixOverrides="OR">
                <if test="searchType != null">
                    <bind name="pattern" value="'%' + searchKeyword + '%'" />
                    <if test="searchType == 'all' || searchType == 'text'">
                        OR b.title LIKE #{pattern}
                        OR b.content LIKE #{pattern}
                    </if>
                    <if test="searchType == 'all' || searchType == 'nickName'">
                        OR m.nick_name LIKE #{pattern}
                    </if>
                </if>
            </trim>
            </script>
            """)
    Integer countAllwithSearch(String searchType, String searchKeyword);

    @Insert("""
            INSERT INTO board_file (board_id, name)
            VALUES (#{boardId}, #{name})
            """)
    int addFileName(Integer boardId, String name);

    @Select("""
            SELECT name
            FROM board_file
            WHERE board_id=#{boardId}
            """)
    List<String> selectFileNameByBoardId(Integer boardId);

    @Delete("""
            DELETE FROM board_file
            WHERE board_id=#{boardId}
            """)
    int deleteFileByBoardId(Integer boardId);
}
