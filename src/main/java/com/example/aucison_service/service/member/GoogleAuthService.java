package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.auth.GoogleTokenRequestDto;
import com.example.aucison_service.dto.auth.GoogleTokenResponseDto;
import com.example.aucison_service.enums.Role;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;


    private final WebClient webClient;
    private final MembersRepository membersRepository;

    @Autowired
    public GoogleAuthService(WebClient.Builder webClientBuilder, MembersRepository membersRepository) {
        this.webClient = webClientBuilder.build();
        this.membersRepository = membersRepository;
    }


    public String createGoogleAuthorizationURL() {
        // Build the Google OAuth URL
        return "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&access_type=offline";
    }

    public Mono<OAuth2AuthenticationToken> exchangeCodeForToken(String code) {
        GoogleTokenRequestDto tokenRequest = GoogleTokenRequestDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .bodyValue(tokenRequest)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new ResponseStatusException(response.statusCode(), "Error while exchanging code for token")))
                .bodyToMono(GoogleTokenResponseDto.class)
                .map(tokenResponse -> {
                    String idToken = tokenResponse.getIdToken();
                    GoogleIdToken.Payload payload = decodeGoogleIdToken(idToken);

                    // UserDetails 대신 OAuth2User를 사용합니다.
                    OAuth2User oAuth2User = createOAuth2User(payload);

                    // OAuth2User를 OAuth2AuthenticationToken에 넘깁니다.
                    return new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "google");
                });
    }


    public Mono<MembersEntity> registerOrLoginUser(OAuth2AuthenticationToken authentication) {
        // Extract email from authentication
        String email = authentication.getName(); // Assuming email is the name in UserDetails

        // Find existing user by email or create a new one
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Extract more information from authentication if necessary
                    String name = authentication.getPrincipal().getAttribute("name");
                    // Create a new user entity
                    MembersEntity newMember = new MembersEntity(email, name, null, Role.ROLE_CUSTOMER);
                    // Save the new member to the database
                    return membersRepository.save(newMember);
                });

        return Mono.just(member);
    }

    private UserDetails createUserDetails(GoogleIdToken.Payload payload) {
        // 여기서는 예시로, 단순한 UserDetails의 구현체를 사용하고 있음
        // 실제로는 더 복잡한 로직이 필요할 수 있음
        return User.withUsername(payload.getEmail())
                .password("")
                .authorities(Collections.emptyList())
                .build();
    }

    private GoogleIdToken.Payload decodeGoogleIdToken(String idTokenString) {
        // GoogleIdTokenVerifier를 사용하여 idToken을 검증하고 파싱
        // 실제 환경에서는 HTTPS를 통한 검증이 필요
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                // 이 부분은 실제로 필요한 검증을 추가해야 함
                .build();

        GoogleIdToken idToken;
        try {
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
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSOTMER"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", payload.getSubject()); // Google 사용자 고유 ID
        attributes.put("email", payload.getEmail());
        attributes.put("name", payload.get("name"));

        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}




