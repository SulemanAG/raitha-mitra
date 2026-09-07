package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateUserRequestDto;
import com.raithamitra.backend.dto.request.UpdateUserStatusRequestDto;
import com.raithamitra.backend.dto.response.UserProfileResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Service Interface defining User management business rules, composite profile access, and account status lifecycle.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface UserService {

    UserResponseDto createUser(CreateUserRequestDto requestDto);

    UserResponseDto getUserById(UUID id);

    UserProfileResponseDto getUserProfileById(UUID id);

    UserResponseDto getUserByMobileNumber(String mobileNumber);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUserStatus(UUID id, UpdateUserStatusRequestDto requestDto);

    void deactivateUser(UUID id);
}
