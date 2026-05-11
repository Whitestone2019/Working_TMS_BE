package whitestone.trainee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import whitestone.trainee_management.models.*;

@Repository
public interface TraineeAssessmentAttemptRepository extends JpaRepository<TraineeAssessmentAttempt, Long> {
	
	Optional<TraineeAssessmentAttempt> findByTraineeIdAndAssessmentId(String traineeId, Long assessmentId);
	
	List<TraineeAssessmentAttempt> findByTraineeIdAndSubmittedTrue(String traineeId);

	@Query("SELECT DISTINCT t.traineeId FROM TraineeAssessmentAttempt t")
	List<String> findDistinctTraineeIds();
	
	Optional<TraineeAssessmentAttempt> findById(Long id);
	
	Optional<TraineeAssessmentAttempt> 
	findByTraineeIdAndAssessmentIdAndSubmittedTrue(String traineeId, Long assessmentId);
	
	  List<TraineeAssessmentAttempt> findByTraineeId(String traineeId);
	  
}

