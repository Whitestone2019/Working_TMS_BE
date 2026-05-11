package whitestone.trainee_management.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import whitestone.trainee_management.models.*;
import whitestone.trainee_management.repository.*;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository repo;

    
    @Autowired
    private StepProgressRepository stepRepo;

    @Autowired
    private SyllabusRepository syllabusRepo;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private AssessmentEmailLogRepository emailLogRepo;
    
    @Autowired
    private TraineeAssessmentAttemptService attemptService;
    
    @Autowired
    private TraineeAssessmentAttemptRepository attemptRepository;
    
    @Autowired
	private UserRepository userRepository;
    
    //  SAVE
    public Assessment create(Assessment assessment) {
        return repo.save(assessment);
    }

    //  GET ALL
    public List<Assessment> getAll() {
        return repo.findAll();
    }

    //  DELETE
    public void delete(Long id) {
        repo.deleteById(id);
    }

    //  UPDATE (IMPORTANT)
    public Assessment update(Long id, Assessment newData) {

        Assessment existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        existing.setTitle(newData.getTitle());
        existing.setDepartmentIds(newData.getDepartmentIds());
        existing.setSyllabusIds(newData.getSyllabusIds());

        //  IMPORTANT: replace sections completely
        existing.getSections().clear();
        existing.getSections().addAll(newData.getSections());

        return repo.save(existing);
    }
    
 //  GET BY ID
    public Assessment getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));
    }
    
  

    public void checkAndSendAssessment(String traineeId) {

        List<StepProgress> approvedSteps = stepRepo
                .findByUser_UseridAndCompleteTrueAndCheckerTrue(traineeId);

        if (approvedSteps.isEmpty()) return;

        User user = approvedSteps.get(0).getUser();

     
        if (user.getManagerData() == null) return;

        String email = user.getEmailid();

        Map<Long, List<StepProgress>> syllabusMap = approvedSteps.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSubTopic().getSyllabus().getId()
                ));

        for (Long syllabusId : syllabusMap.keySet()) {

            int totalSubtopics = syllabusRepo.findById(syllabusId)
                    .orElseThrow(() -> new RuntimeException("Syllabus not found"))
                    .getSubTopics()
                    .size();

            int approvedCount = syllabusMap.get(syllabusId).size();

            if (totalSubtopics == approvedCount) {

                //  FAST department fetch
                List<Long> departmentIds = syllabusRepo.findDepartmentIdsBySyllabusId(syllabusId);

                //  ONLY MATCHING assessments
                List<Assessment> assessments = repo
                        .findMatchingAssessments(departmentIds, syllabusId);
                for (Assessment a : assessments) {

                	// ✅ STEP 1: check email already sent
                	boolean alreadyExists = emailLogRepo
                	        .existsByTraineeIdAndAssessmentId(traineeId, a.getId());

                	// ✅ STEP 2: check already submitted
                	boolean alreadySubmitted = attemptService
                	        .hasAlreadySubmitted(traineeId, a.getId());

                	// 🔥 FINAL CONDITION
                	if (alreadyExists || alreadySubmitted) {
                	    System.out.println("Skipping assessment (already sent/submitted): " + a.getId());
                	    continue;
                	}

                    // ✅ STEP 2: save
//                    AssessmentEmailLog log = new AssessmentEmailLog();
//                    log.setTraineeId(traineeId);
//                    log.setAssessmentId(a.getId());
//
//                    emailLogRepo.save(log);
//
//                    // ✅ STEP 3: send email
//                    //emailService.sendAssessmentLink(email, a.getId())
//                    String traineeName = user.getFirstname() + " " + user.getLastname();
//
//                 // 🔥 syllabus fetch karo
//                 String syllabusName = syllabusRepo.findById(syllabusId)
//                         .orElseThrow(() -> new RuntimeException("Syllabus not found"))
//                         .getTitle();
//                 
//                 emailService.sendAssessmentLink(email, traineeName, a.getId(), syllabusName);
                	try {
                	    AssessmentEmailLog log = new AssessmentEmailLog();
                	    log.setTraineeId(traineeId);
                	    log.setAssessmentId(a.getId());

                	    emailLogRepo.save(log); // 👈 agar duplicate hua toh yahin fail hoga

                	    // ✅ ONLY IF SAVE SUCCESS → SEND MAIL
                	    String traineeName = user.getFirstname() + " " + user.getLastname();

                	    String syllabusName = syllabusRepo.findById(syllabusId)
                	            .orElseThrow(() -> new RuntimeException("Syllabus not found"))
                	            .getTitle();

                	    emailService.sendAssessmentLink(email, traineeName, a.getId(), syllabusName);

                	} catch (Exception e) {
                	    System.out.println("Duplicate email prevented for assessment: " + a.getId());
                	}
                }
            }
        }
    }
    
    public List<Assessment> getAssessmentsForTrainee(String traineeId, List<Long> departmentIds) {

        // ✅ 1. Approved steps (complete + checker true)
        List<StepProgress> approvedSteps = stepRepo
                .findByUser_UseridAndCompleteTrueAndCheckerTrue(traineeId);

        if (approvedSteps.isEmpty()) return List.of();

        // ✅ 2. Group by syllabusId
        Map<Long, List<StepProgress>> syllabusMap = approvedSteps.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSubTopic().getSyllabus().getId()
                ));

        // ✅ 3. Fully completed syllabus
        Set<Long> fullyCompletedSyllabusIds = new HashSet<>();

        for (Map.Entry<Long, List<StepProgress>> entry : syllabusMap.entrySet()) {

            Long syllabusId = entry.getKey();

            int totalSubtopics = syllabusRepo.findById(syllabusId)
                    .orElseThrow(() -> new RuntimeException("Syllabus not found"))
                    .getSubTopics()
                    .size();

            int approvedCount = entry.getValue().size();

            if (totalSubtopics == approvedCount) {
                fullyCompletedSyllabusIds.add(syllabusId);
            }
        }

        if (fullyCompletedSyllabusIds.isEmpty()) return List.of();

        // ✅ 4. Get all assessments
        List<Assessment> allAssessments = repo.findAll();

        // ✅ 5. Filter (MULTIPLE DEPARTMENT LOGIC 🔥)
        return allAssessments.stream()
                .filter(a ->

                        // ✔ department match (ANY match)
                        a.getDepartmentIds() != null &&
                        a.getDepartmentIds().stream()
                                .anyMatch(departmentIds::contains)

                        &&

                        // ✔ syllabus match
                        a.getSyllabusIds() != null &&
                        a.getSyllabusIds().stream()
                                .anyMatch(fullyCompletedSyllabusIds::contains)
                )
                .toList();
    }
    
