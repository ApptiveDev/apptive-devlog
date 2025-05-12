package apptive.devlog.comment.service;

import apptive.devlog.comment.dto.CommentRequest;
import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.exception.BadCommentRequestException;
import apptive.devlog.comment.exception.NotFoundCommentException;
import apptive.devlog.comment.repository.CommentRepository;
import apptive.devlog.comment.repository.QCommentRepository;
import apptive.devlog.domain.Comment;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.Post;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.post.exception.BadPostRequestException;
import apptive.devlog.post.exception.NotFoundPostException;
import apptive.devlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    public CommentResponse saveComment(CommentRequest request, Long id, String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 아닙니다."));
        Post findPost = postRepository.findById(id).orElseThrow(() -> new NotFoundPostException("게시글이 존재하지 않습니다"));

        Comment saved = commentRepository.save(new Comment(request.getContent(), findMember, findPost, null));

        return new CommentResponse(findMember.getNickname(), saved);
    }

    public CommentResponse saveReComment(CommentRequest request, Long postId, Long commentId, String email) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("댓글이 존재하지 않습니다."));
        if (parentComment.getParent() != null) throw new BadCommentRequestException("대댓글에는 대댓글 작성이 불가능합니다.");
        if (parentComment.isDeleted()) throw new BadCommentRequestException("삭제된 댓글에는 대댓글 작성이 불가능합니다.");

        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 아닙니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("게시글이 존재하지 않습니다"));

        Comment saved = commentRepository.save(new Comment(request.getContent(), findMember, findPost, parentComment));

        return new CommentResponse(findMember.getNickname(), saved);
    }

    public void updateComment(CommentRequest request, Long id, String email) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundCommentException("댓글이 존재하지 않습니다"));
        if (findComment.isDeleted())
            throw new BadCommentRequestException("삭제된 댓글입니다.");
        if (!findComment.getMember().getEmail().equals(email))
            throw new BadCommentRequestException("본인이 작성한 댓글만 삭제할 수 있습니다.");

        findComment.changeContent(request.getContent());
    }

    public void deleteComment(Long id, String email) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundCommentException("댓글이 존재하지 않습니다."));
        if (!findComment.getMember().getEmail().equals(email))
            throw new BadCommentRequestException("본인이 작성한 댓글만 삭제할 수 있습니다.");

        if (findComment.getParent() != null) {
            commentRepository.delete(findComment);
            Comment parent = findComment.getParent();
            if (parent.isDeleted() && commentRepository.countChild(parent) == 0) commentRepository.delete(parent);
        }
        else if (commentRepository.countChild(findComment) == 0) {
            commentRepository.delete(findComment);
        }
        else {
            findComment.softDelete();
        }
    }

}
