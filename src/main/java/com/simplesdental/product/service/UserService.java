package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.exception.EmailAlreadyExistsException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.mapper.UserMapper;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import com.simplesdental.product.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator validator;

    public UserService(UserRepository userRepository, UserValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public Optional<UserDTO> findById(Long id) {
        log.info("Buscando usuário por id: {}", id);
        Optional<UserDTO> result = userRepository.findById(id).map(UserMapper::toDTO);

        return result;
    }

    public Optional<UserDTO> findByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        Optional<UserDTO> result = userRepository.findByEmail(email)
                .map(UserMapper::toDTO);

        return result;
    }

    public UserDTO save(UserDTO dto) {
        log.info("Salvando usuário: {}", dto);
        ValidationResult validation = validator.validate(dto);

        if (!validation.isValid()) {
            log.error("Falha na validação ao salvar usuário: {}", validation.getErrors());
            throw new ValidationException(validation);
        }

        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            if (u.getId() != null && !u.getId().equals(dto.getId())) {
                log.error("Email já cadastrado para outro usuário: {}", dto.getEmail());
                throw new EmailAlreadyExistsException("E-mail já cadastrado.");
            }
        });

        User entity = UserMapper.toEntity(dto);
        UserDTO saved = UserMapper.toDTO(userRepository.save(entity));
        log.info("Usuário salvo com sucesso: {}", saved);

        return saved;
    }
}
