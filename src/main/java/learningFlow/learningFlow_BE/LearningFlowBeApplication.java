package learningFlow.learningFlow_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class LearningFlowBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningFlowBeApplication.class, args);
	}

}
