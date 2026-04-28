package com.example.security.service;
import com.example.security.dto.LoginRequestDTO;
import com.example.security.dto.LoginResponseDTO;
import com.example.security.dto.RegisterRequestDTO;
import com.example.security.exception.BadRequestException;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import com.example.security.security.JwtUtil;
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

        // ModelMapper handles:username, password, role
        User user = modelMapper.map(request, User.class);

        userRepository.save(user);
        return "User registered successfully: " + user.getUsername();
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Manual construction: token comes from the jwtutil, not form the user
        return new LoginResponseDTO(token, user.getUsername(), user.getRole());
    }
}