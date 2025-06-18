package br.com.rafaelaranda.task_manager.config.security.filter;

import br.com.rafaelaranda.task_manager.config.security.service.TokenService;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;
import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);

        LOGGER.info("Request URL: {} | HTTP Method: {}", request.getRequestURI(), request.getMethod());

        if (Objects.nonNull(token)) {

            if (JWT.decode(token).getExpiresAt().before(new Date())) {

                SecurityContextHolder.getContext().setAuthentication(null);

            } else {
                String login = tokenService.validateToken(token);
                if (login != null && !login.isEmpty()) {

                    UserDetails userDetails = userRepository.findOneByEmail(login);
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } else {
                    logger.warn("Validation failed.");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        final var authHeader = request.getHeader("Authorization");
        return Objects.isNull(authHeader) ? null : authHeader.replace("Bearer ", "");
    }
}
