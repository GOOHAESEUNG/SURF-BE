package com.tavemakers.surf.domain.board.service;

import com.tavemakers.surf.domain.board.dto.request.BoardCreateReqDTO;
import com.tavemakers.surf.domain.board.dto.request.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.response.BoardResDTO;
import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    @LogEvent(value = "board.create", message = "게시판 생성 성공")
    public BoardResDTO createBoard(BoardCreateReqDTO req) {
        Board board = Board.of(req);
        Board saved = boardRepository.save(board);
        return BoardResDTO.from(saved);
    }

    public List<BoardResDTO> getBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream().map(BoardResDTO::from).toList();
    }

    public BoardResDTO getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return BoardResDTO.from(board);
    }

    @Transactional
    @LogEvent(value = "board.update", message = "게시판 수정 성공")
    public BoardResDTO updateBoard(
            @LogParam("board_id") Long id,
            BoardUpdateReqDTO req) {
        Board board = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        board.update(req.name(), req.type());
        return BoardResDTO.from(board);
    }

    @Transactional
    @LogEvent(value = "board.delete", message = "게시판 삭제 성공")
    public void deleteBoard(
            @LogParam("board_id") Long id) {
        if (!boardRepository.existsById(id)) throw new BoardNotFoundException();
        boardRepository.deleteById(id);
    }
}