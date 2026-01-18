package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.login.auth.service.RefreshTokenService;
import com.tavemakers.surf.domain.member.dto.request.AdminPageLoginReqDto;
import com.tavemakers.surf.domain.member.dto.request.PasswordReqDto;
import com.tavemakers.surf.domain.member.dto.response.AdminPageLoginResDto;
import com.tavemakers.surf.domain.member.dto.response.MemberRegistrationDetailResDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberRegistrationSliceResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Password;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.AdminPageRoleException;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.MemberPatchService;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.domain.score.service.PersonalScoreSaveService;
import com.tavemakers.surf.global.jwt.JwtService;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import com.tavemakers.surf.global.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAdminUsecase {

    private final MemberPatchService memberPatchService;
    private final MemberGetService memberGetService;
    private final MemberService memberService;
    private final PersonalScoreSaveService personalScoreSaveService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void changeRole (Long memberId, MemberRole role) {
        Member member = memberGetService.getMember(memberId);
        memberPatchService.grantRole(member, role);
    }

    @Transactional
    @LogEvent(value = "signup.approve", message = "회원가입 승인 처리")
    public void approveMember(
            @LogParam("member_id") Long memberId,
            @LogParam("approver_id") Long approverId
    ) {
        Member member = memberGetService.getMemberByStatus(memberId, MemberStatus.WAITING);
        memberService.approveMember(member);
        personalScoreSaveService.savePersonalScore(member);
    }

    @Transactional
    @LogEvent(value = "signup.reject", message = "회원가입 거절 처리")
    public void rejectMember(
            @LogParam("member_id") Long memberId,
            @LogParam("approver_id") Long approverId
    ) {
        Member member = memberGetService.getMemberByStatus(memberId, MemberStatus.WAITING);
        memberService.rejectMember(member);
    }

    @Transactional
    public void setUpPassword(PasswordReqDto dto) {
        Member member = memberGetService.getMember(SecurityUtils.getCurrentMemberId());
        member.updatePassword(dto.password());
    }

    public AdminPageLoginResDto loginAdminHomePage(AdminPageLoginReqDto dto, HttpServletResponse response) {
        Member member = memberGetService.getMemberByEmail(dto.email());
        member.checkPassword(dto.password());
        validateLoginMemberRole(member);

        String accessToken = jwtService.createAccessToken(member.getId(), member.getRole().name());
        String deviceId = UUID.randomUUID().toString();
        refreshTokenService.issue(response, member.getId(), deviceId);

        return AdminPageLoginResDto.of(accessToken, member);
    }

    public MemberRegistrationSliceResDTO readRegistrationList(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        Slice<MemberRegistrationDetailResDTO> registrationList = memberGetService.searchMembers(MemberStatus.WAITING, pageable)
                .map(MemberRegistrationDetailResDTO::from);

        return MemberRegistrationSliceResDTO.from(registrationList);
    }

    private void validateLoginMemberRole(Member member) {
        if(member.isMember()){
            throw new AdminPageRoleException();
        }
    }

}
