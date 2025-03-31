package apptive.devlog.Global.Config;

import apptive.devlog.Global.Auth.Filter.JwtAuthenticationFilter;
import apptive.devlog.Global.Auth.Handler.OAuth2SuccessHandler;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Global.Auth.Service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final UserDetailsService userDetailsService;
    private final OAuth2SuccessHandler successHandler;

    private final JwtTokenProvider jwtTokenProvider;

    public SpringSecurityConfig(OAuth2Service oAuth2Service, UserDetailsService userDetailsService, OAuth2SuccessHandler successHandler, JwtTokenProvider jwtTokenProvider) {
        this.oAuth2Service = oAuth2Service;
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .cors(AbstractHttpConfigurer::disable) // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/signup").permitAll() // 회원가입은 누구나 접근 가능하도록 수정!
                        .requestMatchers("/user/**").authenticated() // 멤버만 사용가능하도록 통제
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\": 403, \"code\": \"NO_AUTHORITY\", \"message\": \"접근 권한이 없습니다.\"}");
                        })
                )
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("loginId")	// [C] submit할 아이디
                        .passwordParameter("password")
                        .permitAll()
                        .defaultSuccessUrl("/") // 성공 시 리다이렉트 URL
                        .failureUrl("/login?error") // 실패 시 리다이렉트 URL
                )

                // OAuth2 Login 추가
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2Service)) // 사용자 정보를 처리할 서비스 지정
                        .successHandler(successHandler) // OAuth2 성공 시 JWT 발급
                )
                .httpBasic(Customizer.withDefaults())
                .logout((logout) -> logout
                        .logoutUrl("/logout") // 로그아웃
                        .logoutSuccessUrl("/"));

        // JWT 필터 등록
        //필터는 로그인된 사용자인지를 검증하는 역할
        //jwT는 세션을 사용하지 않기 때문에 매번 토큰을 통해 사용자를 검증
        http.addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

}
