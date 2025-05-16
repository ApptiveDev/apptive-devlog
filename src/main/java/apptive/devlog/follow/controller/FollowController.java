package apptive.devlog.follow.controller;

import apptive.devlog.follow.dto.FollowDto;
import apptive.devlog.follow.service.FollowService;
import apptive.devlog.mail.MailService;
import apptive.devlog.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final MailService mailService;

    @PostMapping("/{id}") // 팔로우 요청
    public ResponseEntity<Void> followReq(@PathVariable Long id, @AuthenticationPrincipal MemberDetails memberDetails) {

        followService.followRequest(id, memberDetails.getUsername());
        mailService.sendFollowReqMail(id, memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/requests") // 팔로우 요청 리스트
    public ResponseEntity<?> getFollowRequest(@AuthenticationPrincipal MemberDetails memberDetails) {

        List<FollowDto> response = followService.followReqList(memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{followId}/response") // 팔로우 요청 응답
    public ResponseEntity<Void> followResponse(@PathVariable Long followId, @RequestParam String action, @AuthenticationPrincipal MemberDetails memberDetails) {
        if(followService.followResponse(followId, action)) {
            mailService.sendFollowResponseMail(followId);
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @PostMapping("/{followId}/cancel")
    public ResponseEntity<Void> cancelFollow(@PathVariable Long followId, @AuthenticationPrincipal MemberDetails memberDetails) {
        followService.cancelFollow(followId, memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/followers") // 팔로워 리스트
    public ResponseEntity<?> getFollowers(@AuthenticationPrincipal MemberDetails memberDetails) {

        List<FollowDto> response = followService.followersList(memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/followings") // 팔로잉 리스트
    public ResponseEntity<?> getFollowings(@AuthenticationPrincipal MemberDetails memberDetails) {

        List<FollowDto> response = followService.followingList(memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
