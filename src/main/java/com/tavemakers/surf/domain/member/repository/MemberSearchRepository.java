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

    public Slice<Member> searchMembers(Integer generation, Part part, String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (generation != null) {
            builder.and(member.tracks.any().generation.eq(generation));
        }

        if (part != null) {
            builder.and(member.tracks.any().part.eq(part));
        }

        if (keyword != null && !keyword.isBlank()) {
            BooleanExpression nameMatch = member.name.containsIgnoreCase(keyword);
            BooleanExpression universityMatch = member.university.containsIgnoreCase(keyword);
            BooleanExpression graduateSchoolMatch = member.graduateSchool.containsIgnoreCase(keyword);

            builder.and(nameMatch.or(universityMatch).or(graduateSchoolMatch));
        }

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
