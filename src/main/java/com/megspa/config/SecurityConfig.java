package com.megspa.config;

import com.megspa.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioDetailsService usuarioDetailsService)
            throws Exception {

        http
            .userDetailsService(usuarioDetailsService)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/masajes",
                    "/registro",
                    "/css/**",
                    "/images/**",
                    "/error"
                ).permitAll()

                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                .requestMatchers("/masajista/**")
                    .hasAnyRole("MASAJISTA", "ADMIN")

                .requestMatchers("/reservas/**")
                    .hasRole("CLIENTE")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", false)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}