package com.springminiprj1.controller.board;

import com.springminiprj1.domain.board.Board;
import com.springminiprj1.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;

    @PostMapping("add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity add(@RequestBody Board board, Authentication authentication) {
        if (service.validate(board)) {
            service.add(board, authentication);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("list")
    public List<Board> list(@RequestParam(defaultValue = "1") Integer page) {
        System.out.println("page = " + page);
        return service.list();
    }

    // api/board/:id
    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id) {
        Board board = service.selectBoardById(id);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(board);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity delete(@PathVariable Integer id, Authentication authentication) {
        if (service.hasAccess(id, authentication)) {
            service.deleteBoardById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity edit(@RequestBody Board board, Authentication authentication) {
        if (!service.hasAccess(board.getId(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (service.validate(board)) {
            service.updateBoard(board);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
