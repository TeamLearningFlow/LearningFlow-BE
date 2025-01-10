package learningFlow.learningFlow_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LearningFlowBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningFlowBeApplication.class, args);
	}

}
