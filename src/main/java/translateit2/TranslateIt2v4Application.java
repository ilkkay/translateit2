package translateit2;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import translateit2.configuration.DatabaseInitializer;
import translateit2.fileloader.FileLoaderProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileLoaderProperties.class)
public class TranslateIt2v4Application {

    static String[] additionalArgs = { };
    
    public static void main(String[] args) {
       
        String newArgs[] = Arrays.copyOf(args, args.length + additionalArgs.length);
        for (int i = 0 ; i < additionalArgs.length ; i++) newArgs[args.length + i] = additionalArgs[i];

        SpringApplication.run(TranslateIt2v4Application.class, newArgs);
    }
    
    @Autowired @Qualifier("dev")
    private DatabaseInitializer databaseInitializer;

    @PostConstruct
    private void initializeApplication() {
        databaseInitializer.loadBootstrapData();
    }

    /*
     * With CommandLineRunner you can perform tasks after all Spring Beans are
     * created and the Application Context has been created. Used for reading command line parameters.
        @Bean
        public CommandLineRunner demo() {
            return (args) -> {};
        }
     */
}
