package io.github.seonjiwon.code_combine.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        Info info = new Info()
                .title("Code Combine")
                .description("코딩 테스트 리뷰 플랫폼").version("0.0.1");

        return new OpenAPI()
                .info(info);
    }
}
