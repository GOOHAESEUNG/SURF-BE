package com.tavemakers.surf.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
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
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated()) {
            // ✅ 이메일 등 PII 대신 내부 식별자만 기록
            ctx.userId   = resolveUserId(auth);     // e.g. "42"
            ctx.actorRole = resolvePrimaryRole(auth); // e.g. "ROLE_USER"
        } else {
            ctx.userId = null;
            ctx.actorRole = "GUEST";
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

    private String resolveUserId(Authentication auth) {
        Object principal = auth.getPrincipal();

        try {
            var m = principal.getClass().getMethod("getId");
            Object id = m.invoke(principal);
            if (id != null) return String.valueOf(id);
        } catch (Exception ignore) {}

        return null;
    }

    private String resolvePrimaryRole(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null || authorities.isEmpty()) return "USER";
        return authorities.iterator().next().getAuthority();
    }
}