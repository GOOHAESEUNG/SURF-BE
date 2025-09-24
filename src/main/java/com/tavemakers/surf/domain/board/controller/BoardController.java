package com.tavemakers.surf.domain.board.controller;

import com.tavemakers.surf.domain.board.dto.req.BoardCreateReqDTO;
import com.tavemakers.surf.domain.board.dto.req.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.res.BoardResDTO;
import com.tavemakers.surf.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/manager/boards")
@Tag(name = "게시판 관리", description = "추후 MVP를 통해 디벨롭 될 예정")
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시판 생성", description = "새로운 게시판을 생성합니다.")
    @PostMapping
    public ResponseEntity<BoardResDTO> createBoard(
            @Valid @RequestBody BoardCreateReqDTO req) {
        return ResponseEntity.ok(boardService.createBoard(req));
    }

    @Operation(summary = "게시판 조회", description = "특정 ID의 게시판을 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> getBoard(
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @Operation(summary = "게시판 수정", description = "특정 ID의 게시판을 수정합니다.")
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateReqDTO req) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, req));
    }

    @Operation(summary = "게시판 삭제", description = "특정 ID의 게시판을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
