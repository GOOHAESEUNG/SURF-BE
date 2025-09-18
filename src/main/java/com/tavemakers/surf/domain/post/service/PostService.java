package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostResDTO create(PostCreateReqDTO req) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        Post post = new Post();
        set(post, board, req.title(), req.content(),
                req.pinned() != null ? req.pinned() : false,
                req.postedAt() != null ? req.postedAt() : LocalDateTime.now());

        Post saved = postRepository.save(post);
        return toRes(saved);
    }
}
