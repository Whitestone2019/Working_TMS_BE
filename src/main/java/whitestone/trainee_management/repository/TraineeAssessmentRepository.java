package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import whitestone.trainee_management.models.TraineeAssessment;
import whitestone.trainee_management.models.TraineeAssessmentAttempt;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeAssessmentRepository extends JpaRepository<TraineeAssessment, String> {

    List<TraineeAssessment> findByDelFlag(String delFlag);

    Optional<TraineeAssessment> findByAssessmentIdAndDelFlag(String assessmentId, String delFlag);
   
    List<TraineeAssessment> findByUser_TrngidAndDelFlag(String empid, String delFlag);
    TraineeAssessment findTopByUser_UseridAndDelFlagOrderByUpdatedAtDesc(
            String userid, String delFlag);

    @Modifying
    @Transactional
    @Query("DELETE FROM TraineeAssessment t WHERE t.user.trngid = :trngid")
    void deleteByTrngid(@Param("trngid") String trngid);

	
}
