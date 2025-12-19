package com.tavemakers.surf.domain.member.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record MemberSearchSliceResDTO(
        List<MemberSearchDetailResDTO> content,
        int pageNumber,
        int pageSize,
        int numberOfElements,
        boolean isLast
) {
    public static MemberSearchSliceResDTO from(Slice<MemberSearchDetailResDTO> slice) {
        return MemberSearchSliceResDTO.builder()
                .content(slice.getContent())
                .pageNumber(slice.getNumber())
                .pageSize(slice.getSize())
                .numberOfElements(slice.getNumberOfElements())
                .isLast(slice.isLast())
                .build();
    }

}
