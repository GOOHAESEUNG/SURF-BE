package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.member.dto.response.MemberLikeListResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.post.dto.res.PostLikeListResDTO;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeGetService {
    private final PostLikeRepository postLikeRepository;

    public PostLikeListResDTO getPostLikes(Long postId) {
        List<MemberLikeListResDTO> likes = postLikeRepository.findLikedMembersByPostId(postId)
                .stream()
                .map(MemberLikeListResDTO::from)
                .toList();

        return new PostLikeListResDTO(likes);
    }
}
