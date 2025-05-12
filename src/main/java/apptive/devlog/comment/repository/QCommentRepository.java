package apptive.devlog.comment.repository;


import apptive.devlog.domain.Comment;
import apptive.devlog.domain.QComment;
import apptive.devlog.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static apptive.devlog.domain.QComment.*;
import static apptive.devlog.domain.QMember.*;

@Repository
public class QCommentRepository {

    private final JPAQueryFactory queryFactory;

    public QCommentRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    public Page<Comment> findParentComment(Long id, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(id).and(comment.parent.id.isNull()))
                .orderBy(comment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = Optional.ofNullable(queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(id).and(comment.parent.id.isNull()))
                .fetchOne()).orElse(0L);

       return new PageImpl<>(comments, pageable, count);
    }
}
