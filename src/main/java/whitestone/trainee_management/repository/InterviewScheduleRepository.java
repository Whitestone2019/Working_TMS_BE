package whitestone.trainee_management.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import whitestone.trainee_management.models.InterviewSchedule;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {
	
	
}