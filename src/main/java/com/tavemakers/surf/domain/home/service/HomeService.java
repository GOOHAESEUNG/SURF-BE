package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.response.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeResDTO;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeContentRepository homeContentRepository;
    private final HomeBannerRepository homeBannerRepository;

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public HomeResDTO getHome() {
        // 1) main message
        String message = homeContentRepository.findMessage().orElse("");

        // 2) banners
        List<HomeBannerResDTO> banners = homeBannerRepository.findAllByOrderByDisplayOrderAsc()
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
        String nextScheduleDate = null;
        String nextScheduleDeepLink = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");

        Optional<Schedule> next = scheduleRepository.findFirstByCategoryAndStartAtAfterOrderByStartAtAsc(
                "regular",
                LocalDateTime.now()
        );

        if (next.isPresent()) {
            Schedule s = next.get();
            nextTitle = s.getTitle();
            nextScheduleDate = s.getStartAt().toLocalDate().format(formatter);

            if (s.getPost() != null && s.getPost().getBoard() != null) {
                Long postId = s.getPost().getId();
                Long boardId = s.getPost().getBoard().getId();
                nextScheduleDeepLink = "/board/" + boardId + "/post/" + postId;
            }
        }

        return new HomeResDTO(
                message,
                banners,
                memberName,
                memberGeneration,
                memberPart,
                nextTitle,
                nextScheduleDate,
                nextScheduleDeepLink
        );
    }
}