package com.vendas.system.infra.security;

import com.vendas.system.model.UsuarioModel;
import com.vendas.system.model.UsuarioRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
            response.sendRedirect(savedRequest.getRedirectUrl());
            return;
        }
        UsuarioModel usuario = (UsuarioModel) authentication.getPrincipal();
        String path = usuario.getRole() == UsuarioRole.CLIENTE ? "/catalogoProdutos" : "/dashboard";
        response.sendRedirect(request.getContextPath() + path);
    }
}
