package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import whitestone.trainee_management.models.Question;

public interface QuestionRepository
extends JpaRepository<Question,Long>{}