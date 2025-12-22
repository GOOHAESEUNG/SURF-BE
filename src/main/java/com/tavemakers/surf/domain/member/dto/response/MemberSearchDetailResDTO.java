package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record MemberSearchDetailResDTO(
        Long memberId,
        String name,
        String university,
        String profileImageUrl,
        List<TrackResDTO> trackList
) {
    public static MemberSearchDetailResDTO from(Member member) {
        List<TrackResDTO> trackDtoList = member.getTracks()
                .stream()
                .sorted(Comparator.comparing(Track::getGeneration).reversed())
                .map(TrackResDTO::from)
                .toList();

        return MemberSearchDetailResDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .university(member.getUniversity())
                .profileImageUrl(member.getProfileImageUrl())
                .trackList(trackDtoList)
                .build();
    }
}
