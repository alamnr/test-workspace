package info.ejava.examples.svc.authn;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.web.WebLoggingFilter;
import jakarta.servlet.Filter;

@SpringBootApplication
public class AuthConfigExampleApp {
    
    public static void main(String ... args){
        SpringApplication.run(AuthConfigExampleApp.class, args);
        
    }

    @Bean
    public Filter logFilter() {
        return WebLoggingFilter.logFilter();
    }

    
}
