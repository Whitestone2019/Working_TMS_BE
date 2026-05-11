 package whitestone.trainee_management.repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import whitestone.trainee_management.models.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByTrngidAndDelFlag(String traineeId, String delFlag);
	
	//User findByTrngid(String empid);

    @Query("SELECT u.userid FROM User u")
    Set<String> findAllUserIds();
    
	 List<User> findByDelFlag(String delFlag);
	 
	 List<User> findByRole_IsManagerFalse();  
	 List<User> findByRole_IsManagerTrue();
	 Optional<User> findByTrngid(String trngid);

	 
	 
	 Optional<User> findByUserid(String userid);
	 @Query("SELECT u FROM User u WHERE u.role.roleName = 'MANAGER'")
	    User findManager();
	 
	 @Query("SELECT u FROM User u WHERE u.managerData.userid = :managerId")
	 List<User> findTraineesByManager(String managerId);
	 
	 @Query("SELECT u FROM User u LEFT JOIN FETCH u.managerData WHERE u.role.isManager = false")
	 List<User> findAllTraineesWithManager();

	 @Query("SELECT u FROM User u WHERE u.managerData.userid = :managerId")
	    List<User> findTraineesByManagerId(@Param("managerId") String managerId);
	 
//	 @Query(value = "SELECT " +
//		       "u.trngid as traineeId, " +
//		       "CONCAT(u.firstname, ' ', u.lastname) as name, " +
//		       "u.emailid as email, " +
//		       "ta.current_step as currentStep, " +
//		       "ta.percentage as completionPercentage, " +
//		       "ta.interview_done as interviewStatus, " +
//		       "ta.sub_topics as subTopicsRaw, " +
//		       "CAST(ta.assessment_date AS CHAR) as lastAssessmentDate " +
//		       "FROM users u " +
//		       "LEFT JOIN trainee_assessment_form ta ON u.trngid = ta.trngid " +
//		       "WHERE u.manager_id = :managerId " + 
//		       "AND (ta.assessment_id IS NULL OR ta.assessment_id = (" +
//		       "    SELECT t2.assessment_id FROM trainee_assessment_form t2 " +
//		       "    WHERE t2.trngid = u.trngid " +
//		       "    ORDER BY t2.assessment_date DESC, t2.submitted_at DESC LIMIT 1" +
//		       "))", nativeQuery = true)
//		List<Map<String, Object>> findTraineeSummaryByManager(@Param("managerId") String managerId);
	 
	 @Query(value = "SELECT " +
		       "u.trngid as traineeId, " +
		       "CONCAT(u.firstname, ' ', u.lastname) as name, " +
		       "u.emailid as email, " +
		       "ta.current_step as currentStep, " +
		       "ta.percentage as completionPercentage, " +
		       "ta.interview_done as interviewStatus, " +
		       "ta.sub_topics as subTopicsRaw, " +
		       "CAST(ta.assessment_date AS CHAR) as lastAssessmentDate " +
		       "FROM users u " +
		       "LEFT JOIN trainee_assessment_form ta ON u.trngid = ta.trngid " +
		       "WHERE u.manager_id = :managerId " + 
		       "AND (ta.assessment_id IS NULL OR ta.assessment_id = (" +
		       "    SELECT t2.assessment_id FROM trainee_assessment_form t2 " +
		       "    WHERE t2.trngid = u.trngid " +
		       "    ORDER BY t2.assessment_date DESC, t2.submitted_at DESC LIMIT 1" +
		       "))", nativeQuery = true)
		List<Map<String, Object>> findTraineeSummaryByManager(@Param("managerId") String managerId);
	 
	  
	 
	 
	  @Query("""
		        SELECT u
		        FROM User u
		        WHERE u.delFlag = 'N'
		          AND u.role.isManager = true
		    """)
		    List<User> findAllManagers();
	 
	
	 List<User> findByManagerData_UseridAndDelFlag(String managerUserId, String delFlag);
	 
	 Optional<User> findByUseridAndRole_IsManagerTrueAndDelFlag(
	            String userid,
	            String delFlag
	    );
	 boolean existsByTrngid(String trngid); 
	 @Query("SELECT MAX(CAST(u.userid AS long)) FROM User u")
	 Long findMaxUserId();

	 @Query("SELECT u FROM User u WHERE u.role.isManager = true AND u.role.roleName NOT IN ('CEO','HR','PM','CTO')")
	 List<User> findAllManagersExcludingTopRoles();

	 void deleteByTrngid(String trngid);
	 
	 List<User> findByManagerDataIsNotNullAndDelFlag(String delFlag);
	 
	 List<User> findByManagerData_Userid(String managerId);

	 List<User> findAllByUseridInAndRole_IsManagerTrueAndDelFlag(List<String> userIds, String delFlag);
	 
	 Optional<User> findByUseridAndDelFlag(String userid, String delFlag);

	 
	 List<User> findAllByTrngidInAndDelFlag(List<String> trngids, String delFlag);
	 
	// Optional<User> findByUserid(String userid);
	

}

