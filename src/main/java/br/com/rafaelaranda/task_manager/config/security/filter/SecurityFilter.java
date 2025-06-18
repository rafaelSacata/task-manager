package br.com.rafaelaranda.task_manager.config.security.filter;

import br.com.rafaelaranda.task_manager.config.security.service.TokenService;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    private final TokenService tokenService;

    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = this.recoverToken(request);
        LOGGER.info("Request URL: {} | HTTP Method: {}", path, request.getMethod());

        if (Objects.nonNull(token)) {
            try {
                String login = tokenService.validateToken(token);

                if (login != null && !login.isEmpty()) {
                    UserDetails userDetails = userRepository.findOneByEmail(login);

                    if (userDetails != null) {
                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        LOGGER.warn("User not found for login: {}", login);
                        response.sendError(HttpStatus.UNAUTHORIZED.value(), "User not found");
                        return;
                    }
                } else {
                    LOGGER.warn("Token validation failed or returned empty login.");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                    return;
                }
            } catch (JWTVerificationException e) {
                LOGGER.warn("Invalid or expired JWT token: {}", e.getMessage());
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                return;
            } catch (Exception e) {
                LOGGER.error("Unexpected error in security filter: {}", e.getMessage(), e);
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        final var authHeader = request.getHeader("Authorization");
        return Objects.isNull(authHeader) ? null : authHeader.replace("Bearer ", "");
    }
}
