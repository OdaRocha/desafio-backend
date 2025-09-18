package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.Role;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                Role.from(user.getRole())
        );
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole().name());
        return user;
    }

}
