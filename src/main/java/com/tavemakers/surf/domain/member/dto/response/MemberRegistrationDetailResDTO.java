package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record MemberRegistrationDetailResDTO(
        Long memberId,
        String username,
        String university,
        String profileImageUrl,
        List<TrackResDTO> trackList,
        String createdAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

    public static MemberRegistrationDetailResDTO from(Member member) {
        return MemberRegistrationDetailResDTO.builder()
                .memberId(member.getId())
                .username(member.getName())
                .university(member.getUniversity())
                .profileImageUrl(member.getProfileImageUrl())
                .trackList(member.getTracks().stream()
                        .map(TrackResDTO::from)
                        .toList())
                .createdAt(member.getCreatedAt().format(FORMATTER))
                .build();
    }
}
