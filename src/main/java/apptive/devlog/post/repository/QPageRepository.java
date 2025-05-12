package apptive.devlog.post.repository;

import apptive.devlog.domain.Member;
import apptive.devlog.post.dto.PostResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static apptive.devlog.domain.QPost.*;

@Repository
public class QPageRepository {

    private final JPAQueryFactory queryFactory;

    public QPageRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    public PageImpl<PostResponse> findByMemberPage(Member member, Pageable pageable, String title) {

        List<PostResponse> contents = queryFactory.selectFrom(post)
                .where(post.member.eq(member), titleLike(title))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(p->new PostResponse(p, member.getNickname())).toList();

        long count = Optional.ofNullable(queryFactory.select(post.count())
                .from(post)
                .where(post.member.eq(member), titleLike(title))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(contents, pageable, count);
    }

    private BooleanExpression titleLike(String title) {
        if (!StringUtils.hasText(title)) return null;
        else {
            return post.title.like("%" + title + "%");
        }
    }
}
