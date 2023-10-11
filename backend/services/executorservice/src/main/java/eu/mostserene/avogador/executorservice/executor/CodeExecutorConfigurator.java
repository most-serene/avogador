package eu.mostserene.avogador.executorservice.executor;

import eu.mostserene.avogador.executorservice.storage.StorageService;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class CodeExecutorConfigurator {
    @Autowired
    private Environment environment;

    @Autowired
    private StorageService storageService;

    @Bean
    void configure() {
        CodeExecutor.configure(environment, storageService);
        log.info(LoggerColors.success("|-- Docker Configured --|"));
    }
}
