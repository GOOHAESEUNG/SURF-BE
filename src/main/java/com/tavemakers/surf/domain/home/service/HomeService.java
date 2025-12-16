package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.res.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.dto.res.HomeResDTO;
import com.tavemakers.surf.domain.home.repository.HomeBannerRepository;
import com.tavemakers.surf.domain.home.repository.HomeContentRepository;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import com.tavemakers.surf.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeContentRepository homeContentRepository;
    private final HomeBannerRepository homeBannerRepository;

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public HomeResDTO getHome() {
        // 1) main text
        String mainText = homeContentRepository.findMainText().orElse("");

        // 2) banners
        List<HomeBannerResDTO> banners = homeBannerRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(HomeBannerResDTO::from)
                .toList();

        // 3) member summary (비로그인 null 처리)
        String memberName = null;
        Integer memberGeneration = null;
        String memberPart = null;

        Long memberId = SecurityUtils.getCurrentMemberId();
        if (memberId != null) {
            var m = memberRepository.findById(memberId).orElse(null);

            if (m != null) {
                memberName = m.getName();

                List<Track> tracks = m.getTracks();
                if (tracks != null && !tracks.isEmpty()) {
                    var firstTrack = tracks.stream()
                            .filter(t -> t.getGeneration() != null)
                            .min(Comparator.comparing(Track::getGeneration))
                            .orElse(null);

                    if (firstTrack != null) {
                        memberGeneration = firstTrack.getGeneration();
                        memberPart = (firstTrack.getPart() != null) ? firstTrack.getPart().name() : null;
                    }
                }
            }
        }

        // 4) next schedule
        String nextTitle = null;
        Long nextDaysLeft = null;
        String nextLabel = null;

        var next = scheduleRepository.findFirstByStartAtAfterOrderByStartAtAsc(LocalDateTime.now()); // 필드명 맞춰 수정
        if (next.isPresent()) {
            var s = next.get();
            nextTitle = s.getTitle();
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), s.getStartAt().toLocalDate());
            nextDaysLeft = daysLeft;
            nextLabel = toDdayLabel(daysLeft);
        }

        return new HomeResDTO(
                mainText,
                banners,
                memberName,
                memberGeneration,
                memberPart,
                nextTitle,
                nextDaysLeft,
                nextLabel
        );
    }

    private static String toDdayLabel(long daysLeft) {
        if (daysLeft > 0) return "D-" + daysLeft;
        if (daysLeft == 0) return "D-DAY";
        return "D+" + Math.abs(daysLeft);
    }
}