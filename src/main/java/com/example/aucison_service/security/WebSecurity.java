package com.example.aucison_service.security;


import com.example.aucison_service.service.member.AuthService;
import com.example.aucison_service.service.member.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;




@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] WHITE_LIST = {
            "/users/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // X-Frame-Options 헤더를 비활성화
        /*
        authorizeRequest는 deprecated로 더이상 권장되지 않기 때문에 이제는 authorizeHttpRequests()를 대신 사용하는 것을 권장됨

        http
                .authorizeRequests(authorizeRequests -> {
                    for (String pattern : WHITE_LIST) {
                        authorizeRequests.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll(); // 화이트리스트에 있는 URL들에 대한 접근을 허용
                    }
                    authorizeRequests
                            .requestMatchers(PathRequest.toH2Console()).permitAll() // H2 데이터베이스 콘솔에 대한 접근을 허용
                            .requestMatchers(new IpAddressMatcher("192.168.0.130")).permitAll(); // 특정 IP 주소에서의 요청을 허용
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 추가합니다.

         */
        http
                .authorizeHttpRequests(authorizeRequests -> {
                    for (String pattern : WHITE_LIST) {
                        authorizeRequests.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll(); // 화이트리스트에 있는 URL들에 대한 접근을 허용
                    }
                    authorizeRequests
                            .requestMatchers(PathRequest.toH2Console()).permitAll() // H2 데이터베이스 콘솔에 대한 접근을 허용
                            .anyRequest().permitAll();  // 이 줄을 추가하여 모든 요청을 허용
                            //.requestMatchers(new IpAddressMatcher("192.168.0.130")).permitAll(); // 특정 IP 주소에서의 요청을 허용
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 추가합니다.


        return http.build();


    }
}