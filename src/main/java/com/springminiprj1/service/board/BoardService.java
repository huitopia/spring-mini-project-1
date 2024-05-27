package com.springminiprj1.service.board;

import com.springminiprj1.domain.board.Board;
import com.springminiprj1.mapper.board.BoardMapper;
import com.springminiprj1.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> list(Integer page, String searchType, String searchKeyword) {
        Map pageInfo = new HashMap();
        Integer countAll = mapper.countAllwithSearch(searchType, searchKeyword);

        Integer offset = (page - 1) * 10;
        Integer lastPageNumber = (countAll - 1) / 10 + 1;
        Integer leftPageNumber = (page - 1) / 10 * 10 + 1;
        Integer rightPageNumber = leftPageNumber + 9;

        rightPageNumber = Math.min(rightPageNumber, lastPageNumber);

        Integer prevPageNumber = (leftPageNumber - 1);
        Integer nextPageNumber = (rightPageNumber + 1);

        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }

        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        pageInfo.put("currentPageNumber", page);
        pageInfo.put("lastPageNumber", lastPageNumber);
        pageInfo.put("leftPageNumber", leftPageNumber);
        pageInfo.put("rightPageNumber", rightPageNumber);

        return Map.of(
                "pageInfo", pageInfo,
                "boardList", mapper.selectAllPaging(offset, searchType, searchKeyword)
        );
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
