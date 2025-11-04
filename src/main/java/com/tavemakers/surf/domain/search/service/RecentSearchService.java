package com.tavemakers.surf.domain.search.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.search.entity.RecentSearch;
import com.tavemakers.surf.domain.search.repository.RecentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecentSearchService {
    private final RecentSearchRepository repo;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveQuery(Long memberId, String raw) {
        String q = normalize(raw);
        if (q.isBlank()) return;

        // 중복이면 최신화
        Optional<RecentSearch> opt = repo.findByMemberIdAndKeyword(memberId, q);
        if (opt.isPresent()) {
            opt.get().touch();
        } else {
            Member m = memberRepository.getReferenceById(memberId);
            repo.save(RecentSearch.of(m, q));
        }

        // 10개 초과 트림
        Page<Long> ids = repo.findAllIdsByMemberIdOrderBySearchedAtDesc(memberId, PageRequest.of(1, Integer.MAX_VALUE));
        if (!ids.isEmpty()) repo.deleteAllByIdInBatch(ids.getContent()); // 11번째 이후 모두 삭제
    }

    @Transactional(readOnly = true)
    public List<String> getRecent10(Long memberId) {
        return repo.findTop10ByMemberIdOrderBySearchedAtDesc(memberId)
                .stream().map(RecentSearch::getKeyword).toList();
    }

    @Transactional
    public void clearAll(Long memberId) {
        repo.deleteByMemberId(memberId);
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}