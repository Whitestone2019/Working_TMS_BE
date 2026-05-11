package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import whitestone.trainee_management.models.AssessmentAnswer;

public interface AssessmentAnswerRepository
extends JpaRepository<AssessmentAnswer,Long>{}
