package com.bhattaditya2.book_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class GatewayOnlyFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayOnlyFilter.class);

    @Value("${gateway.filter.enabled:true}")
    private boolean filterEnabled;

    @Value("${gateway.filter.header:X-Gateway}")
    private String gatewayHeaderName;

    @Value("${gateway.filter.expected-value:true}")
    private String expectedHeaderValue;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        if (!filterEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String actualHeaderValue = request.getHeader(gatewayHeaderName);

        if (!expectedHeaderValue.equals(actualHeaderValue)) {
            LOGGER.warn("Blocked request: missing or invalid '{}' header. Path: {}",
                    gatewayHeaderName, request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        filterChain.doFilter(request, response);
    }
}