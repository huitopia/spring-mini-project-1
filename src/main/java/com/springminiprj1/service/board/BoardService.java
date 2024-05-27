package com.springminiprj1.service.board;

import com.springminiprj1.domain.board.Board;
import com.springminiprj1.mapper.board.BoardMapper;
import com.springminiprj1.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;
    private final MemberMapper memberMapper;

    public void add(Board board, Authentication authentication) {
        board.setMemberId(Integer.valueOf(authentication.getName()));
        mapper.add(board);
    }

    public boolean validate(Board board) {
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }
        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }
        return true;
    }

    public List<Board> list(Integer page) {
        Integer offset = (page - 1) * 10;
        return mapper.selectAllPaging(offset);
    }

    public Board selectBoardById(Integer id) {
        return mapper.selectBoardById(id);
    }

    public void deleteBoardById(Integer id) {
        mapper.deleteBoardById(id);
    }

    public void updateBoard(Board board) {
        mapper.updateBoard(board);
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        Board board = mapper.selectBoardById(id);

        return board.getMemberId()
                .equals(Integer.valueOf(authentication.getName()));
    }
}
