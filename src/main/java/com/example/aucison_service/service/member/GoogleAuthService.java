package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.auth.AuthResponseDto;
import com.example.aucison_service.enums.Role;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import com.example.aucison_service.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class GoogleAuthService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
//    private String redirectUri;


    //private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public GoogleAuthService(MembersRepository membersRepository, RestTemplate restTemplate, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        //this.webClient = webClientBuilder.build();
        this.membersRepository = membersRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    //1. 구글 인증 url 얻기
//    public String createGoogleAuthorizationURL() {
//        logger.info("createGoogleAuthorizationURL");
//        // Build the Google OAuth URL
//        return "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + clientId +
//                "&redirect_uri=" + redirectUri +
//                "&response_type=code" +
//                "&scope=openid%20email%20profile" +
//                "&access_type=offline";
//    }

//    //2. 구글 액세스 토큰 얻기
//    public GoogleTokenResponseDto getGoogleAccessToken(String code) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("code", code);
//        params.add("redirect_uri", redirectUri);
//        params.add("grant_type", "authorization_code");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<GoogleTokenResponseDto> response = restTemplate.postForEntity(
//                "https://www.googleapis.com/oauth2/v4/token", request, GoogleTokenResponseDto.class);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException("Failed to retrieve access token");
//        }
//
//        return response.getBody();
//    }

    //3. 사용자 인증 및 JWT 토큰 생성
    // 사용자 인증 및 JWT 토큰 생성 메소드는 다음과 같이 수정합니다.
    @Transactional
    public AuthResponseDto authenticateUserAndGetJwtToken(String accessToken) {
        // Access Token을 사용하여 Google로부터 사용자 정보를 가져옵니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
            logger.error("Failed to retrieve user info from Google");
            throw new RuntimeException("Failed to retrieve user info");
        }

        // Google로부터 받은 사용자 정보를 사용하여 사용자가 시스템에 존재하는지 확인하고, JWT 토큰을 생성합니다.
        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // 사용자가 데이터베이스에 존재하는지 확인합니다.
        MembersEntity member = membersRepository.findByEmail(email);

        boolean isNewUser = false;
        MembersEntity user;
        if (member != null) {
            // 이미 존재하는 사용자인 경우, 정보 업데이트
            user = member;
            user.updateFromGoogle(userInfo); // 사용자 정보 업데이트 메소드
        } else {
            isNewUser = true; // 새 사용자임을 표시
            // 새 사용자인 경우, 회원가입 처리
            user = MembersEntity.builder()
                    .email(email)
                    .name(name)
                    .nickname(name + "_nickname") // 닉네임 설정 예시
                    .role(Role.ROLE_CUSTOMER) // 기본 역할 설정
                    .build();

            membersRepository.save(user); // 새 사용자 정보 저장
        }

        // JWT 토큰을 생성합니다.
        String jwtToken = jwtTokenProvider.createToken(email, Role.ROLE_CUSTOMER);
        // JWT 토큰 값을 로그로 출력
//        logger.info("Generated JWT Token: {}", jwtToken);



        logger.info(" 새로운 접속자 로그인 완료: " + member.getEmail());

        // 생성된 JWT 토큰을 AuthResponseDto에 담아 반환합니다.
        return AuthResponseDto.builder()
                .jwtToken(jwtToken)
                .isNewUser(isNewUser)
                .build();
    }



//    public AuthResponseDto authenticateUserAndGetJwtToken(GoogleTokenResponseDto tokenResponse) {
//        // Google 사용자 정보 가져오기
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(tokenResponse.getAccessToken());
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
//                "https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});
//
//        if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
//            throw new RuntimeException("Failed to retrieve user info");
//        }
//
//        Map<String, Object> userInfo = userInfoResponse.getBody();
//        String email = (String) userInfo.get("email");
//
//        // JWT 토큰 생성
//        String jwtToken = jwtTokenProvider.createToken(email, Role.ROLE_USER);
//
//        // 사용자 정보 및 JWT 토큰 반환
//        AuthResponseDto response = new AuthResponseDto();
//        response.setJwtToken(jwtToken);
//        // 여기에서 추가적으로 사용자의 다른 정보도 설정할 수 있습니다.
//
//        return response;
//    }


