package translateit2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/* A popular solution to this problem is the use of Cross-Origin Resource Sharing 
 * (CORS). CORS is a W3C Recommendation, supported by all modern browsers, 
 * that involves a set of procedures and HTTP headers that together allow a 
 * browser to access data (notably Ajax requests) from a site other than the one 
 * from which the current page was served.'*/

// http://chariotsolutions.com/blog/post/angular-2-spring-boot-jwt-cors_part1/

@Configuration
public class RestConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}