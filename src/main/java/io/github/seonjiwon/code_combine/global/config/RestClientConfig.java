package io.github.seonjiwon.code_combine.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${github.base-url}")
    private String baseUrl;

    @Bean
    public RestClient gitHubRestClient() {
        return RestClient.builder()
                         .baseUrl(baseUrl)
                         .build();
    }
}
