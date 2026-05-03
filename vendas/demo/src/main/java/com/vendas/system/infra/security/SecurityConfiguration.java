package com.vendas.system.infra.security;

import com.vendas.system.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfiguration(SecurityFilter securityFilter, CustomUserDetailsService customUserDetailsService) {
        this.securityFilter = securityFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return httpSecurity
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI() != null ? request.getRequestURI() : "";
                            String path = request.getServletPath() != null ? request.getServletPath() : "";
                            String p = uri.isEmpty() ? path : uri;
                            return p.startsWith("/auth/") || p.contains("/auth/")
                                    || p.startsWith("/h2-console") || p.contains("/h2-console")
                                    || p.startsWith("/actuator/health") || p.contains("/actuator/health")
                                    || p.equals("/log") || p.endsWith("/log")
                                    || p.startsWith("/dev/") || p.contains("/dev/")
                                    || p.equals("/login") || p.startsWith("/css/") || p.startsWith("/js/")
                                    || p.equals("/register") || p.equals("/catalogoProdutos") || p.startsWith("/images/") || p.equals("/index")
                                    || p.startsWith("/produto/") || p.equals("/adicionarCarrinho") || p.equals("/carrinho")
                                    || p.equals("/carrinho/remover") || p.equals("/checkout") || p.equals("/finalizarPedido");
                        }).permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .requestMatchers(HttpMethod.PUT, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .requestMatchers(HttpMethod.PATCH, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> response.sendRedirect("/dashboard"))
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
