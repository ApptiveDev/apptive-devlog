package apptive.devlog.auth.service;

import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signup(User user){
        return userRepository.save(user);
    }

    public boolean isEmailDuplicated(String email){
        // 중복 체크

        return false;
    }

    public boolean isNicknameDuplicated(String nickname){
        // 중복 체크

        return false;
    }
}
