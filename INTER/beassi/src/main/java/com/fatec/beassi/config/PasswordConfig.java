package com.fatec.beassi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //identifica a classe como uma classe de configuração.
public class PasswordConfig {

    @Bean//disponibiliza o codificador para ser utilizado por outros componentes do Spring.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //realiza a transformação da senha em um hash seguro.
    }
}

