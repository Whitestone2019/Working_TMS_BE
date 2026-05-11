
package whitestone.trainee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;

import whitestone.trainee_management.models.SubTopic;
import whitestone.trainee_management.models.Syllabus;
import whitestone.trainee_management.service.SyllabusService;
import whitestone.trainee_management.payload.ApiResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/syllabus")
@CrossOrigin(origins = "*")
public class SyllabusController {

    @Autowired
    private SyllabusService syllabusService;

    @Autowired
    private ObjectMapper objectMapper;

    // Add Syllabus
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse addSyllabus(@RequestPart("syllabus") String syllabusJson,
                                   @RequestPart("files") MultipartFile[] files) {
        try {
            Syllabus syllabus = objectMapper.readValue(syllabusJson, Syllabus.class);

            Syllabus saved = syllabusService.saveSyllabus(syllabus, files);

            return new ApiResponse(200, true, "Syllabus created", saved);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, false, "Error: " + e.getMessage(), null);
        }
    }

    // Get All Syllabus
    @GetMapping("/all")
    public ApiResponse getAllSyllabus() {
        try {
            List<Syllabus> list = syllabusService.getAllSyllabus();
            return new ApiResponse(200, true, "Syllabus fetched", list);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, false, "Error: " + e.getMessage(), null);
        }
    }

    //  Get By ID
    @GetMapping("/{id}")
    public ApiResponse getSyllabusById(@PathVariable Long id) {
        try {
            Syllabus syllabus = syllabusService.getSyllabusById(id);
            return new ApiResponse(200, true, "Syllabus fetched", syllabus);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, false, "Error: " + e.getMessage(), null);
        }
    }

    //  Update Syllabus
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse updateSyllabus(
            @PathVariable Long id,
            @RequestPart("syllabus") String syllabusJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        try {
            Syllabus syllabus = objectMapper.readValue(syllabusJson, Syllabus.class);
            Syllabus updated = syllabusService.updateSyllabus(id, syllabus, files);

            return new ApiResponse(200, true, "Syllabus updated", updated);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, false, e.getMessage(), null);
        }
    }

    // Subtopic List
    @GetMapping("/subtopic/all")
    public ApiResponse getAllSubTopics() {
        try {
            List<SubTopic> list = syllabusService.getAllSubTopicsWithSyllabus();
            return new ApiResponse(200, true, "Subtopics fetched", list);
        } catch (Exception e) {
            return new ApiResponse(500, false, e.getMessage(), null);
        }
    }

    // File Preview & download
    @GetMapping("/preview")
    public ResponseEntity<org.springframework.core.io.Resource> previewFile(@RequestParam String path) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();

            if (!Files.exists(filePath)) {
                return ResponseEntity.badRequest().body(null);
            }

            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());
            String filename = filePath.getFileName().toString().toLowerCase();
            String mimeType;

            if (filename.endsWith(".pdf")) {
                mimeType = "application/pdf";
            } else if (filename.endsWith(".docx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (filename.endsWith(".doc")) {
                mimeType = "application/msword";
            } else if (filename.endsWith(".pptx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            } else if (filename.endsWith(".xlsx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                mimeType = Files.probeContentType(filePath);
                if (mimeType == null) mimeType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/file-url")
    public ResponseEntity<?> getPublicFileUrl(@RequestParam String path) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();
            if (!Files.exists(filePath)) {
                return ResponseEntity.badRequest().body("File not found");
            }
            String publicUrl = "http://localhost:8080/uploads/" + path;
            return ResponseEntity.ok(Map.of("url", publicUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/file-base64")
    public ResponseEntity<?> getBase64File(@RequestParam String path) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();
            if (!Files.exists(filePath)) {
                return ResponseEntity.badRequest().body("File not found: " + path);
            }

            byte[] bytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mime = Files.probeContentType(filePath);
            if (mime == null) mime = "application/octet-stream";
            String name = filePath.getFileName().toString();

            return ResponseEntity.ok(
                    Map.of(
                            "data", base64,
                            "mime", mime,
                            "name", name
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reading file: " + e.getMessage());
        }
    }

    @GetMapping("/all-progress")
    public ResponseEntity<List<Map<String, Object>>> getAllSyllabusProgress() {
        return ResponseEntity.ok(
                syllabusService.getAllSyllabusProgress()
        );
    }

    @GetMapping("/all-progress/{empid}")
    public ResponseEntity<List<Map<String, Object>>> getSyllabusByEmpId(
            @PathVariable String empid) {

        return ResponseEntity.ok(
                syllabusService.getSyllabusByEmpId(empid)
        );
    }

    @DeleteMapping("/subtopic/{id}")
    public ResponseEntity<String> deleteSubTopic(@PathVariable("id") Long subTopicId) {
        try {
            syllabusService.deleteSubTopic(subTopicId);
            return ResponseEntity.ok("SubTopic and related data deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/bulk-delete/{id}")
    public ResponseEntity<String> deleteSyllabusWithSubTopics(@PathVariable("id") Long syllabusId) {
        try {
            syllabusService.deleteSyllabusWithSubTopics(syllabusId);
            return ResponseEntity.ok("Syllabus, its subtopics, and related step progress deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/syllabus-progress/{managerId}")
    public ResponseEntity<List<Map<String, Object>>> 
        getSyllabusProgressByManager(@PathVariable String managerId) {

        List<Map<String, Object>> response =
                syllabusService.getAllSyllabusProgress(managerId);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/progress/{trainerUserId}")
    public ResponseEntity<List<Map<String, Object>>> 
        getAllSyllabusProgressByTrainer(@PathVariable String trainerUserId) {

        List<Map<String, Object>> response =
                syllabusService.getAllSyllabusProgress(trainerUserId);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/trainer/{trainerId}")
    public List<Map<String, Object>> getSyllabusByTrainer(@PathVariable String trainerId) {
        return syllabusService.getAllSyllabusProgressByTrainer(trainerId);
    }
    
    
}

