package eu.mostserene.avogador.codeexecutor;

import eu.mostserene.avogador.codeexecutor.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CodeExecutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeExecutorApplication.class, args);

		Runtime.getRuntime()
				.addShutdownHook(new Thread(() ->
						log.info(LoggerColors.cyan("It has been an honor to play with you tonight")))
				);

		log.info(LoggerColors.success("\n\n\t> CodeExecutor started\n"));
	}

}
