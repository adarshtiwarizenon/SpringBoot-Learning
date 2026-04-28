package com.example.security.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String welcomeMessage;
    private int maxStudentsPerCourse;

    public String getWelcomeMessage() { return welcomeMessage; }
    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }

    public int getMaxStudentsPerCourse() { return maxStudentsPerCourse; }
    public void setMaxStudentsPerCourse(int maxStudentsPerCourse) {
        this.maxStudentsPerCourse = maxStudentsPerCourse;
    }
}