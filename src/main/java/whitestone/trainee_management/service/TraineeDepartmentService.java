package whitestone.trainee_management.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.Department;
import whitestone.trainee_management.models.TraineeDepartment;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.repository.DepartmentRepository;
import whitestone.trainee_management.repository.TraineeDepartmentRepository;
import whitestone.trainee_management.repository.UserRepository;

@Service
@Transactional
public class TraineeDepartmentService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TraineeDepartmentRepository traineeDepartmentRepository;
    private final EmailService emailService;
    

    //  Manual Constructor Injection
    public TraineeDepartmentService(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            TraineeDepartmentRepository traineeDepartmentRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.traineeDepartmentRepository = traineeDepartmentRepository;
        this.emailService = emailService;
    }
    
    @Autowired
    private JavaMailSender mailSender;

    
//    public void assignDepartments(String trngid, List<Long> departmentIds) {
//
//    	User trainee = userRepository
//    	        .findByTrngid(trngid)
//    	        .orElseThrow(() -> new RuntimeException("Trainee not found"));
//
//
//        for (Long deptId : departmentIds) {
//
//            Department department = departmentRepository.findById(deptId)
//                    .orElseThrow(() -> new RuntimeException("Department not found"));
//
//            boolean exists = traineeDepartmentRepository
//                    .findByTraineeAndDepartment(trainee, department)
//                    .isPresent();
//
//            if (!exists) {
//                TraineeDepartment mapping = new TraineeDepartment();
//                mapping.setTrainee(trainee);
//                mapping.setDepartment(department);
//                traineeDepartmentRepository.save(mapping);
//                
//                emailService.sendDepartmentAssignedEmail(
//                        trainee.getEmailid(),
//                        trainee.getFirstname(),
//                        department.getName()
//                );
//            }
//        }
//    }
    
    
    public void assignDepartments(String trngid, List<Long> departmentIds) {

        User trainee = userRepository
                .findByTrngid(trngid)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<String> assignedDeptNames = new ArrayList<>();

        for (Long deptId : departmentIds) {

            Department department = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            boolean exists = traineeDepartmentRepository
                    .findByTraineeAndDepartment(trainee, department)
                    .isPresent();

            if (!exists) {
                TraineeDepartment mapping = new TraineeDepartment();
                mapping.setTrainee(trainee);
                mapping.setDepartment(department);
                traineeDepartmentRepository.save(mapping);

                //  sirf collect karo
                assignedDeptNames.add(department.getName());
            }
        }

        //  ek hi mail bhejo
        if (!assignedDeptNames.isEmpty()) {
            emailService.sendMultipleDepartmentsEmail(
                    trainee.getEmailid(),
                    trainee.getFirstname(),
                    assignedDeptNames
            );
        }
    }


   
    public void removeDepartment(String trngid, Long departmentId) {

    	User trainee = userRepository
    	        .findByTrngid(trngid)
    	        .orElseThrow(() -> new RuntimeException("Trainee not found"));
    	
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        traineeDepartmentRepository.deleteByTraineeAndDepartment(trainee, department);
        emailService.sendDepartmentRemovedEmail(
                trainee.getEmailid(),
                trainee.getFirstname(),
                department.getName()
        );
    }
    
    


