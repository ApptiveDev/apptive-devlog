package apptive.devlog.Comment.Service;

import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Comment.Dto.CreateCommentRequest;

public interface CommentService {
    Long createComment(Long postId, CreateCommentRequest request, Member member);
    void deleteComment(Long postId, Long commentId, Member member);
} 