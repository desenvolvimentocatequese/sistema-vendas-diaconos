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

    private static final String[] LOJA_ROLES = {"CLIENTE", "ADMIN", "USER_COMUM", "RESPONSAVEL_SETOR"};
    private static final String[] EQUIPE_ROLES = {"ADMIN", "USER_COMUM", "RESPONSAVEL_SETOR"};

    private final SecurityFilter securityFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleBasedLoginSuccessHandler roleBasedLoginSuccessHandler;

    public SecurityConfiguration(SecurityFilter securityFilter,
                                 CustomUserDetailsService customUserDetailsService,
                                 RoleBasedLoginSuccessHandler roleBasedLoginSuccessHandler) {
        this.securityFilter = securityFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.roleBasedLoginSuccessHandler = roleBasedLoginSuccessHandler;
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
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/h2-console/**",
                                "/actuator/health",
                                "/log",
                                "/log/**",
                                "/dev/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalogoProdutos", "/produto/**", "/index").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN")
                        .requestMatchers("/dashboard", "/produtos/**", "/pedidos/**", "/chamados/**")
                                .hasAnyRole(EQUIPE_ROLES)
                        .requestMatchers(HttpMethod.POST, "/adicionarCarrinho").hasAnyRole(LOJA_ROLES)
                        .requestMatchers(HttpMethod.GET, "/carrinho").hasAnyRole(LOJA_ROLES)
                        .requestMatchers(HttpMethod.POST, "/carrinho/remover").hasAnyRole(LOJA_ROLES)
                        .requestMatchers(HttpMethod.GET, "/checkout").hasAnyRole(LOJA_ROLES)
                        .requestMatchers(HttpMethod.POST, "/finalizarPedido").hasAnyRole(LOJA_ROLES)
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .requestMatchers(HttpMethod.PUT, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .requestMatchers(HttpMethod.PATCH, "/**").hasAnyRole("ADMIN", "USER_COMUM")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/catalogoProdutos?acesso=negado"))
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(roleBasedLoginSuccessHandler)
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
