package com.tavemakers.surf.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class WebLoggingFilter extends OncePerRequestFilter {

    private final LogEventEmitterImpl emitter; // flush 쓰려고 구현체 주입

    public WebLoggingFilter(LogEventEmitterImpl emitter) { this.emitter = emitter; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        long startNs = System.nanoTime();
        RequestLogContext ctx = RequestLogContext.get();

        ctx.requestId = headerOrUuid(req, "X-Request-ID");
        ctx.httpMethod = req.getMethod();
        ctx.path = req.getRequestURI();
        ctx.startAt = Instant.now();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            ctx.userId = auth.getName();
            ctx.actorRole = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("MEMBER");
        } else {
            ctx.userId = null; ctx.actorRole = "GUEST";
        }

        try {
            chain.doFilter(req, res);
        } finally {
            ctx.durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            ctx.status = res.getStatus();
            emitter.flush();
            RequestLogContext.clear();
        }
    }

    private String headerOrUuid(HttpServletRequest req, String key) {
        String v = req.getHeader(key);
        return (v == null || v.isBlank()) ? UUID.randomUUID().toString() : v;
    }
}