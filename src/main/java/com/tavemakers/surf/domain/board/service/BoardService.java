package com.tavemakers.surf.domain.board.service;

import com.tavemakers.surf.domain.board.dto.req.BoardCreateReqDTO;
import com.tavemakers.surf.domain.board.dto.req.BoardUpdateReqDTO;
import com.tavemakers.surf.domain.board.dto.res.BoardResDTO;
import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.global.logging.LogEvent;
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
    @LogEvent("board.create")
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
    @LogEvent("board.update")
    public BoardResDTO updateBoard(Long id, BoardUpdateReqDTO req) {
        Board board = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        board.update(req.name(), req.type());
        return BoardResDTO.from(board);
    }

    @Transactional
    @LogEvent("board.delete")
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) throw new BoardNotFoundException();
        boardRepository.deleteById(id);
    }
}