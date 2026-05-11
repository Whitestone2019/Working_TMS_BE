package whitestone.trainee_management.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import whitestone.trainee_management.models.*;
import whitestone.trainee_management.repository.*;

@Service
public class SyllabusFeedbackService {

    private final SyllabusFeedbackRepository feedbackRepo;
    private final UserRepository userRepo;
    private final SyllabusRepository syllabusRepo;

    public SyllabusFeedbackService(
            SyllabusFeedbackRepository feedbackRepo,
            UserRepository userRepo,
            SyllabusRepository syllabusRepo) {

        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
        this.syllabusRepo = syllabusRepo;
    }

    // ================= TRAINEE FEEDBACK =================
    @Transactional
    public SyllabusFeedback submitTraineeFeedback(
            String traineeId,
            String trainerId,
            Long syllabusId,
            String feedbackText) {

        User trainee = userRepo
                .findByTrngidAndDelFlag(traineeId, "N")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        User trainer = userRepo
                .findByTrngidAndDelFlag(trainerId, "N")
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        Syllabus syllabus = syllabusRepo.findById(syllabusId)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        SyllabusFeedback feedback = feedbackRepo
                .findByTrainee_TrngidAndTrainer_TrngidAndSyllabus_Id(
                        traineeId, trainerId, syllabusId)
                .orElse(new SyllabusFeedback());

        feedback.setTrainee(trainee);
        feedback.setTrainer(trainer);
        feedback.setSyllabus(syllabus);
        feedback.setTraineeFeedback(feedbackText);
        feedback.setFeedbackGivenTrainee(true);

        return feedbackRepo.save(feedback);
    }

    
    @Transactional
    public SyllabusFeedback submitTrainerFeedback(
            String traineeId,
            String trainerId,
            Long syllabusId,
            String feedbackText) {

        SyllabusFeedback feedback = feedbackRepo
                .findByTrainee_TrngidAndTrainer_TrngidAndSyllabus_Id(
                        traineeId, trainerId, syllabusId)
                .orElseGet(() -> {
                    SyllabusFeedback newFeedback = new SyllabusFeedback();
                    newFeedback.setTrainee(userRepo.findByTrngid(traineeId).orElseThrow());
                    newFeedback.setTrainer(userRepo.findByTrngid(trainerId).orElseThrow());
                    newFeedback.setSyllabus(syllabusRepo.findById(syllabusId).orElseThrow());
                    return newFeedback;
                });

        feedback.setTrainerFeedback(feedbackText);
        feedback.setFeedbackGivenTrainer(true);

        return feedbackRepo.save(feedback);
    }


    // ================= GET FEEDBACK =================
    public Optional<SyllabusFeedback> getFeedback(
            String traineeId,
            String trainerId,
            Long syllabusId) {

        return feedbackRepo
                .findByTrainee_TrngidAndTrainer_TrngidAndSyllabus_Id(
                        traineeId, trainerId, syllabusId);
    }

    // ================= RESET FEEDBACK =================
    @Transactional
    public void resetFeedbackForSyllabus(Long syllabusId) {

        var feedbackList = feedbackRepo.findAll();

        feedbackList.stream()
                .filter(f -> f.getSyllabus().getId().equals(syllabusId))
                .forEach(f -> {
                    f.setFeedbackGivenTrainee(false);
                    f.setFeedbackGivenTrainer(false);
                    f.setTraineeFeedback(null);
                    f.setTrainerFeedback(null);
                });

        feedbackRepo.saveAll(feedbackList);
    }
    
