package apptive.devlog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화(테스트용)
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // H2 콘솔용 설정
                )

                // URL 별 접근 권한
                .authorizeHttpRequests((auth) -> auth
                        // 로그인/회원가입 페이지 인증 없이 접근 허용
                        .requestMatchers("/users/login", "/users/signup").permitAll()
                        .requestMatchers("/api/board").permitAll() // board api 테스트용
                        .requestMatchers("/mail/html").permitAll() // mail api 테스트용
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔용 설정
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/users/login")
                        // 로그인 성공 시 이동 url
                        .defaultSuccessUrl("/users/home", true)
                        // 로그인 페이지 인증 없이 접근 허용
                        .permitAll()
                )
                .logout((logout) -> logout
                        // 로그아웃 성공 이동 url
                        .logoutSuccessUrl("/users/login?logout")
                        // 로그아웃 인증 없이 접근 허용
                        .permitAll()
                );

        return http.build();
    }

    // 인증 관리자 설정 (AuthenticationManager 빈 등록)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 비밀번호 인코딩(BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
