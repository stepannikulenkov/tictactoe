package ru.boycemic.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import ru.boycemic.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {

    private final AuthService authService;
    private final Set<String> excludedPaths = Set.of("/auth/register", "/auth/login");

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        if (excludedPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UUID userId = authService.authenticate(request.getHeader("Authorization"));
            request.setAttribute("userId", userId);

            // Кладём Authentication в контекст, иначе Spring Security отклонит
            // запрос на шаге authorizeHttpRequests(...).anyRequest().authenticated(),
            // даже если сам AuthFilter уже подтвердил учётные данные.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
