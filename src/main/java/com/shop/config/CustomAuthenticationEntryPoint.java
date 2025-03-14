package com.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 클레스 이름 커스텀 사용자 인증 진입점 <- 인증 진입점을 상속받아서 오버라이드 함.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            // 상속받은 매개변수 중에서 하나라도 에러가 있다면
            // IOException 또는 ServletException 이 발생하고 이는 상위 호출자에게 전파됨

            throws IOException, ServletException {


        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        // 리스팡스(response) 로 Http 서블렛에 에러를 전달함.
    }
}
