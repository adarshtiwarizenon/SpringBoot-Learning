package com.example.Task_Manager.service;


import com.example.Task_Manager.dto.LoginRequestDTO;
import com.example.Task_Manager.dto.LoginResponseDTO;
import com.example.Task_Manager.dto.RegisterRequestDTO;
import com.example.Task_Manager.exception.BadRequestException;
import com.example.Task_Manager.model.User;
import com.example.Task_Manager.repository.UserRepository;
import com.example.Task_Manager.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    public String register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }

        User user = modelMapper.map(request, User.class);
        userRepository.save(user);
        return "User registered successfully: " + user.getUsername();
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponseDTO(token, user.getUsername(), user.getRole());
    }
}