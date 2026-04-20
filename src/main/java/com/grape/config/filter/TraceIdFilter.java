package com.grape.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(TraceIdFilter.class);

    private static final String HEADER = "X-Trace-Id";
    private static final String MDC_KEY = "traceId";

    private static final String EXTERNAL_PREFIX = "EXT-";
    private static final String INTERNAL_PREFIX = "IXT-";

    private static final int MAX_LENGTH = 64;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String traceId = resolveTraceId(request);
        MDC.put(MDC_KEY, traceId);
        request.setAttribute(HEADER, traceId);
        response.setHeader(HEADER, traceId);

        long start = System.currentTimeMillis();

        try{
            filterChain.doFilter(request, response);
        } finally {

            long duration = System.currentTimeMillis() - start;

            logger.info("Request [{} {}] traceId={} duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    traceId,
                    duration);

            MDC.remove(MDC_KEY);
        }

    }

    private String resolveTraceId(HttpServletRequest request){
        String incoming = request.getHeader(HEADER);
        String randomUUID = genereateTraceId();

        if(StringUtils.hasText(incoming)) {
            if(incoming.length() > MAX_LENGTH) {
                return INTERNAL_PREFIX + randomUUID;
            }

            String sanitized = incoming.replaceAll("[\\r\\n]", "").trim();

            return EXTERNAL_PREFIX + sanitized;
        }

        return INTERNAL_PREFIX + randomUUID;
    }

    private String genereateTraceId() {
        return UUID.randomUUID()
                .toString()
                .replace("-","")
                .substring(0,12);
    }
}


