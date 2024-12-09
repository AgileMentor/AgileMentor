package agilementor.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Info info = new Info().title("AgileMentor")
            .description("AgileMentor API 문서")
            .version("0.1.0");

        Server server = new Server()
            .url("https://api.agilementor.kr");

        return new OpenAPI()
            .info(info)
            .servers(List.of(server));
    }
}
