package whitestone.trainee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.StepProgress;
import whitestone.trainee_management.models.SubTopic;
import whitestone.trainee_management.models.User;
import whitestone.trainee_management.models.*;
import whitestone.trainee_management.repository.StepProgressRepository;
import whitestone.trainee_management.repository.SubTopicRepository;
import whitestone.trainee_management.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class StepProgressService {

    @Autowired
    private StepProgressRepository stepProgressRepository;

    @Autowired
    private UserRepository userRepository;
    	
    @Autowired
    private SubTopicRepository subTopicRepository;
    
    @Autowired
    private AssessmentService assessmentService;

    
    public StepProgress startSubTopic(String empid, Long subtopicId) {

        return stepProgressRepository
                .findByUser_TrngidAndSubTopic_Id(empid, subtopicId)
                .orElseGet(() -> {

                  
                    User user = userRepository
                            .findByTrngidAndDelFlag(empid, "N")
                            .orElseThrow(() -> 
                                new RuntimeException("User not found for empId: " + empid)
                            );
                    SubTopic subTopic = subTopicRepository
                            .findById(subtopicId)
                            .orElseThrow(() ->
                                    new RuntimeException("SubTopic not found with id: " + subtopicId));

                    StepProgress progress = new StepProgress();
                    progress.setUser(user);
                    progress.setSubTopic(subTopic);
                    progress.setStarttimeSeconds(System.currentTimeMillis() / 1000);
                    progress.setStartDateTime(LocalDateTime.now()); 
                    progress.setComplete(false);
                    progress.setChecker(false);

                    return stepProgressRepository.save(progress);
                });
    }

 
    
    public StepProgress completeSubTopic(String empid, Long subtopicId) {

        StepProgress progress = stepProgressRepository
                .findByUser_TrngidAndSubTopic_Id(empid, subtopicId)
                .orElseThrow(() ->
                        new RuntimeException("Progress not found for empid: "
                                + empid + ", subtopicId: " + subtopicId));

        if (progress.isComplete()) {
            return progress;
        }

        long endTime = System.currentTimeMillis() / 1000;
        progress.setEndtimeSeconds(endTime);
        progress.setEndDateTime(LocalDateTime.now());

        if (progress.getStarttimeSeconds() != null) {
            progress.setTimeSpentSeconds(endTime - progress.getStarttimeSeconds());
        }

        progress.setComplete(true);


        // -------- DEADLINE LOGIC START --------

       
        return stepProgressRepository.save(progress);
    }




    
    // GET ALL SUBTOPIC PROGRESS FOR USER
    
    public List<StepProgress> getAllByEmpid(String empid) {
        return stepProgressRepository.findByUser_Trngid(empid);
    }
    
  
    
    // ADMIN / TRAINER USE CASES (OPTIONAL BUT SAFE)
    
//    public StepProgress approveSubTopic(Long progressId, String review) {
//
//        StepProgress progress = stepProgressRepository.findById(progressId)
//                .orElseThrow(() -> new RuntimeException("Progress not found"));
//
//        progress.setChecker(true);
//        progress.setComplete(true);
//
//        if (review != null) {
//            progress.setReview(review);
//        }
//
//        return stepProgressRepository.save(progress);
//    }
     //  ADD TOP ME

  

  public StepProgress approveSubTopic(Long progressId, String review) {

	    StepProgress progress = stepProgressRepository.findById(progressId)
	            .orElseThrow(() -> new RuntimeException("Progress not found"));

	    progress.setChecker(true);
	    progress.setComplete(true);

	    if (review != null) {
	        progress.setReview(review);
	    }

	    StepProgress saved = stepProgressRepository.save(progress);

	    //  ONLY PASS traineeId
	    String traineeId = saved.getUser().getUserid();

	    //assessmentService.checkAndSendAssessment(traineeId);
	    long completedCount = stepProgressRepository
	            .countByUser_UseridAndCompleteTrueAndCheckerTrue(traineeId);

	    long totalCount = stepProgressRepository
	            .countByUser_Userid(traineeId);

	    if (completedCount == totalCount) {
	        assessmentService.checkAndSendAssessment(traineeId);
	    }

	    return saved;
	}

    public StepProgress rejectSubTopic(Long progressId, String review) {

        StepProgress progress = stepProgressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Progress not found"));

        progress.setChecker(false);
        progress.setComplete(false);
        progress.setReview(review);

        return stepProgressRepository.save(progress);
    }
    public List<StepProgress> getCompletedSyllabus() {
	    return stepProgressRepository.findByComplete(true);
	}

    
    public List<Map<String, Object>> getProgressStructuredResponse() {

        List<StepProgress> progressList = stepProgressRepository.findByComplete(true);
        List<Map<String, Object>> response = new ArrayList<>();

        for (StepProgress sp : progressList) {

            SubTopic subTopic = sp.getSubTopic();
            Syllabus syllabus = subTopic.getSyllabus();

            // ---- USER ----
            Map<String, Object> userMap = new LinkedHashMap<>();
            User user = sp.getUser();
            userMap.put("userid", user.getUserid());
            userMap.put("empid", user.getTrngid());
            userMap.put("firstname", user.getFirstname());
            userMap.put("lastname", user.getLastname());
            userMap.put("email", user.getEmailid());
            userMap.put("role", user.getRoleId());
            userMap.put("designation", user.getDesignation());

            // ---- SUBTOPICS ----
            List<Map<String, Object>> subTopicList = new ArrayList<>();

            for (SubTopic st : syllabus.getSubTopics()) {

                Map<String, Object> subTopicMap = new LinkedHashMap<>();
                subTopicMap.put("id", st.getId());
                subTopicMap.put("stepNumber", st.getStepNumber());
                subTopicMap.put("name", st.getName());
                subTopicMap.put("description", st.getDescription());
                subTopicMap.put("filePath", st.getFilePath());

                // ---- TRAINERS from SYLLABUS ----
                List<Map<String, Object>> trainers = new ArrayList<>();

               if (syllabus.getManagers() != null) {
                   for (User tr : syllabus.getManagers()) {
                        Map<String, Object> trainerMap = new LinkedHashMap<>();
                       trainerMap.put("trainerId", tr.getUserid());
                        trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
                        trainerMap.put("title", tr.getRole().getRoleName());
                        trainerMap.put("email", tr.getEmailid());
                       trainers.add(trainerMap);
                   }
               }

                subTopicMap.put("trainers", trainers);
                subTopicList.add(subTopicMap);
           }
                
//                Map<String, Object> trainerMap = null;
//
//                if (syllabus.getManager() != null) {
//
//                    User tr = syllabus.getManager();
//
//                    trainerMap = new LinkedHashMap<>();
//                    trainerMap.put("trainerId", tr.getUserid());
//                    trainerMap.put("name", tr.getFirstname() + " " + tr.getLastname());
//                    trainerMap.put("title", tr.getRole().getRoleName());
//                    trainerMap.put("email", tr.getEmailid());
//                }
//
//                subTopicMap.put("trainer", trainerMap); // single object
//                subTopicList.add(subTopicMap);
//            }

            // ---- SYLLABUS ----
            Map<String, Object> syllabusMap = new LinkedHashMap<>();
            syllabusMap.put("id", syllabus.getId());
            syllabusMap.put("title", syllabus.getTitle());
            syllabusMap.put("topic", syllabus.getTopic());
            syllabusMap.put("durationInDays", syllabus.getDurationInDays());
            syllabusMap.put("subTopics", subTopicList);

            // ---- FINAL STEP PROGRESS ----
            Map<String, Object> stepProgressMap = new LinkedHashMap<>();
            stepProgressMap.put("stepProgressId", sp.getId());
            stepProgressMap.put("complete", sp.isComplete());
            stepProgressMap.put("checker", sp.isChecker());
            stepProgressMap.put("review", sp.getReview());
            stepProgressMap.put("starttimeSeconds", sp.getStarttimeSeconds());
            stepProgressMap.put("endtimeSeconds", sp.getEndtimeSeconds());
            stepProgressMap.put("timeSpentSeconds", sp.getTimeSpentSeconds());
            stepProgressMap.put("startDateTime", sp.getStartDateTime());
            stepProgressMap.put("endDateTime", sp.getEndDateTime());
            

            stepProgressMap.put("user", userMap);
            stepProgressMap.put("syllabus", syllabusMap);

            response.add(stepProgressMap);
        }

        return response;
    }

    

        public List<Map<String, Object>> getUserStepStatus(String empid) {

            //  Fetch all subtopics ordered by step number
            List<SubTopic> subTopics =
                    subTopicRepository.findAllByOrderByStepNumberAsc();

            //  Fetch progress for user
            List<StepProgress> progressList =
                    stepProgressRepository.findByUser_Trngid(empid);

            Map<Long, StepProgress> progressMap = new HashMap<>();
            for (StepProgress sp : progressList) {
                progressMap.put(sp.getSubTopic().getId(), sp);
            }

            //  Build response
            boolean currentAssigned = false;
            List<Map<String, Object>> response = new ArrayList<>();

            for (SubTopic st : subTopics) {

                StepProgress progress = progressMap.get(st.getId());

                Map<String, Object> step = new LinkedHashMap<>();
                step.put("subTopicId", st.getId());
                step.put("stepNumber", st.getStepNumber());
                step.put("title", st.getName());
                step.put("description", st.getDescription());

                //  COMPLETED (checker approved)
                if (progress != null && progress.isComplete() && progress.isChecker()) {
                    step.put("completed", true);
                    step.put("current", false);
                    step.put("locked", false);
                }
                //  CURRENT
                else if (!currentAssigned) {
                    step.put("completed", false);
                    step.put("current", true);
                    step.put("locked", false);
                    currentAssigned = true;
                }
                //  LOCKED
                else {
                    step.put("completed", false);
                    step.put("current", false);
                    step.put("locked", true);
                }

                // Optional progress data
                if (progress != null) {
                    step.put("timeSpentSeconds", progress.getTimeSpentSeconds());
                    step.put("review", progress.getReview());
                }

                response.add(step);
            }

            return response;
        }
    

}