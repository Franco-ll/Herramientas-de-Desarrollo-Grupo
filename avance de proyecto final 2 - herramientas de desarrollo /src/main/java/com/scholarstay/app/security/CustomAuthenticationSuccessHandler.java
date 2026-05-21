package com.scholarstay.app.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Manejador de éxito de autenticación personalizado.
 * Su propósito es interceptar el flujo inmediatamente después de un login exitoso
 * para determinar a qué página debe ser redirigido el usuario basándose en su rol,
 * resolviendo el problema de la redirección estática obligatoria a /dashboard.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        boolean hasAdminRole = false;
        
        // 1. Obtenemos los roles (Authorities) que Spring Security cargó en memoria (vienen de CustomUserDetails)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // 2. Iteramos buscando si el usuario posee el rol 'ROLE_ADMIN'
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                hasAdminRole = true;
                break;
            }
        }

        // 3. Tomamos la decisión de redirección basada en el rol
        if (hasAdminRole) {
            // Si es administrador, lo enviamos de forma forzada a su panel exclusivo
            redirectStrategy.sendRedirect(request, response, "/admin/dashboard");
        } else {
            // Para cualquier otro rol (ej. ROLE_ESTUDIANTE, ROLE_ANFITRION, ROLE_USER), va al dashboard estándar
            redirectStrategy.sendRedirect(request, response, "/dashboard");
        }
    }
}
