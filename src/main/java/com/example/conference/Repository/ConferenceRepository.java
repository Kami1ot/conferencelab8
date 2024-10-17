package com.example.conference.Repository;

import com.example.conference.Models.Conference;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    @Query("SELECT c FROM Conference c " +
            "WHERE (:conferenceName IS NULL OR LOWER(c.conferenceName) LIKE LOWER(CONCAT('%', :conferenceName, '%'))) " +
            "AND (:moderatorFullName IS NULL OR LOWER(c.moderatorFullName) LIKE LOWER(CONCAT('%', :moderatorFullName, '%'))) " +
            "AND (:leadSpeakerFullName IS NULL OR LOWER(c.leadSpeakerFullName) LIKE LOWER(CONCAT('%', :leadSpeakerFullName, '%'))) " +
            "AND (:startDate IS NULL OR c.conferenceDate >= :startDate) " +
            "AND (:endDate IS NULL OR c.conferenceDate <= :endDate)")
    List<Conference> findByParams(
            @Param("conferenceName") String conferenceName,
            @Param("moderatorFullName") String moderatorFullName,
            @Param("leadSpeakerFullName") String leadSpeakerFullName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Sort sort);

    @Query("SELECT DATE(c.conferenceDate), COUNT(c) FROM Conference c GROUP BY DATE(c.conferenceDate)")
    List<Object[]> findConferenceStats();

    @Query("SELECT COUNT(c) * 1.0 / COUNT(DISTINCT DATE(c.conferenceDate)) FROM Conference c")
    Double findAverageConferencesPerDay();

}
