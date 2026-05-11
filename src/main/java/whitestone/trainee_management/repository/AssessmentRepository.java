package whitestone.trainee_management.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import whitestone.trainee_management.models.*;
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
	 List<Assessment> findByDepartmentIdsContaining(Long departmentId);
	 
	 List<Assessment> findByDepartmentIdsIn(List<Long> departmentIds);
	 
	 @Query("SELECT DISTINCT a FROM Assessment a JOIN a.departmentIds d JOIN a.syllabusIds s " +
		       "WHERE d IN :deptIds AND s = :syllabusId")
		List<Assessment> findMatchingAssessments(List<Long> deptIds, Long syllabusId);
}
