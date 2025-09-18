package com.simplesdental.product.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdental.product.dto.AuthContextResponse;
import com.simplesdental.product.dto.Role;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisClientTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisClient redisClient;

    AuthContextResponse authContext;

    @BeforeEach
    void setup() {
        authContext = new AuthContextResponse(1L, "", Role.USER);
    }

    @Test
    void deveRealizarLoginCorretamente() throws JsonProcessingException {
        String jsonValue = "{\"user\":\"test\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(authContext)).thenReturn(jsonValue);

        assertDoesNotThrow(() -> redisClient.login(authContext));

        verify(objectMapper, times(1)).writeValueAsString(authContext);
        verify(valueOperations, times(1)).set(
                RedisClient.KEY,
                jsonValue,
                Duration.ofSeconds(RedisClient.DEFAULT_EXPIRATION_SECONDS)
        );
    }

    @Test
    void deveFalharAoTentarFazerLogin() throws JsonProcessingException {

        JsonProcessingException jsonException = new JsonProcessingException("Erro de serialização") {
        };
        when(objectMapper.writeValueAsString(authContext)).thenThrow(jsonException);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> redisClient.login(authContext));

        assertEquals("Erro ao serializar AuthContextResponse", exception.getMessage());
        assertEquals(jsonException, exception.getCause());

        verify(objectMapper, times(1)).writeValueAsString(authContext);
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void deveObterContextoDeAutenticacaoCorretamente() throws JsonProcessingException {
        String jsonValue = "{\"user\":\"test\"}";
        when(valueOperations.get(RedisClient.KEY)).thenReturn(jsonValue);
        when(objectMapper.readValue(jsonValue, AuthContextResponse.class)).thenReturn(authContext);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        AuthContextResponse result = redisClient.getAuthContext();

        assertNotNull(result);
        assertEquals(authContext, result);

        verify(valueOperations, times(1)).get(RedisClient.KEY);
        verify(objectMapper, times(1)).readValue(jsonValue, AuthContextResponse.class);
    }

    @Test
    void deveFalharQuandoContextoNaoExiste() {
        when(valueOperations.get(RedisClient.KEY)).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> redisClient.getAuthContext());

        assertEquals("Necessario realizar login!", exception.getMessage());

        verify(valueOperations, times(1)).get(RedisClient.KEY);
    }

    @Test
    void deveFalharAoDesserializarContexto() throws JsonProcessingException {
        String jsonValue = "{\"invalid\":\"json\"}";
        JsonProcessingException jsonException = new JsonProcessingException("Erro de desserialização") {
        };
        when(valueOperations.get(RedisClient.KEY)).thenReturn(jsonValue);
        when(objectMapper.readValue(jsonValue, AuthContextResponse.class)).thenThrow(jsonException);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> redisClient.getAuthContext());

        assertEquals("Erro ao desserializar AuthContextResponse", exception.getMessage());
        assertEquals(jsonException, exception.getCause());

        verify(valueOperations, times(1)).get(RedisClient.KEY);
        verify(objectMapper, times(1)).readValue(jsonValue, AuthContextResponse.class);
    }

    @Test
    void deveDeletarValorCorretamente() {
        redisClient.deleteValue();

        verify(redisTemplate, times(1)).delete(RedisClient.KEY);
    }
}
