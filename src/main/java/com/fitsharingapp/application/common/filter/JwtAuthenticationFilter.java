package com.fitsharingapp.application.common.filter;

import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER_PREFIX;
import static com.fitsharingapp.common.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Set<String> excludedPaths = new HashSet<>();

    static {
        excludedPaths.add("/auth/login");
        excludedPaths.add("/auth/register");
        excludedPaths.add("/auth/reset-password-request");
        excludedPaths.add("/auth/reset-password");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (excludedPaths.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null) {
            throw ServiceException.withFormattedMessage(MISSING_HEADER, AUTHORIZATION_HEADER);
        }
        if (!authorizationHeader.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            throw new ServiceException(INVALID_AUTHORIZATION_HEADER);
        }

        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            throw new ServiceException(INVALID_TOKEN_EXPIRED);
        } catch (MalformedJwtException exception) {
            throw ServiceException.withFormattedMessage(INVALID_TOKEN, exception.getMessage());
        }
    }

}