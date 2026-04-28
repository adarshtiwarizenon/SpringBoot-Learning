package com.example.security.config;
import com.example.security.dto.RegisterRequestDTO;
import com.example.security.dto.StudentResponseDTO;
import com.example.security.model.Student;
import com.example.security.model.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(PasswordEncoder passwordEncoder) {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true);

        // Existing mapping for Student → StudentResponseDTO
        mapper.typeMap(Student.class, StudentResponseDTO.class)
                .addMappings(m -> {
                    m.map(src -> src.getCourse().getName(), StudentResponseDTO::setCourseName);
                    m.map(src -> src.getCourse().getCode(), StudentResponseDTO::setCourseCode);
                });

        // NEW: Custom converter for password (hash it during mapping)
        Converter<String, String> passwordHashConverter = ctx ->
                ctx.getSource() == null ? null : passwordEncoder.encode(ctx.getSource());

        // NEW: Custom converter for role (uppercase it during mapping)
        Converter<String, String> roleUppercaseConverter = ctx ->
                ctx.getSource() == null ? null : ctx.getSource().toUpperCase();

        // NEW: Mapping for RegisterRequestDTO → User
        mapper.typeMap(RegisterRequestDTO.class, User.class)
                .addMappings(m -> {
                    m.using(passwordHashConverter)
                            .map(RegisterRequestDTO::getPassword, User::setPassword);
                    m.using(roleUppercaseConverter)
                            .map(RegisterRequestDTO::getRole, User::setRole);
                });

        return mapper;
    }
}