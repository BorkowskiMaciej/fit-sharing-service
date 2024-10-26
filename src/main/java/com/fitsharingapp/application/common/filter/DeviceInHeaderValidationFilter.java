package com.fitsharingapp.application.common.filter;

import com.fitsharingapp.application.key.PublicKeyService;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_DEVICE_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceInHeaderValidationFilter extends OncePerRequestFilter {

    private static final Set<String> includedPath = new HashSet<>();
    private final PublicKeyService publicKeyService;

    static {
        includedPath.add("/keys/me");
        includedPath.add("/news/reference");
        includedPath.add("/news/received");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!includedPath.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String deviceIdHeader = request.getHeader(FS_DEVICE_ID_HEADER);
        if (deviceIdHeader == null || deviceIdHeader.isEmpty()) {
            throw ServiceException.withFormattedMessage(MISSING_HEADER, FS_DEVICE_ID_HEADER);
        }

        try {
            UUID deviceId = UUID.fromString(deviceIdHeader);
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            publicKeyService.validateDevice(user.getFsUserId(), deviceId, DEVICE_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw ServiceException.withFormattedMessage(INVALID_UUID_IN_HEADER, FS_DEVICE_ID_HEADER);
        }

        filterChain.doFilter(request, response);
    }

}
