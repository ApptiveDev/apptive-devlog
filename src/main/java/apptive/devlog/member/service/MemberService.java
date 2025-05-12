package apptive.devlog.member.service;

import apptive.devlog.domain.Member;
import apptive.devlog.domain.UploadFile;
import apptive.devlog.error.ErrorMessage;
import apptive.devlog.fileupload.service.UploadService;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.dto.MemberUpdateForm;
import apptive.devlog.member.exception.DuplicateMemberException;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.exception.PasswordException;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.member.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;


@Transactional
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RefreshRepository refreshRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UploadService uploadService;

    @Override
    public UserDetails loadUserByUsername(String email)  {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일"));

        return new MemberDetails(findMember);
    }

    public void join(JoinForm form) {
        String password = passwordEncoder.encode(form.getPassword());

        HashMap<String, String> errors = new HashMap<>();

        if (memberRepository.findByEmail(form.getEmail()).isPresent())
            errors.put("email", "이미 사용중인 이메일입니다");
        if (memberRepository.findByNickname(form.getNickname()).isPresent())
            errors.put("nickname", "이미 사용중인 별명입니다");

        if (!errors.isEmpty()) throw new DuplicateMemberException(new ErrorMessage(errors));

        if (!form.getPassword().equals(form.getConfirmPassword())) throw new PasswordException("비밀번호가 서로 다릅니다.");

        Member member = new Member(form.getEmail(), password, form.getUsername(), form.getNickname(),
                form.getBirthdate(), "ROLE_MEMBER", form.getGender());

        memberRepository.save(member);
    }

    public void update(String email, MemberUpdateForm form) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));


        Map<String, String> errors = new HashMap<>();

        if (!findMember.getNickname().equals(form.getNickname()) && memberRepository.findByNickname(form.getNickname()).isPresent())
            errors.put("nickname", "이미 사용중인 별명입니다");
        if (!errors.isEmpty()) throw new DuplicateMemberException(new ErrorMessage(errors));


        if (!passwordEncoder.matches(form.getCurrentPassword(), findMember.getPassword())) throw new PasswordException("현재 비밀번호와 다릅니다.");
        if (!form.getNewPassword().equals(form.getConfirmPassword())) throw new PasswordException("새로운 비밀번호가 서로 다릅니다.");

        String newPassword = passwordEncoder.encode(form.getNewPassword());

        findMember.updateNickname(form.getNickname());
        findMember.updatePassword(newPassword);
        findMember.updateBirthdate(form.getBirthdate());
        findMember.updateGender(form.getGender());
    }

    public void withdraw(String email) {

        Member findMember = memberRepository.findByEmailWithFiles(email)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        uploadService.deleteFiles(findMember.getUploadFiles());

        memberRepository.delete(findMember);

        if (refreshRepository.existsByEmail(email)) refreshRepository.deleteByEmail(email);

    }
}
