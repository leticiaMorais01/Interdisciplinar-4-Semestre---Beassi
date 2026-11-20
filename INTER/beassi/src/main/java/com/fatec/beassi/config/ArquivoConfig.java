package com.fatec.beassi.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ArquivoConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String diretorioUpload;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    Path caminhoUpload = Paths.get(diretorioUpload)
            .toAbsolutePath()
            .normalize();

    String local = caminhoUpload.toUri().toString();

    if (!local.endsWith("/")) {
        local += "/";
    }

    registry.addResourceHandler("/uploads/**")
            .addResourceLocations(local);
    }


@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                    "http://127.0.0.1:5500",
                    "http://localhost:5500",
                    "http://127.0.0.1:5501",
                    "http://localhost:5501"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}

}
