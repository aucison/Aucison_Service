package com.example.aucison_service.security;

import com.example.Aucison_Member_Service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final AuthService authService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;
    private final ObjectPostProcessor<Object> objectPostProcessor;

    @Bean // 권한 관련
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(c->c.disable());

//        Deprecated.....
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.formLogin().disable(); // 폼 로그인 비활성화
//        http.httpBasic().disable(); // HTTP 기본 인증 비활성화

        http.authorizeHttpRequests((authz)-> authz
                .requestMatchers(new AntPathRequestMatcher("/**")
                        , new AntPathRequestMatcher("/member-service/**")
                        , new AntPathRequestMatcher("/actuator/**")).permitAll()
                .requestMatchers(new IpAddressMatcher("127.0.0.1")).permitAll()
                .anyRequest().authenticated());

        // apigateway-service에서 토큰 검증 후 들어오기 때문에 필터 걸 필요 없음
        //http.addFilter(getAuthenticationFilter());

        http.headers(h->h.frameOptions(f->f.disable()).disable());
        return http.build();
    }

//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(authService).passwordEncoder(bCryptPasswordEncoder);
//        return auth.build();
//    }
//
//    private AuthFilter getAuthenticationFilter() throws Exception {
//        AuthFilter authenticationFilter = new AuthFilter(authService, env);
//        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
//        authenticationFilter.setAuthenticationManager(authenticationManager(builder));
//        return authenticationFilter;
//    }
}
