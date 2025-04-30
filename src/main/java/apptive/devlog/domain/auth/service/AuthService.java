package apptive.devlog.domain.auth.service;

import apptive.devlog.common.response.error.exception.*;
import apptive.devlog.domain.auth.dto.*;
import apptive.devlog.domain.user.enums.Provider;
import apptive.devlog.global.security.jwt.JwtTokenProvider;
import apptive.devlog.infrastructure.redis.repository.RedisRepository;
import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException();
        }
        if (!emailVerificationService.isVerified(requestDto.getEmail(), requestDto.getToken())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        User user = requestDto.toEntity(passwordEncoder);
        userRepository.save(user);
        return new UserSignupResponseDto(user);
    }

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException();
        }
        if (!user.getProviders().contains(Provider.LOCAL)) {
            throw new InvalidProviderException();
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return new UserLoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public UserRefreshResponseDto refresh(UserRefreshRequestDto requestDto) {
        final String accessToken = requestDto.getAccessToken();
        final String refreshToken = requestDto.getRefreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        final String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        redisRepository.deleteAccessToken(accessToken);
        redisRepository.deleteRefreshToken(refreshToken);

        final String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        final String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        return new UserRefreshResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(UserLogoutRequestDto requestDto) {
        redisRepository.deleteAccessToken(requestDto.getAccessToken());
        redisRepository.deleteRefreshToken(requestDto.getRefreshToken());
    }
}
