package com.ebs.boardparadice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// uploads 경로 설정을 위한 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 로컬 테스트 시: 아래 경로 사용 (프로젝트 내 static 폴더)
//    public static final String UPLOAD_BASE_PATH = "./src/main/resources/static/uploads/";
    // 프로덕션(서버)에서는 아래 경로로 변경하세요:
    public static final String UPLOAD_BASE_PATH = "/home/ubuntu/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // URL에서 "/uploads/파일명"으로 접근 가능
                .addResourceLocations("file:" + UPLOAD_BASE_PATH);
    }
}