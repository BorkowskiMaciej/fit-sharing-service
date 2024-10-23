package com.fitsharingapp.application.filter;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.domain.user.repository.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.INVALID_UUID_IN_HEADER;
import static com.fitsharingapp.common.ErrorCode.MISSING_FS_USER_ID_HEADER;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserInHeaderValidationFilter extends OncePerRequestFilter {

    private static final Set<String> excludedPaths = new HashSet<>();
    private final UserService userService;

    static {
        excludedPaths.add("/auth/login");
        excludedPaths.add("/auth/register");
        excludedPaths.add("/auth/reset-password-request");
        excludedPaths.add("/auth/reset-password");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (excludedPaths.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String fsUserIdHeader = request.getHeader(FS_USER_ID_HEADER);
        if (fsUserIdHeader == null || fsUserIdHeader.isEmpty()) {
            throw new ServiceException(MISSING_FS_USER_ID_HEADER);
        }

        try {
            UUID fsUserId = UUID.fromString(fsUserIdHeader);
            userService.validateUser(fsUserId, ErrorCode.USER_NOT_FOUND);
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!user.getFsUserId().equals(fsUserId)) {
                throw new ServiceException(ErrorCode.NOT_AUTHENTICATED_USER_IN_HEADER);
            }
            RequestContextHolder.currentRequestAttributes().setAttribute(FS_USER_ID_HEADER, fsUserId,
                    RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalArgumentException e) {
            throw ServiceException.withFormattedMessage(INVALID_UUID_IN_HEADER, FS_USER_ID_HEADER);
        }

        filterChain.doFilter(request, response);
    }

}
