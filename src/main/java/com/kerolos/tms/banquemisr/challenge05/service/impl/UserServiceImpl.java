package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.UserResponse;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.mapper.UserMapper;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import com.kerolos.tms.banquemisr.challenge05.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(AdminSignupRequest adminSignupRequest) throws Exception {
        if (userRepository.findByEmail(adminSignupRequest.getEmail()).isPresent()) {
            throw new Exception("User with this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(adminSignupRequest.getPassword());

        User user = this.prepareUser(adminSignupRequest, hashedPassword);

        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserDTO);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public UserResponse updateUser(Long id, AdminSignupRequest updatedUser) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!updatedUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new Exception("User with this email already exists");
        }
        user.setFullName(updatedUser.getFullName());
        user.setEmail(updatedUser.getEmail());
        user.setDateOfBirth(updatedUser.getDateOfBirth());
        user.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private User prepareUser(AdminSignupRequest signupRequest, String hashedPassword) {
        User user = new User();
        user.setFullName(signupRequest.getFullName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setDateOfBirth(signupRequest.getDateOfBirth());
        user.setRole(signupRequest.getRole());
        return user;
    }
}
