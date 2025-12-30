package com.tavemakers.surf.domain.badge.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record MemberBadgeSliceResDTO(
    List<MemberBadgeResDTO> content,
    int pageNumber,
    int pageSize,
    int numberOfElements,
    boolean isLast
) {
    public static MemberBadgeSliceResDTO from(Slice<MemberBadgeResDTO> slice) {
        return MemberBadgeSliceResDTO.builder()
                .content(slice.getContent())
                .pageNumber(slice.getNumber())
                .pageSize(slice.getSize())
                .numberOfElements(slice.getNumberOfElements())
                .isLast(slice.isLast())
                .build();
    }
}
