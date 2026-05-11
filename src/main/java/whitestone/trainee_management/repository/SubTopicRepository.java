package whitestone.trainee_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import whitestone.trainee_management.models.SubTopic;
import whitestone.trainee_management.models.Syllabus;

import java.util.*;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Long> {

    //  Get all subtopics
    List<SubTopic> findAll();

    //  Get subtopics by syllabus id
    List<SubTopic> findBySyllabus_Id(Long syllabusId);

    //List<SubTopic> findByManager_Userid(String trainerId);
    
    List<SubTopic> findBySyllabus_Managers_Userid(String trainerId);


    //  Fetch subtopics with syllabus data (FIX for LAZY issue)
    @Query("""
        SELECT st
        FROM SubTopic st
        JOIN FETCH st.syllabus s
        WHERE st.delFlag = 'N'
    """)
    List<SubTopic> findAllWithSyllabus();

    //  Fetch subtopics by syllabus id with syllabus data
    @Query("""
        SELECT st
        FROM SubTopic st
        JOIN FETCH st.syllabus s
        WHERE s.id = :syllabusId
          AND st.delFlag = 'N'
    """)
    List<SubTopic> findBySyllabusIdWithSyllabus(Long syllabusId);
    
   @Query("""
    SELECT st
    FROM SubTopic st
    JOIN FETCH st.syllabus s
    LEFT JOIN FETCH s.managers t
    WHERE st.delFlag = 'N'
""")
List<SubTopic> findAllWithSyllabusAndManager();
   

    List<SubTopic> findAllByOrderByStepNumberAsc();
    
    Optional<SubTopic> findById(Long id);
    
    List<SubTopic> findBySyllabus(Syllabus syllabus);
    @Modifying
    @Query("DELETE FROM SubTopic st WHERE st.syllabus.id = :syllabusId")
    void deleteBySyllabusId(@Param("syllabusId") Long syllabusId);
    
    @Query("SELECT COUNT(st) FROM SubTopic st WHERE st.syllabus.id = :syllabusId")
    long countTotalSubtopics(@Param("syllabusId") Long syllabusId);
    
}