package whitestone.trainee_management.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import whitestone.trainee_management.models.Department;
import whitestone.trainee_management.models.User;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByManager(User manager);
   
}
