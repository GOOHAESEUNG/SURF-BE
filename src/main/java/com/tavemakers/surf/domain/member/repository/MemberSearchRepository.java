package com.tavemakers.surf.domain.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.QTrack;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.querydsl.core.types.dsl.Expressions;

import static com.tavemakers.surf.domain.member.entity.QMember.member;
import static com.tavemakers.surf.domain.member.entity.QTrack.track;

@Repository
@RequiredArgsConstructor
public class MemberSearchRepository {

    private final JPAQueryFactory queryFactory;

    /*
    * NOTE: SURF 규칙
    * "기수 > 이름 > 대학 > 가입일" 순으로 정렬
    * */
    public Slice<Member> searchMembers(Integer generation, Part part, String keyword, Pageable pageable) {
        BooleanBuilder builder = createSearchBuilder(generation, part, keyword);

        NumberExpression<Integer> maxGeneration = getMaxGenerationExpression();
        List<Member> results = queryFactory
                .selectFrom(member)
                .distinct()
                .leftJoin(member.tracks, track)
                .where(builder)
                .orderBy(
                        maxGeneration.desc().nullsLast(),
                        member.name.asc(),
                        member.university.asc(),
                        member.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    public Long countMembers(Integer generation, Part part, String keyword) {
        BooleanBuilder builder = createSearchBuilder(generation, part, keyword);

        return queryFactory
                .select(member.countDistinct())
                .from(member)
                .leftJoin(member.tracks, track)
                .where(builder)
                .fetchOne();
    }

    private BooleanBuilder createSearchBuilder(Integer generation, Part part, String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        if (generation != null) {
            builder.and(member.tracks.any().generation.eq(generation));
        }

        if (part != null) {
            builder.and(member.tracks.any().part.eq(part));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(member.name.containsIgnoreCase(keyword)
                    .or(member.university.containsIgnoreCase(keyword))
                    .or(member.graduateSchool.containsIgnoreCase(keyword)));
        }

        return builder;
    }

    private NumberExpression<Integer> getMaxGenerationExpression() {
        QTrack subTrack = new QTrack("subTrack");
        return Expressions.asNumber(
                JPAExpressions
                        .select(subTrack.generation.max())
                        .from(subTrack)
                        .where(subTrack.member.eq(member))
        );
    }

}
