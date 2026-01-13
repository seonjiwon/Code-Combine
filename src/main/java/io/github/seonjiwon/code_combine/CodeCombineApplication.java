package io.github.seonjiwon.code_combine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CodeCombineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeCombineApplication.class, args);
	}

}
