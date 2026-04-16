package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.WebLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class DockerComposeHelloApp {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(args));
		SpringApplication.run(DockerComposeHelloApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}
}
