package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

import java.util.Optional;

import whitestone.trainee_management.models.StepProgress;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.models.SubTopic;

import java.util.List;


public interface StepProgressRepository extends JpaRepository<StepProgress, Long> {

    Optional<StepProgress> findByUser_TrngidAndSubTopic_Id(
        String empid, Long subtopicId
    );

    
    
    List<StepProgress> findByUser_Trngid(String empid);

    List<StepProgress> findByUser_TrngidAndCompleteTrue(String empid);
    List<StepProgress> findByComplete(boolean complete);
    List<StepProgress> findBySubTopic_Syllabus_Id(Long syllabusId);
    
    List<StepProgress> findBySubTopic_Syllabus_IdAndUser_Trngid(
            Long syllabusId,
            String empid
    );

    
    @Transactional
    @Modifying
    @Query("DELETE FROM StepProgress sp WHERE sp.subTopic = :subTopic")
    void deleteBySubTopic(@Param("subTopic") SubTopic subTopic);

    @Query("SELECT sp FROM StepProgress sp WHERE sp.user.userid = :userId AND sp.subTopic.syllabus.id = :syllabusId")
    Optional<StepProgress> findStepProgress(@Param("userId") String userId,
                                            @Param("syllabusId") Long syllabusId);

	 @Query("""
			    SELECT sp FROM StepProgress sp 
			    WHERE sp.user.userid = :userId 
			      AND sp.subTopic.syllabus.id = :syllabusId
			""")
			Optional<StepProgress> findByUserAndSyllabus(String userId, Long syllabusId);


	

	 @Modifying
	 @Transactional
	 @Query("UPDATE StepProgress s SET s.deadlineMailSent = true WHERE s.user.userid = :userId AND s.subTopic.syllabus.id = :syllabusId")
	 int markDeadlineMailSent(@Param("userId") String userId, @Param("syllabusId") Long syllabusId);
	 // Ensure the existence check also uses String
	 boolean existsByUser_UseridAndSubTopic_Syllabus_IdAndCompleteTrue(String userid, Long syllabusId);

	
	// To find existing records
	 List<StepProgress> findByUser_UseridAndSubTopic_Syllabus_Id(String userid, Long syllabusId);

	 // Keep your existing existence check
	 
	boolean existsByUser_UseridAndSubTopic_Syllabus_IdAndDeadlineMailSentTrue(String userid, Long syllabusId);

	List<StepProgress> findBySubTopic_Syllabus_IdAndUser_Userid(Long syllabusId, String userid);
	//List<StepProgress> 
    //findBySubTopic_Syllabus_IdAndUser_Userid(Long syllabusId, String userId);

	 List<StepProgress> findBySubTopic_IdAndUser_Trngid(Long subTopicId, String trngid);
	 
	 
	 /**
	     * Checks if a trainee has completed a syllabus
	     *
	     * @param traineeId Trainee's user ID
	     * @param syllabusId Syllabus ID
	     * @param status Status string (e.g., "COMPLETED")
	     * @return true if any StepProgress record exists with given trainee, syllabus, and status
	     */
	    boolean existsByUser_TrngidAndSubTopic_Syllabus_IdAndCompleteTrue(String traineeId, Long syllabusId);
	    
	    @Query("""
	    		   SELECT COUNT(sp)
	    		   FROM StepProgress sp
	    		   WHERE sp.user.trngid = :trngid
	    		   AND sp.subTopic.syllabus.id = :syllabusId
	    		   AND sp.complete = false
	    		""")
	    		long countIncompleteSubtopics(String trngid, Long syllabusId);
	    
	    
	   
	    
	    @Modifying
	    @Query("DELETE FROM StepProgress sp WHERE sp.subTopic.syllabus.id = :syllabusId")
	    void deleteBySyllabusId(@Param("syllabusId") Long syllabusId);
	    
	    @Query("""
	    		SELECT COUNT(sp)
	    		FROM StepProgress sp
	    		JOIN sp.subTopic st
	    		WHERE sp.user.trngid = :traineeId
	    		AND st.syllabus.id = :syllabusId
	    		AND sp.complete = true
	    		""")
	    		long countCompletedSubtopics(String traineeId, Long syllabusId);
	    
	    @Query("""
	    	    SELECT COUNT(sp)
	    	    FROM StepProgress sp
	    	    WHERE sp.user.trngid = :traineeId
	    	      AND sp.subTopic.syllabus.id = :syllabusId
	    	      AND sp.complete = true
	    	      AND sp.checker = true
	    	""")
	    	long countApprovedSubtopics(String traineeId, Long syllabusId);
	    @Query("SELECT COUNT(sp) FROM StepProgress sp " +
	    	       "JOIN sp.subTopic st " +
	    	       "WHERE sp.user.trngid = :traineeId " + // Yahan sp.user (User object) ke andar ka trngid check ho raha hai
	    	       "AND st.syllabus.id = :syllabusId " +
	    	       "AND sp.complete = true " +            // boolean field hai isliye 'true' use karna better hai
	    	       "AND sp.checker = true")
	    	long countCompletedAndCheckedSubtopics(@Param("traineeId") String traineeId, @Param("syllabusId") Long syllabusId);
	    
	    void deleteByUser_Userid(String userId);
	    
	    void deleteByUser(User user);
	    
	    List<StepProgress> findByUser_UseridAndCompleteTrueAndCheckerTrue(String traineeId);
	    
	   // List<StepProgress> findByUser_UseridAndCompleteTrueAndCheckerTrue(String userId);
	    
	    List<StepProgress> findByUser_UseridAndSubTopic_Id(String string, Long subTopicId);
	    
	    long countByUser_UseridAndCompleteTrueAndCheckerTrue(String traineeId);

	    // ✅ Total steps count
	    long countByUser_Userid(String traineeId);
	    
	    
	    
	    
}



