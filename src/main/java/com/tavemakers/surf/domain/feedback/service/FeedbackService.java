package com.tavemakers.surf.domain.feedback.service;

import com.tavemakers.surf.domain.feedback.dto.req.FeedbackCreateReqDTO;
import com.tavemakers.surf.domain.feedback.dto.res.FeedbackResDTO;
import com.tavemakers.surf.domain.feedback.entity.Feedback;
import com.tavemakers.surf.domain.feedback.exception.TooManyFeedbackException;
import com.tavemakers.surf.domain.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final WriterHashService writerHashService;

    private static final int DAILY_LIMIT = 3; // 하루 최대 3회

    @Transactional
    public FeedbackResDTO createFeedback(FeedbackCreateReqDTO req, Long memberId) {
        // 1) 일 단위 writerHash 생성 (로그인 유저만 가능)
        String writerHash = writerHashService.hashDaily(memberId, LocalDate.now());

        // 2) 하루 횟수 제한 검사
        long todayCount = feedbackRepository.countByWriterHash(writerHash);
        if (todayCount >= DAILY_LIMIT) {
            throw new TooManyFeedbackException();
        }

        // 3) 저장
        Feedback saved = feedbackRepository.save(Feedback.of(req.content(), writerHash));

        return FeedbackResDTO.from(saved);
    }

    public Slice<FeedbackResDTO> getFeedbacks(Pageable pageable) {
        return feedbackRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(FeedbackResDTO::from);
    }
}
