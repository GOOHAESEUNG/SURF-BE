package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Track;
import lombok.Builder;

@Builder
public record TrackResDTO(
        Integer generation,
        String part
) {
    public static TrackResDTO from(Track track) {
        return TrackResDTO.builder()
                .generation(track.getGeneration())
                .part(track.getPart().getDisplayName())
                .build();
    }
}
