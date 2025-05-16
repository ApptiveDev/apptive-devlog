package apptive.devlog.follow.service;

import apptive.devlog.domain.Follow;
import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.follow.dto.FollowDto;
import apptive.devlog.follow.exception.BadFollowRequestException;
import apptive.devlog.follow.repository.FollowRepository;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.service.MemberService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private EntityManager em;


    @Test
    void followReq() {

        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());


        assertThat(follow.getFromMember().getId()).isEqualTo(fromMember.getId());
        assertThat(follow.getToMember().getId()).isEqualTo(toMember.getId());
        assertThat(follow.isAccepted()).isFalse();

    }

    @Test
    void getFollowReqList() {
        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        List<FollowDto> followSenderDtos = followService.followReqList(toMember.getEmail());
        assertThat(followSenderDtos.size()).isEqualTo(1);
        assertThat(follow.getFromMember().getId()).isEqualTo(fromMember.getId());
        assertThat(follow.getToMember().getId()).isEqualTo(toMember.getId());
    }

    @Test
    void FollowResponseOK() {

        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        followService.followResponse(follow.getId(), "y");

        flushAndClear();

        assertThat(follow.isAccepted()).isTrue();

    }

    @Test
    void FollowResponseNOTOK() {

        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        followService.followResponse(follow.getId(), "n");

        flushAndClear();

        assertThat(followRepository.findById(follow.getId())).isEmpty();
    }

    @Test
    void followCancel() {
        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        followService.followResponse(follow.getId(), "y");

        followService.cancelFollow(follow.getId(), fromMember.getEmail());

        assertThat(followRepository.findById(follow.getId())).isEmpty();
    }

    @Test
    void followCancelFail() {
        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");
        Member noFollower = joinTestCase("lak4738@naver.com", "안넝넝", "안넝넝");

        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        followService.followResponse(follow.getId(), "y");

        assertThatThrownBy(() -> followService.cancelFollow(follow.getId(), noFollower.getEmail()))
                .isInstanceOf(BadFollowRequestException.class);
    }

    @Test
    void getFollowersAndFollowings() {
        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        followService.followResponse(follow.getId(), "y");

        List<FollowDto> followDtos = followService.followersList(toMember.getEmail());
        List<FollowDto> followDtos1 = followService.followingList(fromMember.getEmail());
        assertThat(followDtos.size()).isEqualTo(1);
        assertThat(followDtos1.size()).isEqualTo(1);
    }

    @Test
    void getFollowersAndFollowingsEmpty() {
        Member toMember = joinTestCase("ljw0626@naver.com", "안뇽뇽", "안뇽뇽");
        Member fromMember = joinTestCase("lak4739@naver.com", "안눙눙", "안눙눙");


        Follow follow = followService.followRequest(toMember.getId(), fromMember.getEmail());

        List<FollowDto> followDtos = followService.followersList(toMember.getEmail());
        List<FollowDto> followDtos1 = followService.followingList(fromMember.getEmail());
        assertThat(followDtos.size()).isEqualTo(0);
        assertThat(followDtos1.size()).isEqualTo(0);
    }



    private void flushAndClear() {
        em.flush();
        em.clear();
    }


    private Member joinTestCase(String email, String nickname, String username) {
        JoinForm joinForm =
                new JoinForm(email, "Qwer1234!!","Qwer1234!!", username,
                        nickname, LocalDate.of(2001,6,26), Gender.MALE);
        return memberService.join(joinForm);
    }
}