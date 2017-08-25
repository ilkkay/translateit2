package translateit2.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectServiceTestConfig {

    /*
     * @Configuration classes ... and may not use @Autowired constructor
     * parameters. Any nested configuration classes must be static
     */

    @Bean
    InitializingBean loadProjectMockData() {
        return () -> {

        };

    }

}