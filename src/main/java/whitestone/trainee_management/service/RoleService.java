package whitestone.trainee_management.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import whitestone.trainee_management.repository.RoleRepository;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Map<String, Object>> getAllRoles() {

        return roleRepository.findByIsManagerFalse()   
                .stream()
                .map(role -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("roleId", role.getRoleId());
                    map.put("roleName", role.getRoleName());
                    map.put("description", role.getDescription());
                    map.put("isManager", role.isManager());
                    return map;
                }).toList();
    }
}
