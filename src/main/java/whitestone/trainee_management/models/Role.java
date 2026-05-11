package whitestone.trainee_management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends AuditModel {

	@Id
	@Column(name = "role_id", length = 10, nullable = false)
	private String roleId;

	@Column(name = "role_name", length = 50, nullable = false)
	private String roleName;

	@Column(name = "description", length = 100, nullable = false)
	private String description;

	@Column(name = "is_manager")
	private boolean isManager = false; 

	public boolean isManager() {
		return isManager;
	}

	public void setManager(boolean isManager) {
		this.isManager = isManager;
	}

	// Getters and Setters 
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
