package com.tavemakers.surf.domain.letter.facade;

import com.tavemakers.surf.domain.letter.dto.req.LetterCreateRequest;
import com.tavemakers.surf.domain.letter.dto.res.LetterResponse;
import com.tavemakers.surf.domain.letter.entity.Letter;
import com.tavemakers.surf.domain.letter.service.LetterSaveService;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.global.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LetterFacade {

    private final MemberGetService memberGetService;
    private final LetterSaveService letterSaveService;
    private final EmailService emailService;

    public LetterResponse createLetter(Long senderId, LetterCreateRequest req) {

        // 1) 발신자 조회
        Member sender = memberGetService.getMember(senderId);

        // 2) 수신자 조회
        Member receiver = memberGetService.getMember(req.getReceiverId());

        // 3) 엔티티 생성
        Letter letter = Letter.create(
                req.getTitle(),
                req.getContent(),
                req.getSns(),
                req.getReplyEmail(),
                sender,
                receiver
        );

        // 4) 저장
        Letter saved = letterSaveService.save(letter);

        String emailBody = """
        [Surf에서 %s님이 보낸 쪽지입니다.]
        
        %s
        
        회신 희망 이메일: %s
        SNS: %s
        """
                .formatted(
                        sender.getName(),
                        saved.getContent(),
                        saved.getReplyEmail(),
                        saved.getSns() != null ? saved.getSns() : "-"
                );


        // *** 이메일 전송 (제목 = title, 본문 = content 그대로 사용) ***
        emailService.sendMail(
                receiver.getEmail(),
                saved.getTitle(),
                emailBody
        );

        // 5) 저장된 엔티티 기반으로 Response 생성
        return LetterResponse.builder()
                .noteId(saved.getNoteId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .sns(saved.getSns())
                .replyEmail(saved.getReplyEmail())
                .senderId(sender.getId())
                .senderName(sender.getName())
                .receiverId(receiver.getId())
                .receiverName(receiver.getName())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
