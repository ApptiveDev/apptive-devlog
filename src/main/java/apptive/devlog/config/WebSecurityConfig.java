package apptive.devlog.config;

import apptive.devlog.member.jwt.CustomLogoutFilter;
import apptive.devlog.member.jwt.JWTFilter;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.jwt.LoginFilter;
import apptive.devlog.member.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors((cors) -> cors
                .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration con = new CorsConfiguration();
                        con.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트엔드 주소 허용
                        con.setAllowedMethods(Collections.singletonList("*")); // 모든 GET,POST .. 요청 허용
                        con.setAllowCredentials(true); //쿠키 포함 요청 허용
                        con.setAllowedHeaders(Collections.singletonList("*")); // 클라이언트가 요청할 수 있는 헤더

                        con.setExposedHeaders(Arrays.asList("Set-Cookie", "access"));
                         //서버가 응답한 헤더중 클라이언트가 접근할 수 있는 헤더
                        //Set-Cookie는 브라우저가 쿠키 저장소에 자동으로 저장하기 위한 용도
                        return con;
                    }
                }));

        http.csrf((auth) -> auth.disable()); //jwt방식을 사용하기 때문에 csrf 공격에 취약하지 않음
        http.formLogin((auth)-> auth.disable());
        http.httpBasic((auth)->auth.disable());
        // jwt방식을 사용할때는 위 3가지를 disable 시켜줘야한다.

        http.authorizeHttpRequests((auth)->
                auth.requestMatchers(permitPaths).permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/*", "/users/*/posts/**").permitAll()
                        .requestMatchers("/home").hasRole("MEMBER")//ROLE_MEMBER
                        .anyRequest().authenticated());

        http.sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정하는 것이 중요하다.

        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class); //로그인 필터 전에 등록

        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,refreshRepository),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository),
                LogoutFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final String[] permitPaths = {"/login", "/users", "/reissue", "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            };


}
