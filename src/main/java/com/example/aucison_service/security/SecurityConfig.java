package com.example.aucison_service.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    // JWT 인증 필터 빈 정의
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository())
                        .authorizedClientService(authorizedClientService())
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(new AntPathRequestMatcher("/need/auth/**")).authenticated()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // 구글 OAuth2 클라이언트 등록 정보 설정
        ClientRegistration clientRegistration = ClientRegistration
                .withRegistrationId("google") // 등록 ID
                .clientId(googleClientId) // 구글 클라이언트 ID
                .clientSecret(googleClientSecret) // 구글 클라이언트 비밀번호
                .scope(Arrays.asList("openid", "profile", "email")) // 범위
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth") // 인증 URI
                .tokenUri("https://www.googleapis.com/oauth2/v4/token") // 토큰 URI
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo") // 사용자 정보 URI
                .userNameAttributeName(IdTokenClaimNames.SUB) // 사용자 이름 속성 이름
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs") // JWK 세트 URI
                .clientName("Google") // 클라이언트 이름
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 인가 부여 타입
                .redirectUri(googleRedirectUri) // 리디렉션 URI
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        // OAuth2 클라이언트 서비스 설정
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
}
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final GoogleAuthService googleAuthService;
//
//    // 생성자를 통한 의존성 주입
//    @Autowired
//    public SecurityConfig(GoogleAuthService googleAuthService) {
//        this.googleAuthService = googleAuthService;
//    }
//
//
//
//    // Spring Security Filter Chain 설정
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable());
//        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // X-Frame-Options 헤더 비활성화
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(new AntPathRequestMatcher("/auth/**", HttpMethod.GET.name())).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/oauth2/**", HttpMethod.GET.name())).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2Login ->
//                        oauth2Login
//                                .userInfoEndpoint(userInfoEndpoint ->
//                                        userInfoEndpoint
//                                                .userService(this.oauth2UserService())
//                                )
//                                .successHandler(this::successHandler));
//
//
//        return http.build();
//    }
//
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
//        return new DefaultOAuth2UserService() {
//            @Override
//            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//                OAuth2User oAuth2User = super.loadUser(userRequest);
//
//                // Google에서 받아온 사용자 정보를 가져옵니다.
//                String idTokenValue = userRequest.getAccessToken().getTokenValue();
//                // 여기에서 idTokenValue를 사용하여 GoogleAuthService를 통해 사용자를 인증하고
//                // 데이터베이스에 저장할 수 있습니다.
//                // 예를 들면:
//                // googleAuthService.processGoogleUser(idTokenValue);
//
//                // 인증 후 OAuth2User 반환
//                return oAuth2User;
//            }
//        };
//    }
//
//    private void successHandler(HttpServletRequest request,
//                                HttpServletResponse response,
//                                Authentication authentication) throws IOException {
//        // 로그인 성공 후 처리 로직
//        response.sendRedirect("/home"); // 로그인 성공 후 리디렉션 할 페이지
//    }
//
//    // CORS 정책 설정
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*")); // 모든 도메인 허용
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token"));
//        configuration.setExposedHeaders(List.of("x-auth-token"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}


//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
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