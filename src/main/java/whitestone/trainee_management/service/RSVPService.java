package whitestone.trainee_management.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import whitestone.trainee_management.repository.ScheduleTraineeMapRepository;

@Service
public class RSVPService {

    @Autowired 
    private ScheduleTraineeMapRepository scheduleTraineeMapRepository;

    @Autowired
    private GoogleMeetService googleMeetService;

    public void syncRSVPFromGoogle(String eventId) throws Exception {
        // Get latest RSVP statuses from Google Calendar
        List<Map<String, String>> rsvpList = googleMeetService.getRSVPStatus(eventId);
        
        for (Map<String, String> attendee : rsvpList) {
            String emailid = attendee.get("email");
            String status = attendee.get("status");
            
            System.out.println("Email: "+emailid+" stauts: "+status);
            // Update RSVP for this user in DB
            
            scheduleTraineeMapRepository.updateRSVPByEmail(eventId, emailid, status);
        }
    }
}
