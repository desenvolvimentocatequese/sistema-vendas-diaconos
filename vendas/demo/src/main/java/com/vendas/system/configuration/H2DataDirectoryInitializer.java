package com.vendas.system.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Profile({"local", "homol", "prod"})
public class H2DataDirectoryInitializer {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @EventListener(ContextRefreshedEvent.class)
    public void garantirDiretorioDados() {
        if (!jdbcUrl.contains("jdbc:h2:file:")) {
            return;
        }
        String pathPart = jdbcUrl.substring(jdbcUrl.indexOf("jdbc:h2:file:") + "jdbc:h2:file:".length());
        int semicolon = pathPart.indexOf(';');
        if (semicolon >= 0) {
            pathPart = pathPart.substring(0, semicolon);
        }
        Path parent = Paths.get(pathPart).getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (Exception ignored) {
            // H2 tentará criar o arquivo; falha aqui não impede o start
        }
    }
}
