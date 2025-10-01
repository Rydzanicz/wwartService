package com.example.wwartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvLogger {

    private static final Logger logger = LoggerFactory.getLogger(EnvLogger.class);

    @Value("${DB_USERNAME:}")
    private String dbUser;

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @PostConstruct
    public void logEnvVariables() {
        logger.info("DB_USERNAME: {}", dbUser);
        if (dbPassword != null && !dbPassword.isEmpty()) {
        } else {
            logger.warn("DB_PASSWORD is empty or not set");
        }
    }
}
