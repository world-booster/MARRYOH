package com.grape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
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
