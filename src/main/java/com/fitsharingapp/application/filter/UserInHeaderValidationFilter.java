package com.fitsharingapp.application.filter;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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
@Order(2)
public class UserInHeaderValidationFilter {

    private static final Set<String> excludedPaths = new HashSet<>();
    private final UserService userService;

    static {
        excludedPaths.add("/users");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getMethod().equalsIgnoreCase("POST") && excludedPaths.contains(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String fsUserIdHeader = httpRequest.getHeader(FS_USER_ID_HEADER);
        if (fsUserIdHeader == null || fsUserIdHeader.isEmpty()) {
            throw new ServiceException(MISSING_FS_USER_ID_HEADER);
        }

        try {
            UUID fsUserId = UUID.fromString(fsUserIdHeader);
            RequestContextHolder.currentRequestAttributes().setAttribute(FS_USER_ID_HEADER, fsUserId,
                    RequestAttributes.SCOPE_REQUEST);
            userService.validateUser(fsUserId, ErrorCode.USER_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw ServiceException.withFormattedMessage(INVALID_UUID_IN_HEADER, FS_USER_ID_HEADER);
        }

        chain.doFilter(request, response);
    }

}
