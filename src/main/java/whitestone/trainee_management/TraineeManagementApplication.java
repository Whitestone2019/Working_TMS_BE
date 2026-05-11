package whitestone.trainee_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class TraineeManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraineeManagementApplication.class, args);
	}	
}

 