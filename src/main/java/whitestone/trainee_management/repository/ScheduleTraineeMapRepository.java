package whitestone.trainee_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.InterviewSchedule;
import whitestone.trainee_management.models.ScheduleTraineeMap;
import whitestone.trainee_management.models.User;

public interface ScheduleTraineeMapRepository extends JpaRepository<ScheduleTraineeMap, Long> {

	@Query("""
			SELECT stm
			FROM ScheduleTraineeMap stm
			
			JOIN FETCH stm.interviewSchedule s
			
			JOIN FETCH stm.user u
			WHERE stm.delFlag = 'N'
			""")

    List<ScheduleTraineeMap> getAllFullSchedules();
	
	List<ScheduleTraineeMap> findByDelFlag(String delFlag);

	

	
	 List<ScheduleTraineeMap> findByInterviewScheduleScheduleId(Long scheduleId);
	 
	 
	 // Fetch all event IDs that are not null
	    @Query("SELECT DISTINCT s.eventId FROM ScheduleTraineeMap s WHERE s.eventId IS NOT NULL")
	    List<String> findAllEventIds();

	    // Update RSVP status
	    @Modifying
	    @Transactional
	    @Query("UPDATE ScheduleTraineeMap s SET s.rsvpStatus = :status WHERE s.eventId = :eventId")
	    void updateRSVPForEvent(String eventId, String status);

	    @Modifying
	    @Transactional
	    @Query("UPDATE ScheduleTraineeMap s SET s.rsvpStatus = :status WHERE s.eventId = :eventId AND s.user.emailid = :emailid")
	    void updateRSVPByEmail(String eventId, String emailid, String status);
	    
	    

	    // Find attendees for a specific event
	    List<ScheduleTraineeMap> findByEventId(String eventId);

	    @Query("SELECT stm FROM ScheduleTraineeMap stm JOIN stm.user u WHERE u.trngid = :trngid AND stm.delFlag = 'N'")
	    List<ScheduleTraineeMap> findByUserTrngId(
	            @Param("trngid") String trngid
	    );

	    boolean existsByInterviewScheduleAndUser(InterviewSchedule schedule, User user);
	    
	    @Modifying
	    @Query("DELETE FROM ScheduleTraineeMap s WHERE s.user.trngid = :trngid")
	    void deleteByTrngid(@Param("trngid") String trngid);
	   
}
