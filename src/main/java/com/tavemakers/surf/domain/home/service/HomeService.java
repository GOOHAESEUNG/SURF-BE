package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.response.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeResDTO;
import com.tavemakers.surf.domain.home.entity.HomeContent;
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

    private static final Long HOME_CONTENT_ID = 1L;

    private final HomeContentRepository homeContentRepository;
    private final HomeBannerRepository homeBannerRepository;

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public HomeResDTO getHome() {
        // 1) main message
        String message = "";
        String sender = "";

        HomeContent hc = homeContentRepository.findById(HOME_CONTENT_ID).orElse(null);
        if (hc != null) {
            message = hc.getMessage();
            sender = hc.getSender();
        }

        // 2) banners
        List<HomeBannerResDTO> banners = homeBannerRepository.findAllByStatusTrueOrderByDisplayOrderAsc()
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

        // 4) display schedule
        String scheduleTitle = null;
        String scheduleDate = null;
        String scheduleDeepLink = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");

        Optional<Schedule> scheduleOpt = scheduleRepository.findFirstByCategoryAndStartAtAfterOrderByStartAtAsc(
                "regular",
                LocalDateTime.now()
        );

        if (scheduleOpt.isEmpty()) {
            scheduleOpt = scheduleRepository.findFirstByCategoryAndStartAtLessThanEqualOrderByStartAtDesc(
                            "regular",
                    LocalDateTime.now());
        }

        if (scheduleOpt.isPresent()) {
            Schedule s = scheduleOpt.get();
            scheduleTitle = s.getTitle();
            scheduleDate = s.getStartAt().toLocalDate().format(formatter);

            if (s.getPost() != null && s.getPost().getBoard() != null) {
                Long postId = s.getPost().getId();
                Long boardId = s.getPost().getBoard().getId();
                scheduleDeepLink = "/board/" + boardId + "/post/" + postId;
            }
        }

        return new HomeResDTO(
                message,
                sender,
                banners,
                memberName,
                memberGeneration,
                memberPart,
                scheduleTitle,
                scheduleDate,
                scheduleDeepLink
        );
    }
}