//    @Transactional
//    public String createGoogleAuthorizationURL() {
//
//        logger.info("createGoogleAuthorizationURL");
//        // Build the Google OAuth URL
//        return "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + clientId +
//                "&redirect_uri=" + redirectUri +
//                "&response_type=code" +
//                "&scope=openid%20email%20profile" +
//                "&access_type=offline";
//    }

    //임시 동기 변경
//    public Mono<OAuth2AuthenticationToken> exchangeCodeForToken(String code) {
//        GoogleTokenRequestDto tokenRequest = GoogleTokenRequestDto.builder()
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .code(code)
//                .redirectUri(redirectUri)
//                .grantType("authorization_code")
//                .build();
//
//        return webClient.post()
//                .uri("https://oauth2.googleapis.com/token")
//                .bodyValue(tokenRequest)
//                .retrieve()
//                .onStatus(HttpStatus::isError, response -> Mono.error(new ResponseStatusException(response.statusCode(), "Error while exchanging code for token")))
//                .bodyToMono(GoogleTokenResponseDto.class)
//                .map(tokenResponse -> {
//                    String idToken = tokenResponse.getIdToken();
//                    GoogleIdToken.Payload payload = decodeGoogleIdToken(idToken);
//
//                    // UserDetails 대신 OAuth2User를 사용합니다.
//                    OAuth2User oAuth2User = createOAuth2User(payload);
//
//                    // OAuth2User를 OAuth2AuthenticationToken에 넘깁니다.
//                    return new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "google");
//                });
//    }

