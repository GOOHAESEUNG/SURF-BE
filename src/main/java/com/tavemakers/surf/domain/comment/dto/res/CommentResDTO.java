package com.tavemakers.surf.domain.comment.dto.res;

import com.tavemakers.surf.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "댓글 전체 응답 DTO")
public record CommentResDTO(
        @Schema(description = "댓글 ID", example = "15")
        Long id,

        @Schema(description = "게시글 ID", example = "3")
        Long postId,

        @Schema(description = "루트 댓글 ID (최상위 댓글)", example = "15")
        Long rootId,

        @Schema(description = "부모 댓글 ID (루트면 null)", example = "null")
        Long parentId,

        @Schema(description = "댓글 깊이 (0=루트, 그 외=대댓글)", example = "0")
        int depth,

        @Schema(description = "댓글 내용", example = "좋은 공지 감사합니다!")
        String content,

        @Schema(description = "작성자 ID", example = "7")
        Long memberId,      // 프론트에서 내가 쓴 댓글인지 구분할 때 사용

        @Schema(description = "작성자 닉네임", example = "민수")
        String nickname,

        @Schema(description = "작성자 프로필 이미지 URL", example = "https://.../profile.png")
        String profileImageUrl,

        @Schema(description = "좋아요 수", example = "0")
        Long likeCount,

        @Schema(description = "로그인한 사용자가 이 댓글에 좋아요를 눌렀는지 여부", example = "true")
        Boolean liked,

        @Schema(description = "작성일시")
        LocalDateTime createdAt,

        @Schema(description = "멘션 목록")
        List<MentionResDTO> mentions

) {
    public static CommentResDTO from(Comment comment,
                                     List<MentionResDTO> mentions,
                                     Boolean liked) {
        return new CommentResDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getRootId(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getDepth(),
                comment.getContent(),
                comment.getMember().getId(),
                comment.getMember().getName(),
                comment.getMember().getProfileImageUrl(),
                comment.getLikeCount(),
                liked,
                comment.getCreatedAt(),
                mentions
        );
    }
}

