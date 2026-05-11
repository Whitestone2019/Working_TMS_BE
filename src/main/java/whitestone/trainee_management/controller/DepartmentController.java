package whitestone.trainee_management.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.models.Department;
import whitestone.trainee_management.models.Syllabus;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.repository.DepartmentRepository;
import whitestone.trainee_management.repository.SyllabusRepository;
import whitestone.trainee_management.repository.TraineeDepartmentRepository;
import whitestone.trainee_management.repository.UserRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/departments")

public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    
    @Autowired
    private TraineeDepartmentRepository traineeDepartmentRepository;

    @Autowired
    private SyllabusRepository syllabusRepository;
    
    //  Create Department
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {

        String managerId = department.getManager().getUserid();

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        department.setManager(manager);

        return ResponseEntity.ok(departmentRepository.save(department));
    }

    //  Get departments by manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Department>> getDepartmentsByManager(
            @PathVariable String managerId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        return ResponseEntity.ok(departmentRepository.findByManager(manager));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department departmentDetails) {
        // Find the existing department
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Update the fields with the new data
        department.setName(departmentDetails.getName());
        //department.setDescription(departmentDetails.getDescription());
        
        // Update manager if present in the request
        if (departmentDetails.getManager() != null) {
            String managerId = departmentDetails.getManager().getUserid();
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            department.setManager(manager);
        }

        // Save the updated department
        Department updatedDepartment = departmentRepository.save(department);
        return ResponseEntity.ok(updatedDepartment);
    }
 
    
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {

        //  trainee_departments delete
        traineeDepartmentRepository.deleteByDepartment_Id(id);

        //  syllabus_departments join table clean
        syllabusRepository.deleteSyllabusDepartmentMapping(id);

        //  department delete
        departmentRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/with-syllabus")
    public ResponseEntity<?> getDepartmentsWithAllSyllabus() {

        List<Department> departments = departmentRepository.findAll();
        List<Syllabus> allSyllabus = syllabusRepository.findAll();

        List<Map<String, Object>> response = departments.stream().map(dept -> {

            List<Map<String, Object>> syllabusList = allSyllabus.stream().map(syl -> {

                boolean assigned = syl.getDepartments() != null &&
                        syl.getDepartments().stream()
                                .anyMatch(d -> d.getId().equals(dept.getId()));

                Map<String, Object> syllabusMap = new HashMap<>();
                syllabusMap.put("id", syl.getId());
                syllabusMap.put("title", syl.getTitle());
                syllabusMap.put("assigned", assigned);

                return syllabusMap;

            }).collect(java.util.stream.Collectors.toList()); 

            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("departmentId", dept.getId());
            deptMap.put("departmentName", dept.getName());
            deptMap.put("syllabus", syllabusList);

            return deptMap;

        }).collect(java.util.stream.Collectors.toList()); 

        return ResponseEntity.ok(response);
    }
}
