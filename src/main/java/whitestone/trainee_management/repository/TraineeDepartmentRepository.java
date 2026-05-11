package whitestone.trainee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.TraineeDepartment;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.models.Department;

public interface TraineeDepartmentRepository 
        extends JpaRepository<TraineeDepartment, Long> {

    List<TraineeDepartment> findByTrainee(User trainee);

    Optional<TraineeDepartment> findByTraineeAndDepartment(User trainee, Department department);
 
    void deleteByTraineeAndDepartment(User trainee, Department department);
    @Query("SELECT td.department.id FROM TraineeDepartment td WHERE td.trainee.trngid = :trngid")
    List<Long> findDepartmentIdsByTraineeTrngid(@Param("trngid") String trngid);
    
    void deleteByDepartment_Id(Long departmentId);

    void deleteByTrainee(User trainee);
}
