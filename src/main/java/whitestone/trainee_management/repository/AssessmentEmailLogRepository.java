package whitestone.trainee_management.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import whitestone.trainee_management.models.*;

public interface AssessmentEmailLogRepository extends JpaRepository<AssessmentEmailLog, Long> {

    boolean existsByTraineeIdAndAssessmentId(String traineeId, Long assessmentId);
    
    
}