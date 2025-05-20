package apptive.devlog.auth.service;

import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signup(User user){
        // 서비스에서 exception throw
        if(isEmailDuplicated(user.getEmail())){
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        if(isNicknameDuplicated(user.getNickname())){
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        return userRepository.save(user);
    }

    //로그인
    public User login(String email, String password){
        return userRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicated(String email){
        // 중복 체크 처리
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 체크
    public boolean isNicknameDuplicated(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public User getUser(String name){
        Optional<User> user = userRepository.findByName(name);

        if(user.isEmpty()){
            throw new NoSuchElementException("해당 사용자는 존재하지 않습니다.");
        }
        else{
            return user.get();
        }
    }
}
