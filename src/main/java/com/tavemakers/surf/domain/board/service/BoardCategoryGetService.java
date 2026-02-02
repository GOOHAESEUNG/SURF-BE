package com.tavemakers.surf.domain.board.service;

import com.tavemakers.surf.domain.board.entity.BoardCategory;
import com.tavemakers.surf.domain.board.exception.CategoryNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCategoryGetService {
    private final BoardCategoryRepository boardCategoryRepository;

    /** BoardCategory 엔티티 조회, 없으면 CategoryNotFoundException */
    public BoardCategory getCategory(Long id) {
        return boardCategoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    /** Board ID와 slug로 BoardCategory 엔티티 조회, 없으면 CategoryNotFoundException */
    public BoardCategory getCategoryByBoardAndSlug(Long boardId, String slug) {
        return boardCategoryRepository.findByBoardIdAndSlug(boardId, slug)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
