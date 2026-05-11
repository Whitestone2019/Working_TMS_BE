package whitestone.trainee_management.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import whitestone.trainee_management.models.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRoleId(String roleId);
    List<Role> findByIsManagerFalse();
}
