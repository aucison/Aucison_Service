package com.example.aucison_service.service.member;

import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.auth.GoogleTokenRequestDto;
import com.example.aucison_service.dto.auth.GoogleTokenResponseDto;
import com.example.aucison_service.enums.Role;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
public class GoogleAuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;


    //private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final MembersRepository membersRepository;

    @Autowired
    public GoogleAuthService(MembersRepository membersRepository, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        //this.webClient = webClientBuilder.build();
        this.membersRepository = membersRepository;
    }

    @Transactional
    public String createGoogleAuthorizationURL() {

        logger.info("createGoogleAuthorizationURL");
        // Build the Google OAuth URL
        return "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&access_type=offline";
    }

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

    @Transactional
    public OAuth2AuthenticationToken exchangeCodeForToken(String code) {
        logger.info("exchangeCodeForToken1");

        GoogleTokenRequestDto tokenRequest = GoogleTokenRequestDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .redirectUri(redirectUri)
                .grantType("authorization_code") // 이 부분을 추가합니다.
                .build();

        logger.info("exchangeCodeForToken2");
        ResponseEntity<GoogleTokenResponseDto> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", tokenRequest, GoogleTokenResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(
                    response.getStatusCode(), "Error while exchanging code for token");
        }
        logger.info("exchangeCodeForToken3");
        GoogleTokenResponseDto tokenResponse = response.getBody();
        String idToken = tokenResponse.getIdToken();
        GoogleIdToken.Payload payload = decodeGoogleIdToken(idToken);

        OAuth2User oAuth2User = createOAuth2User(payload);

        return new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "google");
    }


    //임시 동기 변경
//    public Mono<MembersEntity> registerOrLoginUser(OAuth2AuthenticationToken authentication) {
//        // Extract email from authentication
//        String email = authentication.getName(); // Assuming email is the name in UserDetails
//
//        // Find existing user by email or create a new one
//        MembersEntity member = membersRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    // Extract more information from authentication if necessary
//                    String name = authentication.getPrincipal().getAttribute("name");
//                    // Create a new user entity
//                    MembersEntity newMember = new MembersEntity(email, name, null, Role.ROLE_CUSTOMER);
//                    // Save the new member to the database
//                    return membersRepository.save(newMember);
//                });
//
//        return Mono.just(member);
//    }
//
//    private UserDetails createUserDetails(GoogleIdToken.Payload payload) {
//        // 여기서는 예시로, 단순한 UserDetails의 구현체를 사용하고 있음
//        // 실제로는 더 복잡한 로직이 필요할 수 있음
//        return User.withUsername(payload.getEmail())
//                .password("")
//                .authorities(Collections.emptyList())
//                .build();
//    }

    @Transactional
    public MembersEntity registerOrLoginUser(OAuth2AuthenticationToken authentication) {
        logger.info("registerOrLoginUser1");
        String email = authentication.getName(); // Assuming email is the name in UserDetails
        logger.info("registerOrLoginUser2");
        try {
            Optional<MembersEntity> existingMember = membersRepository.findByEmail(email);
            if (existingMember.isPresent()) {
                logger.info("registerOrLoginUser3-1");
                return existingMember.get();
            } else {
                logger.info("registerOrLoginUser3-2");
                String name = authentication.getPrincipal().getAttribute("name");
                // nickname을 포함하여 모든 필수 필드를 채워줍니다.
                MembersEntity newMember = new MembersEntity(email, name, name + "_nickname", Role.ROLE_CUSTOMER);
                return membersRepository.save(newMember); // 이 부분에서 데이터베이스 저장을 시도합니다.
            }
        } catch (Exception e) {
            // 여기서 발생하는 예외를 로그에 기록합니다.
            throw new IllegalStateException("Failed to register or login user", e);
        }
    }


    private GoogleIdToken.Payload decodeGoogleIdToken(String idTokenString) {
        // GoogleIdTokenVerifier를 사용하여 idToken을 검증하고 파싱
        // 실제 환경에서는 HTTPS를 통한 검증이 필요
        logger.info("decodeGoogleIdToken1");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .setIssuer("https://accounts.google.com")
                .build();

        GoogleIdToken idToken;
        try {
            logger.info("decodeGoogleIdToken2");
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new IllegalArgumentException("ID Token cannot be verified", e);
        }

        if (idToken == null) {
            throw new IllegalArgumentException("ID Token is invalid");
        }

        return idToken.getPayload();
    }


    private OAuth2User createOAuth2User(GoogleIdToken.Payload payload) {
        // 여기서는 예시로, 단순한 OAuth2User의 구현체를 사용하고 있음
        // 실제로는 더 복잡한 로직이 필요할 수 있음
        logger.info("createOAuth2User1");
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSOTMER"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", payload.getSubject()); // Google 사용자 고유 ID
        attributes.put("email", payload.getEmail());
        attributes.put("name", payload.get("name"));
        logger.info("createOAuth2User2");
        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}




