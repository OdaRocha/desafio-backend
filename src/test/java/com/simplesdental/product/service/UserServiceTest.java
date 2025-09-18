package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.Role;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.exception.EmailAlreadyExistsException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.mapper.UserMapper;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import com.simplesdental.product.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserService userService;

    @Test
    void deveRetornarUsuarioQuandoEncontrarPorId() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setName("Teste");
        user.setEmail("teste@email.com");

        UserDTO expectedDto = UserMapper.toDTO(user);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(expectedDto.getId(), result.get().getId());
        assertEquals(expectedDto.getName(), result.get().getName());
        assertEquals(expectedDto.getEmail(), result.get().getEmail());
        verify(userRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarUsuarioPorId() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(id);

        assertFalse(result.isPresent());
        verify(userRepository).findById(id);
    }

    @Test
    void deveRetornarUsuarioQuandoEncontrarPorEmail() {
        String email = "teste@email.com";
        User user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail(email);

        UserDTO expectedDto = UserMapper.toDTO(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(expectedDto.getId(), result.get().getId());
        assertEquals(expectedDto.getName(), result.get().getName());
        assertEquals(expectedDto.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarUsuarioPorEmail() {
        String email = "naoexiste@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findByEmail(email);

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void deveLancarValidationExceptionQuandoValidacaoFalhar() {
        UserDTO dto = new UserDTO(null, null, "Teste", "teste@email.com", null);

        ValidationResult validationResult = mock(ValidationResult.class);

        when(validator.validate(dto)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.save(dto);
        });

        assertNotNull(exception.getErrors());
        verify(validator).validate(dto);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deveLancarEmailAlreadyExistsExceptionQuandoEmailJaExistir() {
        UserDTO dto = new UserDTO(1L, null, "Novo Usuário", "existente@email.com", Role.USER);

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("existente@email.com");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        when(validator.validate(dto)).thenReturn(validationResult);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(existingUser));

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.save(dto);
        });

        assertEquals("E-mail já cadastrado.", exception.getMessage());
        verify(validator).validate(dto);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deveSalvarUsuarioComSucesso() {
        UserDTO dto = new UserDTO(1L, null, "Novo Usuário", "novo@email.com", Role.USER);

        User savedEntity = UserMapper.toEntity(dto);
        savedEntity.setId(1L);

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        when(validator.validate(dto)).thenReturn(validationResult);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        UserDTO result = userService.save(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());

        verify(validator).validate(dto);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository).save(any(User.class));
    }

}
