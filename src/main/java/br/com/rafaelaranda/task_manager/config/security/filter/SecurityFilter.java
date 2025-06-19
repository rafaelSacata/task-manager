package br.com.rafaelaranda.task_manager.config.security.filter;

import br.com.rafaelaranda.task_manager.config.security.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public SecurityFilter(TokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        LOGGER.info("Processing request: {} {}", method, path);

        // Skip authentication for public endpoints
        if (path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/error")) {
            LOGGER.debug("Skipping authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        if (token != null) {
            LOGGER.debug("Found token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
            try {
                String login = tokenService.validateToken(token);
                if (login != null && !login.isEmpty()) {
                    LOGGER.info("Token validated for user: {}", login);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(login);
                    if (userDetails == null) {
                        LOGGER.error("UserDetails not found for login: {}", login);
                        response.sendError(HttpStatus.UNAUTHORIZED.value(), "User not found");
                        return;
                    }
                    LOGGER.debug("UserDetails loaded: {}", userDetails.getUsername());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    LOGGER.debug("Authentication set in SecurityContextHolder for user: {}", userDetails.getUsername());
                } else {
                    LOGGER.warn("Token validation returned empty login");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("Error processing token: {}", e.getMessage(), e);
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                return;
            }
        } else {
            LOGGER.warn("No token found in Authorization header for protected endpoint: {}", path);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization token required");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        LOGGER.debug("Authorization header: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.warn("Invalid or missing Authorization header");
            return null;
        }
        String token = authHeader.replace("Bearer ", "").trim();
        LOGGER.debug("Extracted token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        return token;
    }
}