package whitestone.trainee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import whitestone.trainee_management.models.SyllabusFeedback;
import whitestone.trainee_management.models.User;

@Repository
public interface SyllabusFeedbackRepository 
        extends JpaRepository<SyllabusFeedback, Long> {

	Optional<SyllabusFeedback>
	findByTrainee_TrngidAndTrainer_TrngidAndSyllabus_Id(
	        String traineeId,
	        String trainerId,
	        Long syllabusId
	);
	
	List<SyllabusFeedback> 
	findByTrainee_TrngidAndSyllabus_Id(String traineeId, Long syllabusId);

	@Modifying
	@Query("DELETE FROM SyllabusFeedback sf WHERE sf.syllabus.id = :syllabusId")
	void deleteBySyllabusId(@Param("syllabusId") Long syllabusId);
	
	void deleteByTraineeOrTrainer(User trainee, User trainer);

}