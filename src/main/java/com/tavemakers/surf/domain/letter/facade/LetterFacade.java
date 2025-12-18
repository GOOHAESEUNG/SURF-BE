package com.tavemakers.surf.domain.letter.facade;

import com.tavemakers.surf.domain.letter.dto.req.LetterCreateReqDTO;
import com.tavemakers.surf.domain.letter.dto.res.LetterResDTO;
import com.tavemakers.surf.domain.letter.entity.Letter;
import com.tavemakers.surf.domain.letter.exception.LetterMailSendFailException;
import com.tavemakers.surf.domain.letter.service.LetterGetService;
import com.tavemakers.surf.domain.letter.service.LetterSaveService;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.global.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LetterFacade {

    private final MemberGetService memberGetService;
    private final LetterSaveService letterSaveService;
    private final EmailSender emailSender;
    private final LetterGetService letterGetService;

    public LetterResDTO createLetter(Long senderId, LetterCreateReqDTO req) {

        // 1) 발신자 조회
        Member sender = memberGetService.getMember(senderId);

        // 2) 수신자 조회
        Member receiver = memberGetService.getMember(req.getReceiverId());

        // 3) 이메일 본문 생성
        String emailBody = """
        [Surf에서 %s님이 보낸 쪽지입니다.]
        
        %s
        
        회신 희망 이메일: %s
        SNS: %s
        """
                .formatted(
                        sender.getName(),
                        req.getContent(),
                        req.getReplyEmail(),
                        req.getSns() != null ? req.getSns() : "-"
                );

        // 4) 이메일 전송 (실패 시 예외)
        try {
            emailSender.sendMail(
                    receiver.getEmail(),
                    req.getTitle(),
                    emailBody
            );
        } catch (MailException e) {
            throw new LetterMailSendFailException();
        }


        // 5) 엔티티 생성
        Letter letter = Letter.create(
                req.getTitle(),
                req.getContent(),
                req.getSns(),
                req.getReplyEmail(),
                sender,
                receiver
        );

        // 6) 저장
        Letter saved = letterSaveService.save(letter);

        // 7) 저장된 엔티티 기반으로 Response 생성
        return LetterResDTO.from(saved);
    }

    public Slice<LetterResDTO> getSentLetters(Long senderId, Pageable pageable) {
        return letterGetService.getSentLetters(senderId, pageable)
                .map(LetterResDTO::from);
    }

}
