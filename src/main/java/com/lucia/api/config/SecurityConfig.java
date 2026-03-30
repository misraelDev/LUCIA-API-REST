package com.lucia.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucia.api.http.ProblemJson;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.security.JwtAuthenticationFilter;
import com.lucia.api.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================
    // CONFIGURACIÓN CORS
    // ============================================

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                /* Emulador Android: el “localhost” del emulador no es el PC; 10.0.2.2 es el host. */
                "http://10.0.2.2:3000",
                "http://10.0.2.2:3001",
                /* Misma máquina u otra en LAN (útil si abres el front con http://<tu-ip>:3000) */
                "http://192.168.*.*:3000",
                "http://192.168.*.*:3001"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
            Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "http://10.0.2.2:3000",
                "http://10.0.2.2:3001",
                "http://192.168.*.*:3000",
                "http://192.168.*.*:3001"
            )
        );
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(
            Arrays.asList("Authorization", "Content-Type")
        );
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        // Configuración específica para WebSockets
        CorsConfiguration wsConfiguration = new CorsConfiguration();
        wsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));
        wsConfiguration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        wsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        wsConfiguration.setExposedHeaders(
            Arrays.asList("Authorization", "Content-Type")
        );
        wsConfiguration.setAllowCredentials(true);
        wsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/ws/**", wsConfiguration);
        source.registerCorsConfiguration("/api/ws/**", wsConfiguration);

        return source;
    }

    // ============================================
    // CONFIGURACIÓN DE SEGURIDAD Y AUTORIZACIÓN
    // ============================================

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(req ->
                req
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html")
                    .permitAll()
                    // Rutas públicas
                    .requestMatchers("/api/v1/users/signup")
                    .permitAll()
                    .requestMatchers("/api/v1/users/signin")
                    .permitAll()
                    .requestMatchers("/api/v1/users/logout")
                    .permitAll()
                    .requestMatchers("/ws/**")
                    .permitAll()
                    .requestMatchers("/api/v1/users/me/navigation")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/users")
                    .hasAuthority("admin")
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/*")
                    .hasAuthority("admin")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/tenant")
                    .hasAuthority("admin")
                    .requestMatchers(HttpMethod.POST, "/api/v1/calls")
                    .permitAll()
                    .requestMatchers("/api/v1/admin/**")
                    .hasAuthority("admin")
                    // Rutas autenticadas
                    .requestMatchers("/api/v1/users/**")
                    .authenticated()
                    .requestMatchers("/api/v1/stats/**")
                    .hasAnyAuthority("user", "admin", "seller")
                    .requestMatchers("/api/v1/requests/**")
                    .hasAnyAuthority("seller", "admin")
                    .requestMatchers("/api/v1/referrals/**")
                    .hasAnyAuthority("user", "admin", "seller")
                    // Rutas con roles específicos
                    .requestMatchers("/api/v1/calls/**")
                    .hasAnyAuthority("user", "seller")
                    .requestMatchers("/api/v1/contacts/**")
                    .hasAnyAuthority("user", "seller", "admin")
                    .requestMatchers("/api/v1/appointments/**")
                    .hasAnyAuthority("admin", "user", "seller")
                    .requestMatchers("/api/v1/responses/**")
                    .hasAnyAuthority(
                        "admin",
                        "collaborator",
                        "client",
                        "investor",
                        "demo"
                    )
                    // Cualquier otra ruta requiere autenticación
                    .anyRequest()
                    .authenticated()
            )
            .userDetailsService(userDetailsService)
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .exceptionHandling(e ->
                e
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                                writeProblem(response, HttpServletResponse.SC_FORBIDDEN,
                                        ResponseDetail.forbidden(
                                                "No tienes permisos para acceder a este recurso.")))
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                                writeProblem(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        ResponseDetail.unauthorized(
                                                "Token JWT inválido o expirado.")))
            )
            .build();
    }

    // ============================================
    // BEANS DE UTILIDADES
    // ============================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeProblem(HttpServletResponse response, int status, ResponseDetail<Void> body)
            throws IOException {
        response.setStatus(status);
        response.setContentType(ProblemJson.CONTENT_TYPE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
