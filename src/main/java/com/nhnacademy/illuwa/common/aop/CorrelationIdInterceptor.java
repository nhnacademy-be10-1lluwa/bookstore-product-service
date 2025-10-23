package com.nhnacademy.illuwa.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_LOG_KEY = "correlationId";

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId != null) {
            MDC.put(CORRELATION_ID_LOG_KEY, correlationId);
            log.info("MDC에 Correlation-ID 저장: {}", correlationId);
        } else {
            log.warn("Correlation-ID 헤더가 없습니다!");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
