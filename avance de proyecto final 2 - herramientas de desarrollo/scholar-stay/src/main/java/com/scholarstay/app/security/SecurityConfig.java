package com.scholarstay.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuración central de Spring Security.
 * Define la protección de rutas, manejo de formularios (login/logout),
 * codificación de contraseñas y otras configuraciones de seguridad.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    /**
     * Define el PasswordEncoder que será utilizado para cifrar contraseñas
     * (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación con nuestro servicio y el codificador
     * de contraseñas.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad (SecurityFilterChain).
     * Define qué rutas son públicas, cuáles requieren autenticación o ciertos
     * roles,
     * y cómo se maneja el login y logout.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de cabeceras de seguridad
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Permite iframes del mismo origen
                        .xssProtection(xss -> xss.disable()) // Deshabilitado en Spring Security 6 por default, se
                                                             // recomienda usar CSP
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com; object-src 'none';")))
                // Configuración de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (recursos estáticos y vistas iniciales)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/login", "/register", "/error").permitAll()
                        // Rutas exclusivas para el rol ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())
                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login") // Especifica nuestra ruta personalizada para el login
                        .loginProcessingUrl("/login") // La ruta a la que el formulario hará el POST (Spring Security lo
                                                      // maneja)
                        .usernameParameter("email")
                        .successHandler(customAuthenticationSuccessHandler) // Utiliza el handler personalizado para redirigir según el rol
                        .failureUrl("/login?error=true") // A dónde redirigir si las credenciales son incorrectas
                        .permitAll())
                // Configuración de logout
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                // Protección CSRF está activa por defecto, solo nos aseguramos de que no se
                // deshabilite
                // Es vital para proteger formularios contra ataques Cross-Site Request Forgery
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/perfil/avatar")) // AJAX upload no usa form CSRF token

                // Manejo de errores de acceso (Access Denied)
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/dashboard?error=access_denied") // Mensaje amigable al intentar entrar sin
                                                                            // permisos
                )
                // Configuración de sesión
                .sessionManagement(session -> session
                        .maximumSessions(1) // Evitar múltiples sesiones simultáneas del mismo usuario
                        .expiredUrl("/login?expired=true"));

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
