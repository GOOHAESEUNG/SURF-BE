package com.tavemakers.surf.domain.comment.dto.res;

import com.tavemakers.surf.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멘션 회원 검색 응답 DTO")
public record MentionSearchResDTO(

        @Schema(description = "회원 ID", example = "2")
        Long memberId,

        @Schema(description = "회원 이름(닉네임)", example = "홍길동")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://cdn.surf.com/profile/hong.png")
        String profileImageUrl
) {
    public static MentionSearchResDTO from(Member member) {
        return new MentionSearchResDTO(
                member.getId(),
                member.getName(),
                member.getProfileImageUrl()  // Member 엔티티에 없으면 null로 들어감
        );
    }
}
