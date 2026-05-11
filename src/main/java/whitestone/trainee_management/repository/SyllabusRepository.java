package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import whitestone.trainee_management.models.Syllabus;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
	// SyllabusRepository
	List<Syllabus> findAll();
	//List<Syllabus> findByDepartments_DepartmentIdIn(List<Long> departmentIds);
	@Query(value = """
		    SELECT DISTINCT s.*
		    FROM syllabus s
		    JOIN syllabus_departments sd ON s.id = sd.syllabus_id
		    JOIN trainee_departments td ON sd.department_id = td.department_id
		    JOIN users t ON td.trngid = t.trngid
		    WHERE t.trngid = :trngid
		""", nativeQuery = true)
		List<Syllabus> findSyllabusByTraineeDepartmentsNative(@Param("trngid") String trngid);

	List<Syllabus> findByManagers_Userid(String userid);
	@Modifying
	@Query(value = "DELETE FROM syllabus_departments WHERE department_id = :deptId", nativeQuery = true)
	void deleteSyllabusDepartmentMapping(@Param("deptId") Long deptId);
	
	@Query("SELECT d.id FROM Syllabus s JOIN s.departments d WHERE s.id = :syllabusId")
	List<Long> findDepartmentIdsBySyllabusId(Long syllabusId);
}
