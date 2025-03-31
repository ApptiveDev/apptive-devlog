package apptive.devlog.Member.Service;

import apptive.devlog.Global.Auth.Dto.SignUpDto;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Dto.UpdateProfileDto;
import apptive.devlog.Member.Dto.UpdateProfileRequest;
import jakarta.transaction.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

public interface MemberService {
    // parseDate 메소드: 문자열을 LocalDate로 파싱
    LocalDate parseDate(String birthDateStr);

    boolean checkInput(SignUpDto signUpDto);


    Member signUp(SignUpDto dto);

    @Transactional
    void updateProfile(UpdateProfileDto updateProfileDto) throws AccessDeniedException;


}
