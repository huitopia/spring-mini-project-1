package com.springminiprj1.controller.board;

import com.springminiprj1.domain.board.Board;
import com.springminiprj1.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Board board) {
        if (service.validate(board)) {
            service.add(board);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("list")
    public List<Board> list() {
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
    public void delete(@PathVariable Integer id) {
        service.deleteBoardById(id);
    }
}
