package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndStatus(Long memberId, MemberStatus status);
  
    boolean existsByEmail(String email);
  
    Optional<Member> findByEmail(String email);

    //현재 활동 중 + 특정 이름을 가진 회원 리스트 반환
    List<Member> findByActivityStatusAndName(Boolean activityStatus, String name);

    Optional<Member> findByEmailAndStatus(String email, MemberStatus status);
  
    Optional<Member> findByKakaoId(Long kakaoId);

    // 댓글(comment)에서 멘션할 회원을 검색할 때 사용
    List<Member> findTop10ByNameContainingIgnoreCase(String keyword);
}
