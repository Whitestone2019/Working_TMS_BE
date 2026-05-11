package whitestone.trainee_management.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import whitestone.trainee_management.models.SyllabusDeadlineStatus;

import java.util.List;
import java.util.Optional;

public interface SyllabusDeadlineStatusRepository
        extends JpaRepository<SyllabusDeadlineStatus, Long> {

    Optional<SyllabusDeadlineStatus>
    findByTraineeIdAndSyllabusId(String traineeId, Long syllabusId);
    
    List<SyllabusDeadlineStatus> findByManagerId(String managerId);

    // Fetch only delayed records (delayDays > 0)
    List<SyllabusDeadlineStatus> findByManagerIdAndDelayDaysGreaterThan(String managerId, long delayDays);
    List<SyllabusDeadlineStatus> 
    findByTraineeIdInAndDelayDaysGreaterThan(List<String> traineeIds, long delayDays);
  
    @Modifying
    @Query("DELETE FROM SyllabusDeadlineStatus s WHERE s.syllabusId = :syllabusId")
    void deleteBySyllabusId(@Param("syllabusId") Long syllabusId);
    
    @Query("""
    	    SELECT DISTINCT s
    	    FROM SyllabusDeadlineStatus s
    	    JOIN Syllabus sy ON sy.id = s.syllabusId
    	    JOIN sy.departments d
    	    JOIN TraineeDepartment td ON td.department.id = d.id
    	    WHERE s.traineeId = :traineeId
    	      AND td.trainee.trngid = s.traineeId
    	""")
    	List<SyllabusDeadlineStatus> findValidDelaysByTrainee(String traineeId);
    
//    @Query("""
//    	    SELECT s
//    	    FROM SyllabusDeadlineStatus s
//    	    WHERE s.managerId = :managerId
//    	      AND s.delayDays > 0
//    	      AND (
//    	          SELECT COUNT(sp)
//    	          FROM StepProgress sp
//    	          WHERE sp.user.trngid = s.traineeId
//    	            AND sp.subTopic.syllabus.id = s.syllabusId
//    	            AND sp.complete = true
//    	            AND sp.checker = true
//    	      ) <
//    	      (
//    	          SELECT COUNT(st)
//    	          FROM SubTopic st
//    	          WHERE st.syllabus.id = s.syllabusId
//    	      )
//    	""")
//    	List<SyllabusDeadlineStatus> findActiveDelays(String managerId);
    
    @Query("""
    	    SELECT s
    	    FROM SyllabusDeadlineStatus s
    	    WHERE s.managerId = :managerId
    	      AND s.delayDays > 0

    	      AND EXISTS (
    	          SELECT td
    	          FROM TraineeDepartment td
    	          JOIN Syllabus sy ON sy.id = s.syllabusId
    	          JOIN sy.departments d
    	          WHERE td.trainee.trngid = s.traineeId
    	            AND td.department.id = d.id
    	      )

    	      AND (
    	          SELECT COUNT(sp)
    	          FROM StepProgress sp
    	          WHERE sp.user.trngid = s.traineeId
    	            AND sp.subTopic.syllabus.id = s.syllabusId
    	            AND sp.complete = true
    	            AND sp.checker = true
    	      ) <
    	      (
    	          SELECT COUNT(st)
    	          FROM SubTopic st
    	          WHERE st.syllabus.id = s.syllabusId
    	      )
    	""")
    	List<SyllabusDeadlineStatus> findActiveDelays(String managerId);
}