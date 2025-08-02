package com.orvian.travelapi.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Permitir todas as origens
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");

        // Métodos permitidos
        httpResponse.setHeader("Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");

        // Headers permitidos
        httpResponse.setHeader("Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control");

        // Headers expostos
        httpResponse.setHeader("Access-Control-Expose-Headers",
            "Authorization, Content-Disposition");

        // Tempo de cache do preflight
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Para requisições OPTIONS (preflight), retornar OK imediatamente
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Continuar com o filtro
        chain.doFilter(request, response);
    }
}
