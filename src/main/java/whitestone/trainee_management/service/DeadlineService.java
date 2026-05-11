

package whitestone.trainee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whitestone.trainee_management.models.*;
import whitestone.trainee_management.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DeadlineService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SyllabusRepository syllabusRepository;

    @Autowired
    private SyllabusDeadlineStatusRepository deadlineRepository;

    @Autowired
    private StepProgressRepository stepProgressRepository;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private JavaMailSender mailSender;


    //  Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkSyllabusDeadlines() {

        System.out.println("Scheduler Running at: " + LocalDateTime.now());

        List<User> trainees = userRepository.findByRole_IsManagerFalse();

        for (User trainee : trainees) {

            if (trainee.getCreatedAt() == null) continue;

            processTrainee(trainee);
        }
    }


    // ================= TRAINEE PROCESSOR =================

    private void processTrainee(User trainee) {

        LocalDate traineeStartDate =
                trainee.getCreatedAt().toLocalDate();

        List<Syllabus> syllabusList =
                syllabusRepository.findSyllabusByTraineeDepartmentsNative(
                        trainee.getTrngid()
                );

        LocalDate currentStartDate = traineeStartDate;

        for (Syllabus syllabus : syllabusList) {

            boolean completed =
                    isSyllabusCompleted(trainee, syllabus);

            if (completed) {

                resetIfPreviouslyDelayed(trainee, syllabus);

                currentStartDate = currentStartDate
                        .plusDays(syllabus.getDurationInDays())
                        .plusDays(1);

                continue;
            }

            handleIncompleteSyllabus(
                    trainee,
                    syllabus,
                    currentStartDate
            );

            break; 
        }
    }


    private boolean isSyllabusCompleted(User trainee, Syllabus syllabus) {

        long totalSubtopics =
                subTopicRepository.countTotalSubtopics(
                        syllabus.getId()
                );

        if (totalSubtopics == 0) return false;

        long approvedSubtopics =
                stepProgressRepository.countCompletedAndCheckedSubtopics(
                        trainee.getTrngid(),
                        syllabus.getId()
                );

        System.out.println("Total: " + totalSubtopics);
        System.out.println("Approved: " + approvedSubtopics);

        return approvedSubtopics == totalSubtopics;
    }
   

    private void resetIfPreviouslyDelayed(User trainee,
                                          Syllabus syllabus) {

        Optional<SyllabusDeadlineStatus> existing =
                deadlineRepository.findByTraineeIdAndSyllabusId(
                        trainee.getTrngid(),
                        syllabus.getId()
                );

        if (existing.isPresent()) {

            SyllabusDeadlineStatus status = existing.get();

            status.setDelayDays(0);
            status.setMailSent(false);
            status.setLastMailSentAt(null);

            deadlineRepository.saveAndFlush(status);

            System.out.println("Delay reset to 0 for trainee: "
                    + trainee.getTrngid());
        }
    }
   
    private void handleIncompleteSyllabus(
            User trainee,
            Syllabus syllabus,
            LocalDate currentStartDate
    ) {

        LocalDate deadlineDate =
                currentStartDate.plusDays(
                        syllabus.getDurationInDays()
                );

        long delay =
                ChronoUnit.DAYS.between(
                        deadlineDate,
                        LocalDate.now()
                );

        if (delay < 0) delay = 0;

        SyllabusDeadlineStatus status =
                deadlineRepository
                        .findByTraineeIdAndSyllabusId(
                                trainee.getTrngid(),
                                syllabus.getId()
                        )
                        .orElseGet(() -> {
                            SyllabusDeadlineStatus newStatus =
                                    new SyllabusDeadlineStatus();
                            newStatus.setTraineeId(trainee.getTrngid());
                            newStatus.setSyllabusId(syllabus.getId());
                            return newStatus;
                        });

        status.setDeadlineDate(deadlineDate.atStartOfDay());
        status.setDelayDays(delay);

//        User trainer = syllabus.getManager();
//        if (trainer != null) {
//            status.setManagerId(trainer.getUserid());
//        }
        
        Set<User> trainers = syllabus.getManagers();

        if (trainers != null && !trainers.isEmpty()) {
            // first trainer store for reference
            User firstTrainer = trainers.iterator().next();
            status.setManagerId(firstTrainer.getUserid());
        }

        //  Important: Agar delay 0 hai toh mailSent reset karo
        if (delay == 0) {
            status.setMailSent(false);
            status.setLastMailSentAt(null);
        }

        if (delay > 0 && !status.isMailSent()) {

            sendDeadlineMail(
                    trainee,
                    syllabus.getManagers(),
                    syllabus,
                    delay
            );

            status.setMailSent(true);
            status.setLastMailSentAt(LocalDateTime.now());
        }

        deadlineRepository.saveAndFlush(status);
    }


    // ================= MAIL SENDING =================

