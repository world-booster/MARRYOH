package com.grape;

import com.grape.module.auth.email.SecurityEmailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableConfigurationProperties(SecurityEmailProperties.class)
@SpringBootApplication
@ConfigurationPropertiesScan
public class GrapeApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// 외부 톰캣에서 실행 시 사용할 메인 애플리케이션 클래스 지정
		return builder.sources(GrapeApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(GrapeApplication.class, args);

	}

}
