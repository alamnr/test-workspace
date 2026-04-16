package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.WebLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class DockerHelloExampleApp {

	public static void main(String[] args) {
		SpringApplication.run(DockerHelloExampleApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}

	@Order(0)
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatchers(cfg->cfg.requestMatchers("/api/**"));
		http.authorizeHttpRequests(cfg->cfg.anyRequest().permitAll());

		http.httpBasic(Customizer.withDefaults());
		http.csrf(cfg->cfg.disable());
		http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}
}