 // ================= GET ASSIGNED SYLLABUS WITH FEEDBACK =================
    public List<Map<String, Object>> getAssignedSyllabusWithFeedback(
            String traineeId,
            String managerId) {

        //  Validate trainee
        User trainee = userRepo
                .findByTrngidAndDelFlag(traineeId, "N")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        //  Validate manager
        User manager = userRepo
                .findByTrngidAndDelFlag(managerId, "N")
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        //  Get only assigned syllabus of trainee
        List<Syllabus> assignedSyllabusList =
                syllabusRepo.findSyllabusByTraineeDepartmentsNative(traineeId);

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Syllabus syllabus : assignedSyllabusList) {

            Map<String, Object> map = new HashMap<>();
            map.put("syllabusId", syllabus.getId());
            map.put("syllabusName", syllabus.getTitle());

            Optional<SyllabusFeedback> feedbackOpt =
                    feedbackRepo.findByTrainee_TrngidAndTrainer_TrngidAndSyllabus_Id(
                            traineeId,
                            managerId,
                            syllabus.getId());

            if (feedbackOpt.isPresent()
                    && feedbackOpt.get().getFeedbackGivenTrainee()) {

                map.put("feedbackGiven", true);
                map.put("feedbackText",
                        feedbackOpt.get().getTraineeFeedback());
            } else {
                map.put("feedbackGiven", false);
                map.put("feedbackText", null);
            }

            responseList.add(map);
        }

        return responseList;
    }

 // ================= GET ASSIGNED SYLLABUS BY TRAINEE =================
    public List<Map<String, Object>> getAssignedSyllabusByTrainee(
            String traineeId) {

        userRepo.findByTrngidAndDelFlag(traineeId, "N")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Syllabus> assignedSyllabus =
                syllabusRepo.findSyllabusByTraineeDepartmentsNative(traineeId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Syllabus syllabus : assignedSyllabus) {

            Map<String, Object> map = new HashMap<>();
            map.put("syllabusId", syllabus.getId());
            map.put("syllabusName", syllabus.getTitle());

            response.add(map);
        }

        return response;
    }
    
 // ================= GET FEEDBACK BY TRAINEE + SYLLABUS =================
    public List<Map<String, Object>> getFeedbackByTraineeAndSyllabus(
            String traineeId,
            Long syllabusId) {

        userRepo.findByTrngidAndDelFlag(traineeId, "N")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<SyllabusFeedback> feedbackList =
                feedbackRepo.findByTrainee_TrngidAndSyllabus_Id(
                        traineeId,
                        syllabusId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (SyllabusFeedback feedback : feedbackList) {

            Map<String, Object> map = new HashMap<>();

            map.put("trainerId",
                    feedback.getTrainer().getTrngid());
            map.put("trainerName",
                    feedback.getTrainer().getUsername());

            map.put("traineeFeedback",
                    feedback.getTraineeFeedback());

            map.put("trainerFeedback",
                    feedback.getTrainerFeedback());

            map.put("traineeFeedbackGiven",
                    feedback.getFeedbackGivenTrainee());

            map.put("trainerFeedbackGiven",
                    feedback.getFeedbackGivenTrainer());

            response.add(map);
        }

        return response;
    }
    
 // ================= GET ONLY TRAINEE FEEDBACK BY SYLLABUS =================
    public List<Map<String, Object>> getOnlyTraineeFeedback(
            String traineeId,
            Long syllabusId) {

        //  Validate trainee
        userRepo.findByTrngidAndDelFlag(traineeId, "N")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        //  Validate syllabus
        syllabusRepo.findById(syllabusId)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        //  Get feedback for specific trainee + syllabus
        List<SyllabusFeedback> feedbackList =
                feedbackRepo.findByTrainee_TrngidAndSyllabus_Id(
                        traineeId,
                        syllabusId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (SyllabusFeedback feedback : feedbackList) {

            if (feedback.getFeedbackGivenTrainee()) {

                Map<String, Object> map = new HashMap<>();

                map.put("syllabusId",
                        feedback.getSyllabus().getId());

                map.put("syllabusName",
                        feedback.getSyllabus().getTitle());

                map.put("traineeFeedback",
                        feedback.getTraineeFeedback());
                
                map.put("trainerFeedback",
                        feedback.getTrainerFeedback());

                map.put("trainerName",
                        feedback.getTrainer().getUsername());
                
                map.put("traineeFeedbackGiven",
                        feedback.getFeedbackGivenTrainee());

                map.put("trainerFeedbackGiven",
                        feedback.getFeedbackGivenTrainer());

                response.add(map);
            }
        }

        return response;
    }



}
