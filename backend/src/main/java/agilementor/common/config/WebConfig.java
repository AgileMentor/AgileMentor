package agilementor.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")  // todo: 테스트가 끝나면 https://agilementor.kr로 수정
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .exposedHeaders("Location")
            .maxAge(1800)
            .allowCredentials(true);
    }
}