//    
//    public void updateTraineeDepartments(String trngid, List<Long> departmentIds) {
//
//    	User trainee = userRepository
//    	        .findByTrngid(trngid)
//    	        .orElseThrow(() -> new RuntimeException("Trainee not found"));
//
//        List<TraineeDepartment> existingMappings =
//                traineeDepartmentRepository.findByTrainee(trainee);
//
//        Set<Long> existingDeptIds = existingMappings.stream()
//                .map(td -> td.getDepartment().getId())
//                .collect(Collectors.toSet());
//
//        Set<Long> newDeptIds = departmentIds.stream().collect(Collectors.toSet());
//
////        //  Remove unselected
//        for (TraineeDepartment td : existingMappings) {
//        	if (!newDeptIds.contains(td.getDepartment().getId())) {
//                traineeDepartmentRepository.delete(td);
//                
//                emailService.sendDepartmentRemovedEmail(
//                        trainee.getEmailid(),
//                        trainee.getFirstname(),
//                        td.getDepartment().getName()
//                );
//            }
//        }
//
//
////        //  Add new
//       for (Long deptId : newDeptIds) {
//           if (!existingDeptIds.contains(deptId)) {
//
//                Department department = departmentRepository.findById(deptId)
//                        .orElseThrow(() -> new RuntimeException("Department not found"));
//
//                TraineeDepartment newMapping = new TraineeDepartment();
//                newMapping.setTrainee(trainee);
//                newMapping.setDepartment(department);
//
//                traineeDepartmentRepository.save(newMapping);
//                emailService.sendDepartmentAssignedEmail(
//                       trainee.getEmailid(),
//                        trainee.getFirstname(),
//                        department.getName()
//                );
//            }
//        }
//    }

    
    public void updateTraineeDepartments(String trngid, List<Long> departmentIds) {

        User trainee = userRepository
                .findByTrngid(trngid)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<TraineeDepartment> existingMappings =
                traineeDepartmentRepository.findByTrainee(trainee);

        Set<Long> existingDeptIds = existingMappings.stream()
                .map(td -> td.getDepartment().getId())
                .collect(Collectors.toSet());

        Set<Long> newDeptIds = new HashSet<>(departmentIds);

        List<String> assignedDeptNames = new ArrayList<>();
        List<String> removedDeptNames = new ArrayList<>();

        //  REMOVE (collect only)
        for (TraineeDepartment td : existingMappings) {
            if (!newDeptIds.contains(td.getDepartment().getId())) {
                traineeDepartmentRepository.delete(td);

                removedDeptNames.add(td.getDepartment().getName());
            }
        }

        //  ADD (collect only)
        for (Long deptId : newDeptIds) {
            if (!existingDeptIds.contains(deptId)) {

                Department department = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new RuntimeException("Department not found"));

                TraineeDepartment newMapping = new TraineeDepartment();
                newMapping.setTrainee(trainee);
                newMapping.setDepartment(department);

                traineeDepartmentRepository.save(newMapping);

                assignedDeptNames.add(department.getName());
            }
        }

        //  SINGLE MAIL FOR ASSIGN
        if (!assignedDeptNames.isEmpty()) {
            emailService.sendMultipleDepartmentsEmail(
                    trainee.getEmailid(),
                    trainee.getFirstname(),
                    assignedDeptNames
            );
        }

        //  SINGLE MAIL FOR REMOVE
        if (!removedDeptNames.isEmpty()) {
            emailService.sendMultipleDepartmentsRemovedEmail(
                    trainee.getEmailid(),
                    trainee.getFirstname(),
                    removedDeptNames
            );
        }
    }
    
    
    public void removeDepartments(String trngid, List<Long> departmentIds) {

        User trainee = userRepository
                .findByTrngid(trngid)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<String> removedDeptNames = new ArrayList<>();

        for (Long deptId : departmentIds) {

            Department department = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            traineeDepartmentRepository.deleteByTraineeAndDepartment(trainee, department);

            removedDeptNames.add(department.getName());
        }

        //  ek hi mail
        if (!removedDeptNames.isEmpty()) {
            emailService.sendMultipleDepartmentsRemovedEmail(
                    trainee.getEmailid(),
                    trainee.getFirstname(),
                    removedDeptNames
            );
        }
    }
    
    public List<Department> getDepartmentsByTrainee(String trngid) {

    	User trainee = userRepository
    	        .findByTrngid(trngid)
    	        .orElseThrow(() -> new RuntimeException("Trainee not found"));

        return traineeDepartmentRepository.findByTrainee(trainee)
                .stream()
                .map(TraineeDepartment::getDepartment)
                .collect(Collectors.toList());
    }
    
}