//    @Transactional
//    public OAuth2AuthenticationToken exchangeCodeForToken(String code) {
//        logger.info("exchangeCodeForToken - Received code: {}", code); // 여기에 코드 값을 로깅합니다.
//
//        // 요청 정보를 로깅합니다. 여기서는 단순히 code 값을 로깅하지만,
//        // 실제로는 요청 IP, 헤더 등 추가 정보를 로깅할 수 있습니다.
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String ipAddress = request.getRemoteAddr();
//            logger.info("Request info - IP: {}, Code: {}", ipAddress, code);
//        } catch (Exception e) {
//            logger.error("Failed to log request info", e);
//        }
//
//        // GoogleTokenRequestDto 객체를 생성합니다.
//        GoogleTokenRequestDto tokenRequest = GoogleTokenRequestDto.builder()
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .code(code)
//                .redirectUri(redirectUri)
//                .grantType("authorization_code") // 이 필드는 "authorization_code"로 설정해야 합니다.
//                .build();
//
//        // RestTemplate을 사용하여 Google에 토큰 요청을 보냅니다.
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(tokenRequest.toFormUrlEncoded(), headers);
//
//        ResponseEntity<GoogleTokenResponseDto> response = restTemplate.postForEntity(
//                "https://oauth2.googleapis.com/token", requestEntity, GoogleTokenResponseDto.class);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            logger.error("Non-successful response received: {}", response.getStatusCode());
//            throw new ResponseStatusException(
//                    response.getStatusCode(), "Error while exchanging code for token");
//        }
//
//        // 응답을 로그에 기록하고 필요한 정보를 추출합니다.
//        GoogleTokenResponseDto tokenResponse = response.getBody();
//        if (tokenResponse != null) {
//            logger.info("exchangeCodeForToken - Token response: {}", tokenResponse);
//            String idToken = tokenResponse.getIdToken();
//            if (idToken != null) {
//                logger.info("exchangeCodeForToken - ID Token is present");
//            } else {
//                logger.error("exchangeCodeForToken - ID Token is null in the response");
//                throw new IllegalArgumentException("ID 토큰이 null입니다.");
//            }
//        } else {
//            logger.error("exchangeCodeForToken - Response body is null");
//            throw new IllegalStateException("Response body is null");
//        }
//
//        // Google의 ID 토큰을 디코딩하여 사용자 정보를 얻습니다.
//        GoogleIdToken.Payload payload = decodeGoogleIdToken(tokenResponse.getIdToken());
//
//        // OAuth2 사용자를 생성합니다.
//        OAuth2User oAuth2User = createOAuth2User(payload);
//
//        // OAuth2 인증 토큰을 생성하고 반환합니다.
//        return new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "google");
//    }
//
//
//    //임시 동기 변경
////    public Mono<MembersEntity> registerOrLoginUser(OAuth2AuthenticationToken authentication) {
////        // Extract email from authentication
////        String email = authentication.getName(); // Assuming email is the name in UserDetails
////
////        // Find existing user by email or create a new one
////        MembersEntity member = membersRepository.findByEmail(email)
////                .orElseGet(() -> {
////                    // Extract more information from authentication if necessary
////                    String name = authentication.getPrincipal().getAttribute("name");
////                    // Create a new user entity
////                    MembersEntity newMember = new MembersEntity(email, name, null, Role.ROLE_CUSTOMER);
////                    // Save the new member to the database
////                    return membersRepository.save(newMember);
////                });
////
////        return Mono.just(member);
////    }
////
////    private UserDetails createUserDetails(GoogleIdToken.Payload payload) {
////        // 여기서는 예시로, 단순한 UserDetails의 구현체를 사용하고 있음
////        // 실제로는 더 복잡한 로직이 필요할 수 있음
////        return User.withUsername(payload.getEmail())
////                .password("")
////                .authorities(Collections.emptyList())
////                .build();
////    }
//
//    @Transactional
//    public MembersEntity registerOrLoginUser(OAuth2AuthenticationToken authentication) {
//        logger.info("registerOrLoginUser1");
//        String email = authentication.getName(); // Assuming email is the name in UserDetails
//        logger.info("registerOrLoginUser2");
//        try {
//            Optional<MembersEntity> existingMember = membersRepository.findByEmail(email);
//            if (existingMember.isPresent()) {
//                logger.info("registerOrLoginUser3-1");
//                return existingMember.get();
//            } else {
//                logger.info("registerOrLoginUser3-2");
//                String name = authentication.getPrincipal().getAttribute("name");
//                // nickname을 포함하여 모든 필수 필드를 채워줍니다.
//                MembersEntity newMember = new MembersEntity(email, name, name + "_nickname", Role.ROLE_CUSTOMER);
//                return membersRepository.save(newMember); // 이 부분에서 데이터베이스 저장을 시도합니다.
//            }
//        } catch (Exception e) {
//            // 여기서 발생하는 예외를 로그에 기록합니다.
//            throw new IllegalStateException("Failed to register or login user", e);
//        }
//    }
//
//
//    private GoogleIdToken.Payload decodeGoogleIdToken(String idTokenString) {
//        if (idTokenString == null) {
//            throw new IllegalArgumentException("ID 토큰이 null입니다.");
//        }
//        // GoogleIdTokenVerifier를 사용하여 idToken을 검증하고 파싱
//        // 실제 환경에서는 HTTPS를 통한 검증이 필요
//        logger.info("decodeGoogleIdToken1");
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
//                .setAudience(Collections.singletonList(clientId))
//                .setIssuer("https://accounts.google.com")
//                .build();
//
//        GoogleIdToken idToken;
//        try {
//            logger.info("decodeGoogleIdToken2");
//            idToken = verifier.verify(idTokenString);
//        } catch (Exception e) {
//            logger.error("ID Token verification failed: ", e);
//            throw new IllegalArgumentException("ID Token cannot be verified", e);
//        }
//
//        if (idToken == null) {
//            throw new IllegalArgumentException("ID Token is invalid");
//        }
//
//        return idToken.getPayload();
//    }
//
//
//    private OAuth2User createOAuth2User(GoogleIdToken.Payload payload) {
//        // 여기서는 예시로, 단순한 OAuth2User의 구현체를 사용하고 있음
//        // 실제로는 더 복잡한 로직이 필요할 수 있음
//        logger.info("createOAuth2User1");
//        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSOTMER"));
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("sub", payload.getSubject()); // Google 사용자 고유 ID
//        attributes.put("email", payload.getEmail());
//        attributes.put("name", payload.get("name"));
//        logger.info("createOAuth2User2");
//        return new DefaultOAuth2User(authorities, attributes, "email");
//    }
}




