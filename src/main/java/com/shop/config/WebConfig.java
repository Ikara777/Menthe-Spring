package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/payment**")
                .allowedOrigins("http://localhost:80") // 클라이언트 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }


    @Value("${uploadPath}")
    //  프로퍼티 값(C:/shop/images)을 uploadPath 필드에 주입하여, addResourceHandlers 메서드에서 그 값을 사용할 수 있게 합
    String uploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // Spring MVC가 정적 리소스(이미지, CSS, JS 등)를 어떻게 처리할지 설정하는 부분입니다.
        registry.addResourceHandler("/images/**") //images ** 즉 이미지스 라고 하는 모든 폴더를 핸들링 한다는것
                // /images로 시작하는 경우 uploadPath에 설정한폴더를 기준으로 파일을 읽어오도록 함
                .addResourceLocations(uploadPath);
    }



}