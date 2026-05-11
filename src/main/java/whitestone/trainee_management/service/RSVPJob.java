
package whitestone.trainee_management.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import whitestone.trainee_management.models.ScheduleTraineeMap;
import whitestone.trainee_management.repository.ScheduleTraineeMapRepository;

@Component
public class RSVPJob {

    @Autowired
    private ScheduleTraineeMapRepository scheduleTraineeMapRepository;

    @Autowired
    private RSVPService rsvpService;

    @Scheduled(fixedRate = 100000) 
    public void updateAllRSVPs() {
        List<String> eventIds = scheduleTraineeMapRepository.findAll()
                .stream()
                .map(ScheduleTraineeMap::getEventId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        for (String eventId : eventIds) {
            try {
                rsvpService.syncRSVPFromGoogle(eventId);
            } catch (Exception e) {
                System.out.println("Failed to sync RSVP for event: " + eventId + " -> " + e.getMessage()+""+e);
            }
        }
    }
}

