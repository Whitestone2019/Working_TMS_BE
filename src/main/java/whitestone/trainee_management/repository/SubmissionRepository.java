package whitestone.trainee_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import whitestone.trainee_management.models.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssessmentIdAndTraineeId(Long assessmentId, String traineeId);
}