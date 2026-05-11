package whitestone.trainee_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import whitestone.trainee_management.models.Syllabus;
import whitestone.trainee_management.models.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

   
    
    public void sendDepartmentAssignedEmail(String toEmail, String traineeName, String departmentName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom("career@whitestones.co.in"); // verified sender
            message.setSubject("New Department Assigned");
            message.setText("Hi " + traineeName + ",\n\n" +
                            "You have been assigned to department: " + departmentName + ".\n" +
                            "Please check your training dashboard for details.\n\n" +
                            "Regards,\nTraining Team");

            mailSender.send(message);
            System.out.println("Email sent to: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email to: " + toEmail + " due to: " + e.getMessage());
        }
    }
    
   

    /** -------------------
     * SEND DEPARTMENT REMOVED EMAIL
     * ------------------- */
    public void sendDepartmentRemovedEmail(String toEmail, String traineeName, String departmentName) {
    	
        String subject = "Department Removed";
        String body = "Hello " + traineeName + ",\n\n"
                + "You have been removed from the department: " + departmentName + ".\n\n"
                + "Regards,\nTraining Management Team";

        sendEmail(toEmail, subject, body);
    }

    
    
    /** -------------------
     * GENERIC EMAIL SENDER
     * ------------------- */
    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("career@whitestones.co.in");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            // Logging framework use kar sakte ho
        }
    }
    
    
   public void sendEmailToTrainer(User trainer, Syllabus syllabus) {
        String subject = "New Syllabus Assigned: " + syllabus.getTitle();
        String body = "Hello " + trainer.getFirstname() + ",\n\n" +
                "A new syllabus has been assigned to you.\n\n" +
                "Title: " + syllabus.getTitle() + "\n" +
                "Topic: " + syllabus.getTopic() + "\n" +
                "Duration: " + syllabus.getDurationInDays() + " days\n\n" +
                "Please login to the system to view details.\n\n" +
                "Regards,\nTraining Team";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("career@whitestones.co.in");
            message.setTo(trainer.getEmailid());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email successfully sent to: " + trainer.getEmailid());
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + trainer.getEmailid());
            e.printStackTrace();
        }
    }
   
   public void sendTrainerAssignedMail(User trainer, Syllabus syllabus) {

	    SimpleMailMessage mail = new SimpleMailMessage();
	    mail.setFrom("career@whitestones.co.in");
	    mail.setTo(trainer.getEmailid());
	    mail.setSubject("New Syllabus Assigned");

	    mail.setText(
	            "Hello " + trainer.getFirstname() + ",\n\n" +
	            "A new syllabus has been assigned to you.\n\n" +
	            "Syllabus: " + syllabus.getTitle() + "\n\n" +
	            "Regards,\nTMS System"
	    );

	    mailSender.send(mail);
	}
   
   public void sendTrainerRemovedMail(User trainer, Syllabus syllabus) {

	    SimpleMailMessage mail = new SimpleMailMessage();
	    mail.setFrom("career@whitestones.co.in");
	    mail.setTo(trainer.getEmailid());
	    mail.setSubject("Syllabus Removed");

	    mail.setText(
	            "Hello " + trainer.getFirstname() + ",\n\n" +
	            "You have been removed from the following syllabus.\n\n" +
	            "Syllabus: " + syllabus.getTitle() + "\n\n" +
	            "Regards,\nTMS System"
	    );

	    mailSender.send(mail);
	}
   
   public void sendSyllabusDeletedMail(User trainer, Syllabus syllabus) {

	    SimpleMailMessage mail = new SimpleMailMessage();
	    mail.setFrom("career@whitestones.co.in");
	    mail.setTo(trainer.getEmailid());
	    mail.setSubject("Syllabus Deleted");

	    mail.setText(
	            "Hello " + trainer.getFirstname() + ",\n\n" +
	            "The following syllabus has been deleted.\n\n" +
	            "Syllabus: " + syllabus.getTitle() + "\n\n" +
	            "Regards,\nTMS System"
	    );

	    mailSender.send(mail);
	}
   
   public void sendSyllabusUpdateEmail(User trainer, Syllabus syllabus) {

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setFrom("career@whitestones.co.in");
	    message.setTo(trainer.getEmailid());
	    message.setSubject("Syllabus Updated");

	    message.setText(
	            "Hello " + trainer.getFirstname() + ",\n\n" +
	            "The syllabus has been UPDATED.\n\n" +
	            "Title: " + syllabus.getTitle() + "\n" +
	            "Topic: " + syllabus.getTopic() + "\n\n" +
	            "Please check the system for updates.\n\n" +
	            "Regards,\nTraining Team"
	    );

	    mailSender.send(message);
	}
   
//   
//   public void sendAssessmentLink(String toEmail, Long assessmentId) {
//
//       //String link = "http://localhost:3000/trainee-test/" + assessmentId;
//	   String link = "http://localhost:3000/login-screen?redirect=/trainee-test/" + assessmentId;
//
//       SimpleMailMessage message = new SimpleMailMessage();
//       message.setFrom("career@whitestones.co.in");
//       message.setTo(toEmail);
//       message.setSubject("Assessment Link");
//
//       message.setText(
//               "Your assessment is ready.\n\n" +
//               "Click below link to start:\n" +
//               link
//       );
//
//       mailSender.send(message);
//   }
   
   public void sendAssessmentLink(String toEmail, String traineeName, 
           Long assessmentId, String syllabusName) {

String link = "http://localhost:3000/login-screen?redirect=/trainee-test/" + assessmentId;

String validity = java.time.LocalDate.now().toString();

SimpleMailMessage message = new SimpleMailMessage();
message.setFrom("career@whitestones.co.in");
message.setTo(toEmail);
message.setSubject("Assessment Invitation – Action Required");

message.setText(
"Dear " + traineeName + ",\n\n" +

"Congratulations! You have successfully completed the syllabus:\n" +
 syllabusName + "\n\n" +

"Based on your progress, you are now eligible to take the assessment.\n\n" +

" Important Instructions:\n" +
"• The assessment is valid for *today only* (" + validity + ").\n" +
"• You can attempt the assessment between *10:00 AM to 4:00 PM*.\n" +
"• Please ensure stable internet connectivity before starting.\n\n" +

"🔗 Assessment Link:\n" + link + "\n\n" +

"We wish you all the best for your assessment.\n\n" +

"Best Regards,\n" +
"Training & Development Team\n" +
"Whitestone"
);

mailSender.send(message);
}
   
   public void sendMultipleDepartmentsEmail(String toEmail, String traineeName, List<String> departments) {
	    try {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(toEmail);
	        message.setFrom("career@whitestones.co.in");
	        message.setSubject("Departments Assigned");

	        String deptList = String.join(", ", departments);

	        message.setText("Hi " + traineeName + ",\n\n"
	                + "You have been assigned to the following departments:\n"
	                + deptList + "\n\n"
	                + "Please check your dashboard.\n\n"
	                + "Regards,\nTraining Team");

	        mailSender.send(message);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
   
   
   
   public void sendMultipleDepartmentsRemovedEmail(String toEmail, String traineeName, List<String> departments) {

	    String subject = "Departments Removed";

	    String deptList = String.join(", ", departments);

	    String body = "Hi " + traineeName + ",\n\n"
	            + "You have been removed from the following departments:\n"
	            + deptList + "\n\n"
	            + "Regards,\nTraining Team";

	    sendEmail(toEmail, subject, body);
	}
}
