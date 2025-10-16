package com.tavemakers.surf.domain.member.dto.request;

import com.tavemakers.surf.global.logging.LogParam;
import com.tavemakers.surf.global.logging.LogPropsProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Schema(description = "프로필 수정 요청 DTO")
public record ProfileUpdateReqDTO(
        @Email
        String email,

        @NotNull
        String university,
        String graduateSchool,

        @Pattern(regexp = "^[0-9\\-]{8,15}$")
        String phoneNumber,

        @NotNull
        Boolean phoneNumberPublic,
        List<CareerCreateReqDTO> careersToCreate,
        List<CareerUpdateReqDTO> careersToUpdate,
        List<Long> careerIdsToDelete
) implements LogPropsProvider {
    public Map<String, Object> buildProps() {
        List<String> changedFields = new ArrayList<>();

        if (email != null && !email.isBlank()) changedFields.add("email");
        if (university != null && !university.isBlank()) changedFields.add("university");
        if (graduateSchool != null && !graduateSchool.isBlank()) changedFields.add("graduateSchool");
        if (phoneNumber != null && !phoneNumber.isBlank()) changedFields.add("phoneNumber");
        if (phoneNumberPublic != null) changedFields.add("phoneNumberPublic");

        return Map.of(
                "changed_fields", changedFields,
                "careers_created_count", careersToCreate == null ? 0 : careersToCreate.size(),
                "careers_updated", careersToUpdate == null ? List.of() :
                        careersToUpdate.stream().map(CareerUpdateReqDTO::careerId).toList(),
                "careers_updated_count", careersToUpdate == null ? 0 : careersToUpdate.size(),
                "careers_deleted", careerIdsToDelete == null ? List.of() : careerIdsToDelete
        );
    }
}
