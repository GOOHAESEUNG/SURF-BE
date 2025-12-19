package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.res.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.dto.res.HomeResDTO;
import com.tavemakers.surf.domain.home.repository.HomeBannerRepository;
import com.tavemakers.surf.domain.home.repository.HomeContentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import com.tavemakers.surf.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            Member m = memberRepository.findById(memberId).orElse(null);

            if (m != null) {
                memberName = m.getName();

                List<Track> tracks = m.getTracks();
                if (tracks != null && !tracks.isEmpty()) {
                    Track latestTrack = tracks.stream()
                            .filter(t -> t.getGeneration() != null)
                            .max(Comparator.comparing(Track::getGeneration))
                            .orElse(null);

                    if (latestTrack != null) {
                        memberGeneration = latestTrack.getGeneration();
                        memberPart = latestTrack.getPart() != null
                                ? latestTrack.getPart().name()
                                : null;
                    }
                }
            }
        }

        // 4) next schedule
        String nextTitle = null;
        LocalDate nextScheduleDate = null;

        var next = scheduleRepository.findFirstByStartAtAfterOrderByStartAtAsc(LocalDateTime.now()); // 필드명 맞춰 수정
        if (next.isPresent()) {
            Schedule s = next.get();
            nextTitle = s.getTitle();
            nextScheduleDate = s.getStartAt().toLocalDate();
        }

        return new HomeResDTO(
                mainText,
                banners,
                memberName,
                memberGeneration,
                memberPart,
                nextTitle,
                nextScheduleDate
        );
    }
}