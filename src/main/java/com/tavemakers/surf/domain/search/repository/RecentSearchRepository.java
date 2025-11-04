package com.tavemakers.surf.domain.search.repository;

import com.tavemakers.surf.domain.search.entity.RecentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    Optional<RecentSearch> findByMemberIdAndKeyword(Long memberId, String keyword);
    List<RecentSearch> findTop10ByMemberIdOrderBySearchedAtDesc(Long memberId);

    @Query("""
      select r.id from RecentSearch r
      where r.member.id = :memberId
      order by r.searchedAt desc
    """)
    Page<Long> findAllIdsByMemberIdOrderBySearchedAtDesc(@Param("memberId") Long memberId, Pageable pageable);

    void deleteByMemberId(Long memberId);
}