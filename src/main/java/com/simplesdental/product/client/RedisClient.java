package com.simplesdental.product.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdental.product.dto.AuthContextResponse;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisClient {

    static final String KEY = "CONTEXT_KEY";
    static final long DEFAULT_EXPIRATION_SECONDS = 50;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void login(AuthContextResponse context) {
        try {
            String value = objectMapper.writeValueAsString(context);
            setValue(KEY, value, DEFAULT_EXPIRATION_SECONDS);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Erro ao serializar AuthContextResponse", e);
        }
    }

    public AuthContextResponse getAuthContext() {
        try {
            String context = getValue(KEY);

            if(context != null) {
                return objectMapper.readValue(context, AuthContextResponse.class);
            }

        } catch (JsonProcessingException e) {
            throw new BusinessException("Erro ao desserializar AuthContextResponse", e);
        }

        throw new UnauthorizedException("Necessario realizar login!");
    }

    public void setValue(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, java.time.Duration.ofSeconds(seconds));
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue() {
        redisTemplate.delete(KEY);
    }

}
