//package com.voicera.config;
//
//import java.io.IOException;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.CrossOrigin;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;


// TODO need to implement oauth when ui and backend is deployed in different server so it can share cookie but here getting cors issue




//@Configuration
//public class CookieConfig {
//
//    @Bean
//    public Filter sameSiteCookieFilter() {
//        return new Filter() {
//            @Override
//            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//                    throws IOException, ServletException {
//
//                if (response instanceof HttpServletResponse) {
//                    HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//                    // Inspect and modify cookies in the response header
//                    for (Cookie cookie : ((HttpServletRequest) request).getCookies()) {
//                        if ("JSESSIONID".equals(cookie.getName())) {
//                            // Set SameSite=None and Secure for cross-origin requests
//                            cookie.setHttpOnly(true);
//                            cookie.setSecure(true);
//                            String cookieValue = String.format("%s; SameSite=None; Secure", cookie.getValue());
//                            httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + cookieValue);
//                        }
//                    }
//                }
//                chain.doFilter(request, response);
//            }
//
//            @Override
//            public void init(FilterConfig filterConfig) throws ServletException {}
//
//            @Override
//            public void destroy() {}
//        };
//    }
//}