//    private void sendDeadlineMail(User trainee,
//    							Set<User> trainers,
//                                  Syllabus syllabus,
//                                  long delayDays) {
//
//        try {
//
//            String subject =
//                    "Deadline Missed - " + syllabus.getTitle();
//
//            if (set != null && set.getEmailid() != null) {
//
//                String trainerMessage =
//                        "Hello Trainer,\n\n" +
//                        "Your trainee has missed a syllabus deadline.\n\n" +
//                        "Trainee Name: " + trainee.getFirstname() + " " +
//                        trainee.getLastname() + "\n" +
//                        "Trainee ID: " + trainee.getTrngid() + "\n" +
//                        "Syllabus: " + syllabus.getTitle() + "\n" +
//                        "Delayed By: " + delayDays + " days\n\n" +
//                        "Please take necessary action.\n\n" +
//                        "Regards,\nWhitestone TMS System";
//
//                sendMail(set.getEmailid(), subject, trainerMessage);
//            }
//
//            if (trainee.getEmailid() != null) {
//
//                String traineeMessage =
//                        "Hello " + trainee.getFirstname() + ",\n\n" +
//                        "You have missed the deadline for the following syllabus.\n\n" +
//                        "Syllabus: " + syllabus.getTitle() + "\n" +
//                        "Delayed By: " + delayDays + " days\n\n" +
//                        "Please complete it as soon as possible.\n\n" +
//                        "Regards,\nWhitestone TMS System";
//
//                sendMail(trainee.getEmailid(), subject, traineeMessage);
//            }
//
//        } catch (Exception e) {
//            System.out.println("Mail Error: " + e.getMessage());
//        }
//    }

    private void sendDeadlineMail(User trainee,
            Set<User> trainers,
            Syllabus syllabus,
            long delayDays) {

try {

String subject = "Deadline Missed - " + syllabus.getTitle();

//  Send mail to all trainers
if (trainers != null) {

for (User trainer : trainers) {

if (trainer.getEmailid() != null) {

  String trainerMessage =
          "Hello Trainer,\n\n" +
          "Your trainee has missed a syllabus deadline.\n\n" +
          "Trainee Name: " + trainee.getFirstname() + " " +
          trainee.getLastname() + "\n" +
          "Trainee ID: " + trainee.getTrngid() + "\n" +
          "Syllabus: " + syllabus.getTitle() + "\n" +
          "Delayed By: " + delayDays + " days\n\n" +
          "Please take necessary action.\n\n" +
          "Regards,\nWhitestone TMS System";

  sendMail(trainer.getEmailid(), subject, trainerMessage);
}
}
}

//  Mail to trainee
if (trainee.getEmailid() != null) {

String traineeMessage =
  "Hello " + trainee.getFirstname() + ",\n\n" +
  "You have missed the deadline for the following syllabus.\n\n" +
  "Syllabus: " + syllabus.getTitle() + "\n" +
  "Delayed By: " + delayDays + " days\n\n" +
  "Please complete it as soon as possible.\n\n" +
  "Regards,\nWhitestone TMS System";

sendMail(trainee.getEmailid(), subject, traineeMessage);
}

} catch (Exception e) {
System.out.println("Mail Error: " + e.getMessage());
}
}

    private void sendMail(String to,
                          String subject,
                          String text) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("career@whitestones.co.in");
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);

        mailSender.send(mail);
    }
}