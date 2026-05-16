package whitestone.trainee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import whitestone.trainee_management.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
