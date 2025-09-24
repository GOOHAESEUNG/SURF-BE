package com.tavemakers.surf.domain.board.controller;

import com.tavemakers.surf.domain.board.dto.req.BoardCreateReqDTO;
import com.tavemakers.surf.domain.board.dto.req.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.res.BoardResDTO;
import com.tavemakers.surf.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/manager/boards")
@Tag(name = "게시판", description = "추후 MVP를 통해 디벨롭 될 예정")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResDTO> createBoard(
            @Valid @RequestBody BoardCreateReqDTO req) {
        return ResponseEntity.ok(boardService.createBoard(req));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> getBoard(
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateReqDTO req) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, req));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
