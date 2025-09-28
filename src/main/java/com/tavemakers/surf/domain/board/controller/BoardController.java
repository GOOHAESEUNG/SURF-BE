package com.tavemakers.surf.domain.board.controller;

import com.tavemakers.surf.domain.board.dto.req.BoardCreateReqDTO;
import com.tavemakers.surf.domain.board.dto.req.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.res.BoardResDTO;
import com.tavemakers.surf.domain.board.service.BoardService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.board.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "게시판", description = "추후 MVP를 통해 디벨롭 될 예정")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/v1/admin/boards/{boardId}")
    public ApiResponse<BoardResDTO> createBoard(
            @Valid @RequestBody BoardCreateReqDTO req) {
        BoardResDTO response = boardService.createBoard(req);
        return ApiResponse.response(HttpStatus.CREATED, BOARD_CREATED.getMessage(), response);
    }


    @Operation(summary = "게시판 조회", description = "특정 ID의 게시판을 조회합니다.")
    @GetMapping("/v1/admin/boards/{boardId}")
    public ApiResponse<BoardResDTO> getBoard(
            @PathVariable Long boardId) {
        BoardResDTO response = boardService.getBoard(boardId);
        return ApiResponse.response(HttpStatus.OK, BOARD_READ.getMessage(), response);
    }

    @Operation(summary = "게시판 수정", description = "특정 ID의 게시판을 수정합니다.")
    @PutMapping("/v1/admin/boards/{boardId}")
    public ApiResponse<BoardResDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateReqDTO req) {
        BoardResDTO response = boardService.updateBoard(boardId, req);
        return ApiResponse.response(HttpStatus.OK, BOARD_UPDATED.getMessage(), response);
    }

    @Operation(summary = "게시판 삭제", description = "특정 ID의 게시판을 삭제합니다.")
    @DeleteMapping("/v1/admin/boards/{boardId}")
    public ApiResponse<Void> deleteBoard(
            @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, BOARD_DELETED.getMessage());
    }
}
