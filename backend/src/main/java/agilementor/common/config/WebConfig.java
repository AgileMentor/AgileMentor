package agilementor.common.config;

import agilementor.common.resolver.LoginMemberIdArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;

    public WebConfig(LoginMemberIdArgumentResolver loginMemberIdArgumentResolver) {
        this.loginMemberIdArgumentResolver = loginMemberIdArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("https://agilementor.kr", "https://www.agilementor.kr")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .exposedHeaders("Location")
            .maxAge(1800)
            .allowCredentials(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberIdArgumentResolver);
    }

}