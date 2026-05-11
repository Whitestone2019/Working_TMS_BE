package whitestone.trainee_management.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "Interview Scheduler";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.tokens.dir}")
    private String TOKENS_DIR;

    /* -------------------- AUTH -------------------- */

    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {

        InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("credentials.json");

        if (in == null) {
            throw new IOException("credentials.json not found in resources");
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport,
                        JSON_FACTORY,
                        clientSecrets,
                        Collections.singleton(CalendarScopes.CALENDAR)
                )
                .setDataStoreFactory(
                        new FileDataStoreFactory(new java.io.File(TOKENS_DIR))
                )
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

    /* -------------------- RESPONSE DTO -------------------- */

    public static class MeetEvent {
        private String meetLink;
        private String eventId;

        public MeetEvent(String meetLink, String eventId) {
            this.meetLink = meetLink;
            this.eventId = eventId;
        }

        public String getMeetLink() {
            return meetLink;
        }

        public String getEventId() {
            return eventId;
        }
    }

    /* -------------------- CREATE MEET -------------------- */

    public MeetEvent generateMeetLink(
            String startDateTime,
            String endDateTime,
            List<String> emails
    ) throws GeneralSecurityException, IOException {

    	
    	System.out.println("Sending: "+emails);
        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        Calendar calendarService =
                new Calendar.Builder(
                        httpTransport,
                        JSON_FACTORY,
                        getCredentials(httpTransport)
                )
                .setApplicationName(APPLICATION_NAME)
                .build();

        ZonedDateTime startZdt = ZonedDateTime.parse(startDateTime);
        ZonedDateTime endZdt = ZonedDateTime.parse(endDateTime);

        DateTime start =
                new DateTime(Date.from(startZdt.toInstant()));
        DateTime end =
                new DateTime(Date.from(endZdt.toInstant()));

        Event event = new Event()
                .setSummary("Interview Meeting")
                .setDescription("Auto generated Google Meet interview")
                .setStart(new EventDateTime()
                        .setDateTime(start)
                        .setTimeZone("Asia/Kolkata"))
                .setEnd(new EventDateTime()
                        .setDateTime(end)
                        .setTimeZone("Asia/Kolkata"));

        // Attendees
        List<EventAttendee> attendees = new ArrayList<>();
        for (String email : emails) {
            attendees.add(new EventAttendee().setEmail(email));
        }
        
        event.setAttendees(attendees);
        
        System.out.println("Evetn: "+event);
        // Meet link creation
        ConferenceSolutionKey key =
                new ConferenceSolutionKey().setType("hangoutsMeet");

        CreateConferenceRequest request =
                new CreateConferenceRequest()
                        .setRequestId("meet-" + System.currentTimeMillis())
                        .setConferenceSolutionKey(key);

        event.setConferenceData(
                new ConferenceData().setCreateRequest(request)
        );

        Event createdEvent =
                calendarService.events()
                        .insert("primary", event)
                        .setConferenceDataVersion(1)
                        .setSendUpdates("all")
                        .execute();

        String meetLink = createdEvent.getHangoutLink();

        return new MeetEvent(meetLink, createdEvent.getId());
    }

    /* -------------------- RSVP STATUS -------------------- */

    public List<Map<String, String>> getRSVPStatus(String eventId)
            throws GeneralSecurityException, IOException {

        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        Calendar calendarService =
                new Calendar.Builder(
                        httpTransport,
                        JSON_FACTORY,
                        getCredentials(httpTransport)
                )
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event event =
                calendarService.events()
                        .get("primary", eventId)
                        .execute();
        
        System.out.println("Event: "+event);

        List<Map<String, String>> response = new ArrayList<>();

        if (event.getAttendees() != null) {
            for (EventAttendee attendee : event.getAttendees()) {
                Map<String, String> map = new HashMap<>();
                
                map.put("email", attendee.getEmail());
                map.put("status", attendee.getResponseStatus());
                response.add(map);
            }
        }	

        return response;
    }
}
