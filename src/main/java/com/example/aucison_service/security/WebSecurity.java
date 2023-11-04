package com.example.aucison_service.security;


import com.example.aucison_service.service.member.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;


@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final GoogleAuthService googleAuthService;

    // 생성자를 통한 의존성 주입
    @Autowired
    public WebSecurity(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }



    // Spring Security Filter Chain 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // X-Frame-Options 헤더 비활성화
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/auth/**", HttpMethod.GET.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/oauth2/**", HttpMethod.GET.name())).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint
                                                .userService(this.oauth2UserService())
                                )
                                .successHandler(this::successHandler));


        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oAuth2User = super.loadUser(userRequest);

                // Google에서 받아온 사용자 정보를 가져옵니다.
                String idTokenValue = userRequest.getAccessToken().getTokenValue();
                // 여기에서 idTokenValue를 사용하여 GoogleAuthService를 통해 사용자를 인증하고
                // 데이터베이스에 저장할 수 있습니다.
                // 예를 들면:
                // googleAuthService.processGoogleUser(idTokenValue);

                // 인증 후 OAuth2User 반환
                return oAuth2User;
            }
        };
    }

    private void successHandler(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        // 로그인 성공 후 처리 로직
        response.sendRedirect("/home"); // 로그인 성공 후 리디렉션 할 페이지
    }

    // CORS 정책 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // 모든 도메인 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class WebSecurity {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    private static final String[] WHITE_LIST = {
//            "/users/**"
//    };
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable());
//        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // X-Frame-Options 헤더를 비활성화
//        /*
//        authorizeRequest는 deprecated로 더이상 권장되지 않기 때문에 이제는 authorizeHttpRequests()를 대신 사용하는 것을 권장됨
//
//        http
//                .authorizeRequests(authorizeRequests -> {
//                    for (String pattern : WHITE_LIST) {
//                        authorizeRequests.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll(); // 화이트리스트에 있는 URL들에 대한 접근을 허용
//                    }
//                    authorizeRequests
//                            .requestMatchers(PathRequest.toH2Console()).permitAll() // H2 데이터베이스 콘솔에 대한 접근을 허용
//                            .requestMatchers(new IpAddressMatcher("192.168.0.130")).permitAll(); // 특정 IP 주소에서의 요청을 허용
//                })
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 추가합니다.
//
//         */
//        http
//                .authorizeHttpRequests(authorizeRequests -> {
//                    for (String pattern : WHITE_LIST) {
//                        authorizeRequests.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll(); // 화이트리스트에 있는 URL들에 대한 접근을 허용
//                    }
//                    authorizeRequests
//                            .requestMatchers(PathRequest.toH2Console()).permitAll() // H2 데이터베이스 콘솔에 대한 접근을 허용
//                            .anyRequest().permitAll();  // 이 줄을 추가하여 모든 요청을 허용
//                            //.requestMatchers(new IpAddressMatcher("192.168.0.130")).permitAll(); // 특정 IP 주소에서의 요청을 허용
//
//                })
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 추가합니다.
//
//
//        return http.build();
//
//
//    }
//}