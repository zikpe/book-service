package com.bhattaditya2.book_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

        // Allow internal service-to-service calls
        if (isInternalRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String actualHeaderValue = request.getHeader(gatewayHeaderName);

        if (!expectedHeaderValue.equals(actualHeaderValue)) {
            LOGGER.warn("Blocked request: missing or invalid '{}' header. Path: {}, RemoteAddr: {}",
                    gatewayHeaderName, request.getRequestURI(), request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInternalRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();

        // Allow localhost requests
        if ("127.0.0.1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr) || "localhost".equals(remoteHost)) {
            return true;
        }

        // Allow private IP ranges
        try {
            InetAddress addr = InetAddress.getByName(remoteAddr);
            if (addr.isSiteLocalAddress() || addr.isLoopbackAddress()) {
                return true;
            }
        } catch (UnknownHostException e) {
            LOGGER.debug("Could not parse remote address: {}", remoteAddr);
        }

        // Allow requests with internal service header
        String internalHeader = request.getHeader("X-Internal-Service");
        if ("true".equals(internalHeader)) {
            return true;
        }

        return false;
    }
}