//    public Map<String, Object> getAssessmentSummary(String traineeId, List<Long> departmentIds) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        // ✅ 1. Assigned assessments
//        List<Assessment> assignedAssessments =
//                getAssessmentsForTrainee(traineeId, departmentIds);
//
//        int totalAssigned = assignedAssessments.size();
//
//        if (totalAssigned == 0) {
//            response.put("totalAssigned", 0);
//            response.put("completed", 0);
//            response.put("pending", 0);
//            return response;
//        }
//
//        // ✅ 2. Ek hi baar attempts fetch karo (optimized 🔥)
//        List<TraineeAssessmentAttempt> attempts =
//                attemptRepository.findByTraineeId(traineeId);
//
//        Set<Long> completedIds = attempts.stream()
//                .filter(TraineeAssessmentAttempt::isSubmitted)
//                .map(TraineeAssessmentAttempt::getAssessmentId)
//                .collect(Collectors.toSet());
//
//        // ✅ 3. Count completed
//        int completed = (int) assignedAssessments.stream()
//                .filter(a -> completedIds.contains(a.getId()))
//                .count();
//
//        int pending = totalAssigned - completed;
//
//        // ✅ 4. Final response
//        response.put("totalAssigned", totalAssigned);
//        response.put("completed", completed);
//        response.put("pending", pending);
//
//        return response;
//    }
    
    
    public Map<String, Object> getAssessmentSummary(String userId, List<Long> departmentIds) {

        Map<String, Object> response = new HashMap<>();

        // ✅ 0. userId → traineeId mapping
        String traineeId = userRepository.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Trainee not found"))
                .getTrngid();

        // ✅ 1. Assigned assessments (yaha agar method userId use karta hai to userId hi pass karo)
        List<Assessment> assignedAssessments =
                getAssessmentsForTrainee(userId, departmentIds);

        int totalAssigned = assignedAssessments.size();

        if (totalAssigned == 0) {
            response.put("totalAssigned", 0);
            response.put("completed", 0);
            response.put("pending", 0);
            return response;
        }

        // ✅ 2. Attempts (yaha traineeId use hoga)
        List<TraineeAssessmentAttempt> attempts =
                attemptRepository.findByTraineeId(traineeId);

        Set<Long> completedIds = attempts.stream()
                .filter(TraineeAssessmentAttempt::isSubmitted)
                .map(TraineeAssessmentAttempt::getAssessmentId)
                .collect(Collectors.toSet());

        // ✅ 3. Count completed
        int completed = (int) assignedAssessments.stream()
                .filter(a -> completedIds.contains(a.getId()))
                .count();

        int pending = totalAssigned - completed;

        // ✅ 4. Response
        response.put("totalAssigned", totalAssigned);
        response.put("completed", completed);
        response.put("pending", pending);

        return response;
    }
}