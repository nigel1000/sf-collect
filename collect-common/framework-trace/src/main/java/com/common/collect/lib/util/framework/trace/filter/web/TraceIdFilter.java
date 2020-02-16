package com.common.collect.lib.util.framework.trace.filter.web;

import com.common.collect.lib.util.framework.trace.TraceIdRequestUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        TraceIdRequestUtil.initTraceId(request);
        filterChain.doFilter(request, response);
    }

}