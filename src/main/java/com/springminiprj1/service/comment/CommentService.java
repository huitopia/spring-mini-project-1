package com.springminiprj1.service.comment;

import com.springminiprj1.domain.comment.Comment;
import com.springminiprj1.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CommentService {
    final CommentMapper mapper;

    public void add(Comment comment, Authentication authentication) {
        comment.setMemberId(Integer.valueOf(authentication.getName()));
        mapper.insert(comment);
    }

    public List<Comment> selectCommentByBoardId(Integer boardId) {
        return mapper.selectCommentByBoardId(boardId);
    }

    public boolean validate(Comment comment) {
        if (comment == null) {
            return false;
        }
        if (comment.getComment().isBlank()) {
            return false;
        }
        if (comment.getBoardId() == null) {
            return false;
        }
        return true;
    }
}
