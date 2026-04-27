package info.ejava.examples.svc.failsafe.hello;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.http.HttpMethod;
import info.ejava.examples.common.web.WebLoggingFilter;
import jakarta.servlet.Filter;

@SpringBootApplication
public class FailsafeExampleApp {
    
    public static void main(String[] args) {
        SpringApplication.run(FailsafeExampleApp.class, args);
    }

    @Bean 
    public Filter logFilter() {
        return WebLoggingFilter.logFilter();
    }

    @Bean
    @Order(0)
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatchers(cfg->cfg.requestMatchers("/api/anonymous/**", "/api/authn/**"));
		http.authorizeHttpRequests(cfg->cfg.requestMatchers("/api/authn/**").authenticated());
		http.authorizeHttpRequests(cfg->cfg.anyRequest().permitAll());

		http.httpBasic(cfg->{});
		http.csrf(cfg->cfg.disable());
		http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
    }

    /**
	 * The following set of re-direct snippets are from
	 * http://zetcode.com/springboot/https/[Spring Boot HTTPS example]
	 */
	@Bean
	@Profile("redirect") //only enable on demand
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");

				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};

		tomcat.addAdditionalTomcatConnectors(redirectConnector());
		return tomcat;
	}

	private Connector redirectConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		return connector;
	}
}
