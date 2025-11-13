package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.entity.CommentMention;
import com.tavemakers.surf.domain.comment.repository.CommentMentionRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.comment.dto.res.MentionResDTO;
import com.tavemakers.surf.domain.comment.exception.CommentMentionSelfException;
import com.tavemakers.surf.domain.comment.dto.res.MentionSearchResDTO;
import com.tavemakers.surf.domain.comment.exception.InvalidMentionSearchKeywordException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMentionService {

    private final CommentMentionRepository commentMentionRepository;
    private final MemberRepository memberRepository;

    /** 댓글 생성 시 멘션이 있으면 저장 */
    public List<CommentMention> createMentions(Comment comment, List<Long> mentionMemberIds) {
        if (mentionMemberIds == null || mentionMemberIds.isEmpty()) {
            return List.of();
        }

        Long writerId = comment.getMember().getId();

        // 자기 자신 멘션 방지
        if (mentionMemberIds.contains(writerId)) {
            throw new CommentMentionSelfException();
        }

        // 중복 제거
        List<Long> filteredIds = mentionMemberIds.stream()
                .distinct()
                .toList();

        List<Member> mentionedMembers = memberRepository.findAllById(filteredIds);

        List<CommentMention> mentions = mentionedMembers.stream()
                .map(member -> CommentMention.of(comment, member))
                .toList();

        return commentMentionRepository.saveAll(mentions);
    }

    /** 댓글 삭제 시 멘션 전체 삭제 */
    public void deleteAllByComment(Comment comment) {
        commentMentionRepository.deleteAllByComment(comment);
    }

    /** 댓글에 달린 멘션 목록 조회 (DTO 변환) */
    @Transactional(readOnly = true)
    public List<MentionResDTO> getMentions(Long commentId) {
        return commentMentionRepository.findByCommentIdWithMember(commentId)
                .stream()
                .map(MentionResDTO::from)
                .toList();
    }

    /** 멘션 가능한 회원 검색 (두 글자 이상 입력 시) */
    @Transactional(readOnly = true)
    public List<MentionSearchResDTO> searchMentionableMembers(String keyword) {

        // 입력 검증 (비었거나, @으로 시작하지 않거나, @ 뒤가 두 글자 미만일 경우 예외)
        if (keyword == null || keyword.isBlank() || !keyword.startsWith("@") || keyword.substring(1).trim().length() < 2) {
            throw new InvalidMentionSearchKeywordException();
        }

        // @ 제거 후 나머지 문자열 추출
        String namePart = keyword.substring(1).trim(); // '@홍길' → '홍길'

        // DB 조회 (정렬 없이)
        List<Member> candidates = memberRepository.findMentionCandidates(namePart);

        // 자바에서 정렬: 가장 최근 기수 → 오래된 순
        return candidates.stream()
                .sorted(Comparator.comparing(
                        (Member m) -> m.getTracks().stream()
                                .map(track -> track.getGeneration())
                                .max(Integer::compareTo) // track 중 최신 기수 기준
                                .orElse(0),
                        Comparator.reverseOrder()
                ))
                .limit(10)
                .map(MentionSearchResDTO::from)
                .toList();
    }
}
