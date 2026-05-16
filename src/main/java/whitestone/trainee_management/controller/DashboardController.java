package whitestone.trainee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import whitestone.trainee_management.models.*;
import org.springframework.web.bind.annotation.*;

import whitestone.trainee_management.repository.SyllabusDeadlineStatusRepository;
import whitestone.trainee_management.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SyllabusDeadlineStatusRepository deadlineRepository;
    
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/delays")
    public List<SyllabusDeadlineStatus> getAllDelays() {
        return deadlineRepository.findAll();
    }

//    @GetMapping("/delays/{traineeId}")
//    public List<SyllabusDeadlineStatus> getTraineeDelays(@PathVariable String traineeId) {
//        return deadlineRepository.findAll()
//                .stream()
//                .filter(d -> d.getTraineeId().equals(traineeId))
//                .toList();
//    }
    @GetMapping("/delays/{traineeId}")
    public List<SyllabusDeadlineStatus> getTraineeDelays(@PathVariable String traineeId) {
        return deadlineRepository.findValidDelaysByTrainee(traineeId);
    }
    
    @GetMapping("/manager-delays/{managerId}")
    public List<SyllabusDeadlineStatus> getManagerDelays(
            @PathVariable String managerId) {

        return deadlineRepository.findActiveDelays(managerId);
    }
   
}