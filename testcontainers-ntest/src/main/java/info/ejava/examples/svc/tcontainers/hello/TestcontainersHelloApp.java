package info.ejava.examples.svc.tcontainers.hello;

import info.ejava.examples.common.web.WebLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class TestcontainersHelloApp {

	public static void main(String[] args) {
		SpringApplication.run(TestcontainersHelloApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}
}
