package apptive.devlog.post.service;

import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.dto.ReCommentResponse;
import apptive.devlog.comment.repository.CommentRepository;
import apptive.devlog.comment.repository.QCommentRepository;
import apptive.devlog.domain.Comment;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.Post;
import apptive.devlog.domain.UploadFile;
import apptive.devlog.fileupload.dto.UploadFileDto;
import apptive.devlog.fileupload.repository.UploadRepository;
import apptive.devlog.fileupload.service.UploadService;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.post.dto.*;
import apptive.devlog.post.exception.BadPostRequestException;
import apptive.devlog.post.exception.NotFoundPostException;
import apptive.devlog.post.repository.PostRepository;
import apptive.devlog.post.repository.QPageRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final QCommentRepository qCommentRepository;
    private final QPageRepository qPageRepository;
    private final UploadRepository uploadRepository;
    private final UploadService uploadService;

    public PostResponse save(CreatePostRequest post, String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하지 않는 회원입니다"));
        Post saved = postRepository.save(new Post(post.getTitle(), post.getContent(), findMember));

        uploadFiles(post, findMember, saved);

        return new PostResponse(saved, findMember.getNickname());
    }


    public void deletePost(Long id, String email) {
        Post findPost = postRepository.findWithFiles(id)
                .orElseThrow(() -> new NotFoundPostException("해당 게시글이 존재하지 않습니다."));

        if (!findPost.getMember().getEmail().equals(email))
            throw new BadPostRequestException("자신의 게시글만 삭제할 수 있습니다.");

        uploadService.deleteFiles(findPost.getFiles()); // AWS s3 서버에서 이미지를 삭제

        postRepository.deleteById(id);
    }

    public void updatePost(Long id, String email, UpdatePostRequest post) {
        Post findPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundPostException("해당 게시글이 존재하지 않습니다."));

        Member findMember = findPost.getMember();

        if (!findMember.getEmail().equals(email))
            throw new BadPostRequestException("자신의 게시글만 수정할 수 있습니다.");


        findPost.changeTitle(post.getTitle());
        findPost.changeContent(post.getContent());
    }

    public PostWithCommentResponse findPost(Long id, Pageable pageable) {
        Post post = postRepository.findWithFiles(id)
                .orElseThrow(() -> new NotFoundPostException("해당 게시글이 존재하지 않습니다"));

        Page<Comment> pages = qCommentRepository.findParentComment(post.getId(), pageable);
        List<Comment> parents = pages.getContent();

        List<ReCommentResponse> reComments = commentRepository.findReComments(parents);


        Map<Long, List<ReCommentResponse>> reCommentsMap = reComments.stream()
                .collect(Collectors.groupingBy(ReCommentResponse::getParentId));

        List<CommentResponse> result = parents.stream().
                map(c -> new CommentResponse(c.getMember().getNickname(), c, reCommentsMap.getOrDefault(c.getId(), new ArrayList<>()))
                ).toList();

        return new PostWithCommentResponse(post, result, pages.getTotalPages());
    }


    public PostPageResponse findPostsByMember(String nickname, Pageable pageable, String title) {


        Member findMember = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundMemberException("존재하지 않는 회원입니다."));

        PageImpl<PostResponse> pages = qPageRepository.findByMemberPage(findMember, pageable, title);

        return new PostPageResponse(pages.getContent(), pages.getTotalPages());
    }


    private void uploadFiles(CreatePostRequest post, Member findMember, Post saved) {
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (UploadFileDto file : post.getFiles()) {
            uploadFiles.add(new UploadFile(file.getFileName(), file.getUrl(), saved, findMember));
        }

        uploadRepository.saveAll(uploadFiles);
    }


}
