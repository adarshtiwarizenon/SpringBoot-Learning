package com.example.Task_Manager.config;
import com.example.Task_Manager.dto.RegisterRequestDTO;
import com.example.Task_Manager.dto.TaskResponseDTO;
import com.example.Task_Manager.model.Task;
import com.example.Task_Manager.model.User;
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

        // Password hash converter
        Converter<String, String> passwordHashConverter = ctx ->
                ctx.getSource() == null ? null : passwordEncoder.encode(ctx.getSource());

        // Role uppercase converter
        Converter<String, String> roleUppercaseConverter = ctx ->
                ctx.getSource() == null ? null : ctx.getSource().toUpperCase();

        // RegisterRequestDTO → User mapping
        mapper.typeMap(RegisterRequestDTO.class, User.class)
                .addMappings(m -> {
                    m.using(passwordHashConverter)
                            .map(RegisterRequestDTO::getPassword, User::setPassword);
                    m.using(roleUppercaseConverter)
                            .map(RegisterRequestDTO::getRole, User::setRole);
                });

        // Task → TaskResponseDTO mapping
        mapper.typeMap(Task.class, TaskResponseDTO.class)
                .addMappings(m -> {
                    m.map(src -> src.getOwner().getUsername(),
                            TaskResponseDTO::setOwnerUsername);
                    m.map(src -> src.getCategory().getName(),
                            TaskResponseDTO::setCategoryName);
                    m.map(src -> src.getCategory().getColor(),
                            TaskResponseDTO::setCategoryColor);
                });

        return mapper;
    }
}