package com.tavemakers.surf.domain.board.controller;

import com.tavemakers.surf.domain.board.dto.request.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.response.BoardResDTO;
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
public class BoardPatchController {
    private final BoardService boardService;

    @Operation(summary = "게시판 수정", description = "특정 ID의 게시판을 수정합니다.")
    @PatchMapping("/v1/admin/boards/{boardId}")
    public ApiResponse<BoardResDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateReqDTO req) {
        BoardResDTO response = boardService.updateBoard(boardId, req);
        return ApiResponse.response(HttpStatus.OK, BOARD_UPDATED.getMessage(), response);
    }
}
