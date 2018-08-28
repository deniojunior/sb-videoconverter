package io.sbvideoconverter.sbvideoconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SbVideoconverterApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SbVideoconverterApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SbVideoconverterApplication.class);
	}
}
