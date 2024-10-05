package com.fitsharingapp.application.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;

@Component
@Slf4j
public class UserInHeaderValidationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String fsUserIdHeader = httpRequest.getHeader(FS_USER_ID_HEADER);
        if (fsUserIdHeader == null || fsUserIdHeader.isEmpty()) {
            log.error("Header 'fsUserId' is missing");
            throw new ServletException("Header 'fsUserId' is required");
        }

        try {
            UUID fsUserId = UUID.fromString(fsUserIdHeader);
            RequestContextHolder.currentRequestAttributes().setAttribute(FS_USER_ID_HEADER, fsUserId,
                    RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalArgumentException e) {
            log.error("Header 'fsUserId' is not a valid UUID: {}", fsUserIdHeader);
            throw new ServletException("Header 'fsUserId' is not a valid UUID");
        }

        chain.doFilter(request, response);
    }


}
