package whitestone.trainee_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whitestone.trainee_management.models.*;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    // ✅ Section ke hisaab se answers fetch karne ke liye
    List<QuestionAnswer> findByQuestionId(Long questionId);

}