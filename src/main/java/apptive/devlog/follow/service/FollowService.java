package apptive.devlog.follow.service;

import apptive.devlog.domain.Follow;
import apptive.devlog.domain.Member;
import apptive.devlog.follow.dto.FollowDto;
import apptive.devlog.follow.exception.BadFollowRequestException;
import apptive.devlog.follow.repository.FollowRepository;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public Follow followRequest(Long toUserId, String fromUserEmail) {
        Member toMember = memberRepository.findById(toUserId).
                orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        Member fromMember = memberRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        if (toMember.getId().equals(fromMember.getId())) throw new BadFollowRequestException("자기 자신을 팔로우할 수 없습니다.");

        return followRepository.save(new Follow(fromMember, toMember, false));
    }

    public List<FollowDto> followReqList(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        List<Follow> requests = followRepository.findRequests(member);

        return requests.stream()
                .map(f -> new FollowDto(f.getId(), f.getFromMember().getId(), f.getFromMember().getNickname()))
                .toList();
    }

    public boolean followResponse(Long followId,String action) {

        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new BadFollowRequestException("존재하는 팔로우 요청이 아닙니다."));

        if (follow.isAccepted()) throw new BadFollowRequestException("이미 승인된 팔로우 요청입니다.");

        if (action.equals("y")) {
            follow.setAccepted(true);
            return true;
        }
        else if (action.equals("n")) {
            followRepository.delete(follow);
            return false;
        }
        else throw new BadFollowRequestException("팔로잉 응답은 y or n");
    }

    public void cancelFollow(Long followId, String email) {
        Member cancelMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));
        Follow follow = followRepository.findByIdWithMember(followId)
                .orElseThrow(() -> new BadFollowRequestException("존재하는 팔로우 요청이 아닙니다."));

        if(!follow.isAccepted()) throw new BadFollowRequestException("팔로워가 아닙니다.");

        Long cancelMemberId = cancelMember.getId();

        if (!cancelMemberId.equals(follow.getToMember().getId()) && !cancelMemberId.equals(follow.getFromMember().getId()))
            throw new BadFollowRequestException("본인의 팔로잉 정보가 아닙니다.");



        followRepository.delete(follow);
    }

    public List<FollowDto> followersList(String email) {
        Member toMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        return followRepository.findFollowers(toMember)
                .stream().map(f-> new FollowDto(f.getId(), f.getFromMember().getId(), f.getFromMember().getNickname()))
                .toList();
    }

    public List<FollowDto> followingList(String email) {
        Member fromMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        return followRepository.findFollowings(fromMember)
                .stream().map(f -> new FollowDto(f.getId(), f.getToMember().getId(), f.getToMember().getNickname()))
                .toList();
    }



}
