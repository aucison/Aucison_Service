package com.example.aucison_service.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories    //Spring Data Redis 저장소를 활성화
public class RedisConfig {

    //Redis 서버의 호스트 주소를 저장
    //application properties 또는 YAML 파일에서 ${spring.redis.host} 키로 설정
    @Value("${spring.redis.host}")
    private String host;

    // Redis 서버의 포트 번호를 저장
    //application properties 또는 YAML 파일에서 ${spring.redis.port} 키로 설정
    @Value("${spring.redis.port}")
    private int port;


    //LettuceConnectionFactory를 사용하여 Redis에 연결하는 RedisConnectionFactory를 생성하고 반환
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    //RedisTemplate를 생성하고 구성합니다.
    //RedisTemplate는 Redis 데이터베이스와 상호 작용하는 데 사용되는 일반적인 템플릿
    //키와 값의 직렬화 방식을 StringRedisSerializer로 설정
    //Redis에 저장되는 데이터를 읽고 쓰기 위해 사용됩니다.
    // 키는 문자열 타입이고, 값은 Object 타입으로 처리될 수 있어 다양한 타입의 값(객체, 문자열 등)을 저장할 수 있습니다.
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //setConnectionFactory: Redis와의 연결 관리, edisTemplate는 Redis 서버에 연결하여 데이터를 읽고 쓸 수 있음
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // Redis의 키를 직렬화하는 방법을 설정, 여기서는 문자열 키를 사용하므로 StringRedisSerializer를 사용하여 키를 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //Redis의 값을 직렬화하는 방법을 설정, 값 역시 문자열로 처리하려는 경우 StringRedisSerializer를 사용할 수 있음
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }


    // 문자열 키와 값을 처리하는 데 특화된 StringRedisTemplate를 생성하고 반환합니다.
    // 이 템플릿은 StringRedisSerializer로 키와 값의 직렬화 방식을 설정
    //RedisTemplate의 특수한 형태로, 키와 값이 모두 문자열 타입으로 처리되는 경우 사용
    //키와 값이 모두 문자열 타입인 경우에 이 템플릿을 사용합니다. 간단한 문자열 데이터를 저장하고 검색하는 데 유용
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        //키를 직렬화하는 방법을 설정, StringRedisTemplate는 키와 값이 모두 문자열이므로, 키를 직렬화하기 위해 StringRedisSerializer를 사용
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        //값을 직렬화하는 방법을 설정합니다. 여기서도 값이 문자열이므로, 값의 직렬화를 위해 StringRedisSerializer를 사용
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}
