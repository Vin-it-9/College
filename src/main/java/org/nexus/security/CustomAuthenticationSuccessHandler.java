package org.nexus.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("Admin".equals(role)) {
                redirectUrl = "/";
                break;
            } else if ("Director".equals(role)) {
                redirectUrl = "/dashboard/director";
                break;
            } else if ("Principal".equals(role)) {
                redirectUrl = "/dashboard/principal";
                break;
            }
//            else if ("LabAssistant".equals(role)) {
//                redirectUrl = "/dashboard/lab-assistant";
//                break;
//            }
//            else if ("Teacher".equals(role)) {
//                redirectUrl = "/dashboard/teacher";
//                break;
//            }
        }
        response.sendRedirect(redirectUrl);
    }
}